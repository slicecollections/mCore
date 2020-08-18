package tk.slicecollections.maxteer.database;

import tk.slicecollections.maxteer.database.data.DataContainer;
import tk.slicecollections.maxteer.database.data.DataTable;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;
import java.sql.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * @author Maxter
 */
public class MySQLDatabase extends Database {

  private String host;
  private String port;
  private String dbname;
  private String username;
  private String password;

  private Connection connection;
  private ExecutorService executor;

  public MySQLDatabase(String host, String port, String dbname, String username, String password) {
    this.host = host;
    this.port = port;
    this.dbname = dbname;
    this.username = username;
    this.password = password;

    this.openConnection();
    this.executor = Executors.newCachedThreadPool();

    this.update(
      "CREATE TABLE IF NOT EXISTS `mCoreNetworkBooster` (`id` VARCHAR(32), `booster` TEXT, `multiplier` DOUBLE, `expires` LONG, PRIMARY KEY(`id`)) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE utf8_bin;");

    DataTable.listTables().forEach(table -> {
      this.update(table.getInfo().create());
      table.init(this);
    });
  }

  @Override
  public Map<String, Map<String, DataContainer>> load(String name) {
    Map<String, Map<String, DataContainer>> tableMap = new HashMap<>();
    for (DataTable table : DataTable.listTables()) {
      Map<String, DataContainer> containerMap = new LinkedHashMap<>();
      tableMap.put(table.getInfo().name(), containerMap);

      CachedRowSet rs = this.query(table.getInfo().select(), name.toLowerCase());
      if (rs != null) {
        try {
          for (int collumn = 2; collumn <= rs.getMetaData().getColumnCount(); collumn++) {
            containerMap.put(rs.getMetaData().getColumnName(collumn), new DataContainer(rs.getObject(collumn)));
          }
        } catch (SQLException ex) {
          LOGGER.log(Level.SEVERE, "Nao foi possível carregar os dados \"" + table.getInfo().name() + "\" do perfil: ", ex);
        }

        continue;
      }

      containerMap = table.getDefaultValues();
      tableMap.put(table.getInfo().name(), containerMap);
      List<Object> list = new ArrayList<>();
      list.add(name);
      list.addAll(containerMap.values().stream().map(DataContainer::get).collect(Collectors.toList()));
      this.execute(table.getInfo().insert(), list.toArray(new Object[list.size()]));
      list.clear();
    }

    return tableMap;
  }

  @Override
  public void save(String name, Map<String, Map<String, DataContainer>> tableMap) {
    this.save0(name, tableMap, true);
  }

  @Override
  public void saveSync(String name, Map<String, Map<String, DataContainer>> tableMap) {
    this.save0(name, tableMap, false);
  }

  private void save0(String name, Map<String, Map<String, DataContainer>> tableMap, boolean async) {
    for (DataTable table : DataTable.listTables()) {
      Map<String, DataContainer> rows = tableMap.get(table.getInfo().name());
      if (rows.values().stream().noneMatch(DataContainer::isUpdated)) {
        continue;
      }

      List<Object> values = rows.values().stream().filter(DataContainer::isUpdated).map(DataContainer::get).collect(Collectors.toList());
      StringBuilder query = new StringBuilder("UPDATE `" + table.getInfo().name() + "` SET ");
      for (Map.Entry<String, DataContainer> collumn : rows.entrySet()) {
        if (collumn.getValue().isUpdated()) {
          collumn.getValue().setUpdated(false);
          query.append("`").append(collumn.getKey()).append("` = ?, ");
        }
      }
      query.deleteCharAt(query.length() - 1);
      query.deleteCharAt(query.length() - 1);
      query.append(" WHERE LOWER(`name`) = ?");
      values.add(name.toLowerCase());
      if (async) {
        this.execute(query.toString(), values.toArray());
      } else {
        this.update(query.toString(), values.toArray());
      }
      values.clear();
    }
  }

