package tk.slicecollections.maxteer.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import tk.slicecollections.maxteer.database.data.DataContainer;
import tk.slicecollections.maxteer.database.data.DataTable;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;
import java.sql.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * @author Maxter
 */
public class HikariDatabase extends Database {

  private String host;
  private String port;
  private String dbname;
  private String username;
  private String password;

  private HikariDataSource dataSource;
  private ExecutorService executor;

  public HikariDatabase(String host, String port, String dbname, String username, String password) {
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
      this.execute(table.getInfo().update(), values.toArray());
      values.clear();
    }
  }

  @Override
  public void saveSync(String name, Map<String, Map<String, DataContainer>> tableMap) {
    for (DataTable table : DataTable.listTables()) {
      List<Object> values = tableMap.get(table.getInfo().name()).values().stream().map(DataContainer::get).collect(Collectors.toList());
      values.add(name.toLowerCase());
      this.update(table.getInfo().update(), values.toArray());
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
    HikariConfig config = new HikariConfig();
    config.setPoolName("mCoreConnectionPool");
    config.setMaximumPoolSize(32);
    config.setConnectionTimeout(30000L);
    config.setDriverClassName("com.mysql.jdbc.Driver");
    config.setJdbcUrl("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.dbname);
    config.setUsername(this.username);
    config.setPassword(this.password);
    config.addDataSourceProperty("autoReconnect", "true");
    this.dataSource = new HikariDataSource(config);

    LOGGER.info("Conectado ao MySQL!");
  }

  public void closeConnection() {
    if (isConnected()) {
      this.dataSource.close();
    }
  }

  public Connection getConnection() throws SQLException {
    return this.dataSource.getConnection();
  }

  public boolean isConnected() {
    return !this.dataSource.isClosed();
  }

  public void update(String sql, Object... vars) {
    Connection connection = null;
    PreparedStatement ps = null;
    try {
      connection = getConnection();
      ps = connection.prepareStatement(sql);
      for (int i = 0; i < vars.length; i++) {
        ps.setObject(i + 1, vars[i]);
      }
      ps.executeUpdate();
    } catch (SQLException ex) {
      LOGGER.log(Level.WARNING, "Nao foi possivel executar um SQL: ", ex);
    } finally {
      try {
        if (connection != null && !connection.isClosed()) connection.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
      try {
        if (ps != null && !ps.isClosed()) ps.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }

  public void execute(String sql, Object... vars) {
    executor.execute(() -> {
      update(sql, vars);
    });
  }

  public int updateWithInsertId(String sql, Object... vars) {
    int id = -1;
    Connection connection = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      connection = getConnection();
      ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
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
        if (connection != null && !connection.isClosed()) connection.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
      try {
        if (ps != null && !ps.isClosed()) ps.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
      try {
        if (rs != null && !rs.isClosed()) rs.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }

    return id;
  }

  public CachedRowSet query(String query, Object... vars) {
    Connection connection = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    CachedRowSet rowSet = null;
    try {
      connection = getConnection();
      ps = connection.prepareStatement(query);
      for (int i = 0; i < vars.length; i++) {
        ps.setObject(i + 1, vars[i]);
      }
      rs = ps.executeQuery();
      rowSet = RowSetProvider.newFactory().createCachedRowSet();
      rowSet.populate(rs);

      if (rowSet.next()) {
        return rowSet;
      }
    } catch (SQLException ex) {
      LOGGER.log(Level.WARNING, "Nao foi possivel executar um Requisicao: ", ex);
    } finally {
      try {
        if (connection != null && !connection.isClosed()) connection.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
      try {
        if (ps != null && !ps.isClosed()) ps.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
      try {
        if (rs != null && !rs.isClosed()) rs.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }

    return null;
  }
}
