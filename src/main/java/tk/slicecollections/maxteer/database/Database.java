package tk.slicecollections.maxteer.database;

import tk.slicecollections.maxteer.Manager;
import tk.slicecollections.maxteer.database.data.DataContainer;

import javax.sql.rowset.CachedRowSet;
import java.util.Map;
import java.util.logging.Logger;

public abstract class Database {

  public abstract Map<String, Map<String, DataContainer>> load(String name);

  public abstract void save(String name, Map<String, Map<String, DataContainer>> tableMap);

  public abstract void saveSync(String name, Map<String, Map<String, DataContainer>> tableMap);

  public abstract String exists(String name);

  public abstract void execute(String sql, Object... vars);

  public abstract CachedRowSet query(String query, Object... vars);

  private static Database instance;
  public static final Logger LOGGER =
    Manager.BUNGEE ? tk.slicecollections.maxteer.bungee.Bungee.getInstance().getLogger() : tk.slicecollections.maxteer.Core.getInstance().getLogger();

  public static void setupDatabase(String type, String mysqlHost, String mysqlPort, String mysqlDbname, String mysqlUsername, String mysqlPassword, boolean hikari) {
    if (hikari) {
      instance = new HikariDatabase(mysqlHost, mysqlPort, mysqlDbname, mysqlUsername, mysqlPassword);
    } else {
      instance = new MySQLDatabase(mysqlHost, mysqlPort, mysqlDbname, mysqlUsername, mysqlPassword);
    }
  }

  public static Database getInstance() {
    return instance;
  }
}
