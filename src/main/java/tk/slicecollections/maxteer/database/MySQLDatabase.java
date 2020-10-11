package tk.slicecollections.maxteer.database;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import tk.slicecollections.maxteer.Manager;
import tk.slicecollections.maxteer.booster.NetworkBooster;
import tk.slicecollections.maxteer.database.cache.RoleCache;
import tk.slicecollections.maxteer.database.data.DataContainer;
import tk.slicecollections.maxteer.database.data.DataTable;
import tk.slicecollections.maxteer.player.role.Role;
import tk.slicecollections.maxteer.utils.StringUtils;

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
  private boolean mariadb;

  private Connection connection;
  private ExecutorService executor;

  public MySQLDatabase(String host, String port, String dbname, String username, String password, boolean mariadb) {
    this(host, port, dbname, username, password, mariadb, false);
  }

  public MySQLDatabase(String host, String port, String dbname, String username, String password, boolean mariadb, boolean skipTables) {
    this.host = host;
    this.port = port;
    this.dbname = dbname;
    this.username = username;
    this.password = password;
    this.mariadb = mariadb;

    this.openConnection();
    this.executor = Executors.newCachedThreadPool();

    if (!skipTables) {
      this.update(
        "CREATE TABLE IF NOT EXISTS `mCoreNetworkBooster` (`id` VARCHAR(32), `booster` TEXT, `multiplier` DOUBLE, `expires` LONG, PRIMARY KEY(`id`)) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE utf8_bin;");

      DataTable.listTables().forEach(table -> {
        this.update(table.getInfo().create());
        table.init(this);
      });
    }
  }

  @Override
  public void setupBoosters() {
    if (!Manager.BUNGEE) {
      for (String mg : tk.slicecollections.maxteer.Core.minigames) {
        if (query("SELECT * FROM `mCoreNetworkBooster` WHERE `id` = ?", mg) == null) {
          execute("INSERT INTO `mCoreNetworkBooster` VALUES (?, ?, ?, ?)", mg, "Maxteer", 1.0, 0L);
        }
      }
    }
  }

  @Override
  public void setBooster(String minigame, String booster, double multiplier, long expires) {
    execute("UPDATE `mCoreNetworkBooster` SET `booster` = ?, `multiplier` = ?, `expires` = ? WHERE `id` = ?", booster, multiplier, expires, minigame);
  }

  @Override
  public NetworkBooster getBooster(String minigame) {
    try (CachedRowSet rs = query("SELECT * FROM `mCoreNetworkBooster` WHERE `id` = ?", minigame)) {
      if (rs != null) {
        String booster = rs.getString("booster");
        double multiplier = rs.getDouble("multiplier");
        long expires = rs.getLong("expires");
        if (expires > System.currentTimeMillis()) {
          rs.close();
          return new NetworkBooster(booster, multiplier, expires);
        }
      }
    } catch (SQLException ignored) {}

    return null;
  }

  @Override
  public String getRankAndName(String player) {
    try (CachedRowSet rs = query("SELECT `name`, `role` FROM `mCoreProfile` WHERE LOWER(`name`) = ?", player.toLowerCase())) {
      if (rs != null) {
        String result = rs.getString("role") + " : " + rs.getString("name");
        RoleCache.setCache(player, rs.getString("role"), rs.getString("name"));
        return result;
      }
    } catch (SQLException ignored) {}
    return null;
  }

  @Override
  public boolean getPreference(String player, String id, boolean def) {
    boolean preference = true;
    try (CachedRowSet rs = query("SELECT `preferences` FROM `mCoreProfile` WHERE LOWER(`name`) = ?", player.toLowerCase())) {
      if (rs != null) {
        preference = ((JSONObject) new JSONParser().parse(rs.getString("preferences"))).get(id).equals(0L);
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }

    return preference;
  }

  @Override
  public List<String[]> getLeaderBoard(String table, String... columns) {
    List<String[]> result = new ArrayList<>();
    StringBuilder add = new StringBuilder(), select = new StringBuilder();
    for (String column : columns) {
      add.append("`").append(column).append("` + ");
      select.append("`").append(column).append("`, ");
    }

    try (CachedRowSet rs = query("SELECT " + select.toString() + "`name` FROM `" + table + "` ORDER BY " + add.toString() + " 0 DESC LIMIT 10")) {
      if (rs != null) {
        rs.beforeFirst();
        while (rs.next()) {
          long count = 0;
          for (String column : columns) {
            count += rs.getLong(column);
          }
          result.add(new String[] {Role.getColored(rs.getString("name"), true), StringUtils.formatNumber(count)});
        }
      }
    } catch (SQLException ignore) {}

    return result;
  }

  @Override
  public void close() {
    this.executor.shutdownNow().forEach(Runnable::run);
    this.closeConnection();
  }

  @Override
  public Map<String, Map<String, DataContainer>> load(String name) {
    Map<String, Map<String, DataContainer>> tableMap = new HashMap<>();
    for (DataTable table : DataTable.listTables()) {
      Map<String, DataContainer> containerMap = new LinkedHashMap<>();
      tableMap.put(table.getInfo().name(), containerMap);

      try (CachedRowSet rs = this.query(table.getInfo().select(), name.toLowerCase())) {
        if (rs != null) {
          for (int collumn = 2; collumn <= rs.getMetaData().getColumnCount(); collumn++) {
            containerMap.put(rs.getMetaData().getColumnName(collumn), new DataContainer(rs.getObject(collumn)));
          }
          continue;
        }
      } catch (SQLException ex) {
        LOGGER.log(Level.SEVERE, "Nao foi possível carregar os dados \"" + table.getInfo().name() + "\" do perfil: ", ex);
        continue;
      }

      containerMap = table.getDefaultValues();
      tableMap.put(table.getInfo().name(), containerMap);
      List<Object> list = new ArrayList<>();
      list.add(name);
      list.addAll(containerMap.values().stream().map(DataContainer::get).collect(Collectors.toList()));
      this.execute(table.getInfo().insert(), list.toArray());
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
      return this.query("SELECT `name` FROM `mCoreProfile` WHERE LOWER(`name`) = ?", name.toLowerCase()).getString("name");
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
      Class.forName(this.mariadb ? "org.mariadb.jdbc.Driver" : "com.mysql.jdbc.Driver");
      this.connection = DriverManager.getConnection((this.mariadb ?
        "jdbc:mariadb://" :
        "jdbc:mysql://") + host + ":" + port + "/" + dbname + "?verifyServerCertificate=false&useSSL=false&useUnicode=yes&characterEncoding=UTF-8", username, password);
      if (reconnected) {
        LOGGER.info("Reconectado ao MySQL!");
        return;
      }

      LOGGER.info("Conectado ao MySQL!");
    } catch (Exception ex) {
      LOGGER.log(Level.SEVERE, "Nao foi possivel se conectar ao MySQL: ", ex);
      System.exit(0);
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
