package tk.slicecollections.maxteer.database;

import tk.slicecollections.maxteer.Core;
import tk.slicecollections.maxteer.database.data.DataContainer;
import tk.slicecollections.maxteer.plugin.logger.MLogger;

import javax.sql.rowset.CachedRowSet;
import java.util.Map;

public abstract class Database {

  public abstract Map<String, Map<String, DataContainer>> load(String name);

  public abstract void save(String name, Map<String, Map<String, DataContainer>> tableMap);

  public abstract void saveSync(String name, Map<String, Map<String, DataContainer>> tableMap);

  public abstract String exists(String name);

  public abstract void execute(String sql, Object... vars);

  public abstract CachedRowSet query(String query, Object... vars);

  private static Database instance;
  public static final MLogger LOGGER = ((MLogger) Core.getInstance().getLogger()).getModule("DATABASE");

  public static void setupDatabase() {
    String type = Core.getInstance().getConfig().getString("database.tipo");

    if (type.equalsIgnoreCase("mysql")) {
      instance = new MySQLDatabase();
    }
  }

  public static Database getInstance() {
    return instance;
  }
}
