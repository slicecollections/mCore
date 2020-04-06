package tk.slicecollections.maxteer.database;

import org.bukkit.configuration.file.FileConfiguration;
import tk.slicecollections.maxteer.Core;
import tk.slicecollections.maxteer.database.data.DataContainer;
import tk.slicecollections.maxteer.database.data.DataTable;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;
import java.sql.*;
import java.util.*;
import java.util.concurrent.Callable;
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
    FileConfiguration config = Core.getInstance().getConfig();
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
    for (DataTable table : DataTable.listTables()) {
      List<Object> values = tableMap.get(table.getInfo().name()).values().stream().map(DataContainer::get).collect(Collectors.toList());
      values.add(name.toLowerCase());
      this.execute(table.getInfo().update(), values.toArray(new Object[values.size()]));
      values.clear();
    }
  }

  @Override
  public void saveSync(String name, Map<String, Map<String, DataContainer>> tableMap) {
    for (DataTable table : DataTable.listTables()) {
      List<Object> values = tableMap.get(table.getInfo().name()).values().stream().map(DataContainer::get).collect(Collectors.toList());
      values.add(name.toLowerCase());
      this.update(table.getInfo().update(), values.toArray(new Object[values.size()]));
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

  public Connection getConnection() {
    if (!isConnected()) {
      this.openConnection();
    }

    return connection;
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
    try {
      PreparedStatement ps = prepareStatement(sql, vars);
      ps.execute();
      ps.close();
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
    try {
      PreparedStatement ps = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
      for (int i = 0; i < vars.length; i++) {
        ps.setObject(i + 1, vars[i]);
      }
      ps.execute();
      ResultSet rs = ps.getGeneratedKeys();
      if (rs.next()) {
        id = rs.getInt(1);
      }
      rs.close();
      ps.close();
    } catch (SQLException ex) {
      LOGGER.log(Level.WARNING, "Nao foi possivel executar um SQL: ", ex);
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
      Future<CachedRowSet> future = executor.submit(new Callable<CachedRowSet>() {

        @Override
        public CachedRowSet call() {
          try {
            PreparedStatement ps = prepareStatement(query, vars);

            ResultSet rs = ps.executeQuery();
            CachedRowSet crs = RowSetProvider.newFactory().createCachedRowSet();
            crs.populate(rs);
            rs.close();
            ps.close();

            if (crs.next()) {
              return crs;
            }
          } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, "Nao foi possivel executar um Requisicao: ", ex);
          }

          return null;
        }
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
