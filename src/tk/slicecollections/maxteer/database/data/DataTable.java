package tk.slicecollections.maxteer.database.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import tk.slicecollections.maxteer.database.Database;
import tk.slicecollections.maxteer.database.data.interfaces.DataTableInfo;
import tk.slicecollections.maxteer.database.tables.CoreTable;
import tk.slicecollections.maxteer.database.tables.SkyWarsTable;
import tk.slicecollections.maxteer.database.tables.TheBridgeTable;

public abstract class DataTable {
  
  public abstract void init(Database database);
  
  public abstract Map<String, DataContainer> getDefaultValues();
  
  public DataTableInfo getInfo() {
    return this.getClass().getAnnotation(DataTableInfo.class);
  }
  
  private static final List<DataTable> TABLES = new ArrayList<>();
  
  static {
    TABLES.add(new CoreTable());
    TABLES.add(new SkyWarsTable());
    TABLES.add(new TheBridgeTable());
  }
  
  public static void registerTable(DataTable table) {
    TABLES.add(table);
  }
  
  public static Collection<DataTable> listTables() {
    return TABLES;
  }
}