  @Override
  public String exists(String name) {
    try {
      return this.query("SELECT * FROM `mCoreProfile` WHERE LOWER(`name`) = ?", name.toLowerCase()).getString("name");
    } catch (Exception ex) {
      return null;
    }
  }

  public void openConnection() {
    try {
      boolean reconnected = true;
      if (this.connection == null) {
        reconnected = false;
      }
      this.connection = DriverManager
        .getConnection("jdbc:mysql://" + host + ":" + port + "/" + dbname + "?verifyServerCertificate=false&useSSL=false&useUnicode=yes&characterEncoding=UTF-8", username,
          password);
      if (reconnected) {
        LOGGER.info("Reconectado ao MySQL!");
        return;
      }

      LOGGER.info("Conectado ao MySQL!");
    } catch (SQLException ex) {
      LOGGER.log(Level.SEVERE, "Nao foi possivel se conectar ao MySQL: ", ex);
    }
  }

  public void closeConnection() {
    if (isConnected()) {
      try {
        connection.close();
      } catch (SQLException e) {
        LOGGER.log(Level.WARNING, "Nao foi possivel fechar a conexao com o MySQL: ", e);
      }
    }
  }

  public Connection getConnection() throws SQLException {
    if (!isConnected()) {
      this.openConnection();
    }

    return this.connection;
  }

  public boolean isConnected() {
    try {
      return !(connection == null || connection.isClosed() || !connection.isValid(5));
    } catch (SQLException ex) {
      LOGGER.log(Level.SEVERE, "Nao foi possivel verificar a conexao com o MySQL: ", ex);
      return false;
    }
  }

  public void update(String sql, Object... vars) {
    try (PreparedStatement ps = prepareStatement(sql, vars)) {
      ps.executeUpdate();
    } catch (SQLException ex) {
      LOGGER.log(Level.WARNING, "Nao foi possivel executar um SQL: ", ex);
    }
  }

  public void execute(String sql, Object... vars) {
    executor.execute(() -> {
      update(sql, vars);
    });
  }

  public int updateWithInsertId(String sql, Object... vars) {
    int id = -1;
    ResultSet rs = null;
    try (PreparedStatement ps = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
      for (int i = 0; i < vars.length; i++) {
        ps.setObject(i + 1, vars[i]);
      }
      ps.execute();
      rs = ps.getGeneratedKeys();
      if (rs.next()) {
        id = rs.getInt(1);
      }
    } catch (SQLException ex) {
      LOGGER.log(Level.WARNING, "Nao foi possivel executar um SQL: ", ex);
    } finally {
      try {
        if (rs != null && !rs.isClosed())
          rs.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }

    return id;
  }

  public PreparedStatement prepareStatement(String query, Object... vars) {
    try {
      PreparedStatement ps = getConnection().prepareStatement(query);
      for (int i = 0; i < vars.length; i++) {
        ps.setObject(i + 1, vars[i]);
      }
      return ps;
    } catch (SQLException ex) {
      LOGGER.log(Level.WARNING, "Nao foi possivel preparar um SQL: ", ex);
    }

    return null;
  }

  public CachedRowSet query(String query, Object... vars) {
    CachedRowSet rowSet = null;
    try {
      Future<CachedRowSet> future = executor.submit(() -> {
        CachedRowSet crs = null;
        try (PreparedStatement ps = prepareStatement(query, vars); ResultSet rs = ps.executeQuery()) {

          CachedRowSet rs2 = RowSetProvider.newFactory().createCachedRowSet();
          rs2.populate(rs);

          if (rs2.next()) {
            crs = rs2;
          }
        } catch (SQLException ex) {
          LOGGER.log(Level.WARNING, "Nao foi possivel executar um Requisicao: ", ex);
        }

        return crs;
      });

      if (future.get() != null) {
        rowSet = future.get();
      }
    } catch (Exception ex) {
      LOGGER.log(Level.WARNING, "Nao foi possivel chamar uma Futura Tarefa: ", ex);
    }

    return rowSet;
  }
}
