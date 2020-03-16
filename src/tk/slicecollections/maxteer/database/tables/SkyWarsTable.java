package tk.slicecollections.maxteer.database.tables;

import java.util.LinkedHashMap;
import java.util.Map;
import tk.slicecollections.maxteer.database.Database;
import tk.slicecollections.maxteer.database.data.DataContainer;
import tk.slicecollections.maxteer.database.data.DataTable;
import tk.slicecollections.maxteer.database.data.interfaces.DataTableInfo;

@DataTableInfo(
    name = "mCoreSkyWars",
    create = "CREATE TABLE IF NOT EXISTS `mCoreSkyWars` (`name` VARCHAR(32), `1v1kills` LONG, `1v1deaths` LONG, `1v1assists` LONG, `1v1games` LONG, `1v1wins` LONG, `2v2kills` LONG, `2v2deaths` LONG, `2v2assists` LONG, `2v2games` LONG, `2v2wins` LONG, `coins` DOUBLE, `cosmetics` TEXT, `selected` TEXT, PRIMARY KEY(`name`)) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE utf8_bin;",
    select = "SELECT * FROM `mCoreSkyWars` WHERE LOWER(`name`) = ?",
    insert = "INSERT INTO `mCoreSkyWars` VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
    update = "UPDATE `mCoreSkyWars` SET `1v1kills` = ?, `1v1deaths` = ?, `1v1assists` = ?, `1v1games` = ?, `1v1wins` = ?, `2v2kills` = ?, `2v2deaths` = ?, `2v2assists` = ?, `2v2games` = ?, `2v2wins` = ?, `coins` = ?, `cosmetics` = ?, `selected` = ? WHERE LOWER(`name`) = ?"
    )
public class SkyWarsTable extends DataTable {
  
  @Override
  public void init(Database database) {}
  
  public Map<String, DataContainer> getDefaultValues() {
    Map<String, DataContainer> defaultValues = new LinkedHashMap<>();
    defaultValues.put("1v1kills", new DataContainer(0L));
    defaultValues.put("1v1deaths", new DataContainer(0L));
    defaultValues.put("1v1assists", new DataContainer(0L));
    defaultValues.put("1v1games", new DataContainer(0L));
    defaultValues.put("1v1wins", new DataContainer(0L));
    defaultValues.put("2v2kills", new DataContainer(0L));
    defaultValues.put("2v2deaths", new DataContainer(0L));
    defaultValues.put("2v2assists", new DataContainer(0L));
    defaultValues.put("2v2games", new DataContainer(0L));
    defaultValues.put("2v2wins", new DataContainer(0L));
    defaultValues.put("coins", new DataContainer(0.0D));
    defaultValues.put("cosmetics", new DataContainer("{}"));
    defaultValues.put("selected", new DataContainer("{}"));
    return defaultValues;
  }
}
