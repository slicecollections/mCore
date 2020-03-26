package tk.slicecollections.maxteer.database.tables;

import tk.slicecollections.maxteer.database.Database;
import tk.slicecollections.maxteer.database.data.DataContainer;
import tk.slicecollections.maxteer.database.data.DataTable;
import tk.slicecollections.maxteer.database.data.interfaces.DataTableInfo;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Maxter
 */
@DataTableInfo(
    name = "mCoreSkyWars",
    create = "CREATE TABLE IF NOT EXISTS `mCoreSkyWars` (`name` VARCHAR(32), `1v1kills` LONG, `1v1deaths` LONG, `1v1assists` LONG, `1v1games` LONG, `1v1wins` LONG, `2v2kills` LONG, `2v2deaths` LONG, `2v2assists` LONG, `2v2games` LONG, `2v2wins` LONG, `coins` DOUBLE, `lastmap` LONG, `cosmetics` TEXT, `selected` TEXT, `kitconfig` TEXT, PRIMARY KEY(`name`)) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE utf8_bin;",
    select = "SELECT * FROM `mCoreSkyWars` WHERE LOWER(`name`) = ?",
    insert = "INSERT INTO `mCoreSkyWars` VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
    update = "UPDATE `mCoreSkyWars` SET `1v1kills` = ?, `1v1deaths` = ?, `1v1assists` = ?, `1v1games` = ?, `1v1wins` = ?, `2v2kills` = ?, `2v2deaths` = ?, `2v2assists` = ?, `2v2games` = ?, `2v2wins` = ?, `coins` = ?, `lastmap` = ?, `cosmetics` = ?, `selected` = ?, `kitconfig` = ? WHERE LOWER(`name`) = ?"
    )
public class SkyWarsTable extends DataTable {
  
  @Override
  public void init(Database database) {
    if (database.query("SHOW COLUMNS FROM `mCoreSkyWars` LIKE 'lastmap'") == null) {
      database.execute(
        "ALTER TABLE `mCoreSkyWars` ADD `lastmap` LONG DEFAULT 0 AFTER `coins`, ADD `kitconfig` TEXT AFTER `selected`");
    }
  }
  
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
    defaultValues.put("lastmap", new DataContainer(0L));
    defaultValues.put("cosmetics", new DataContainer("{}"));
    defaultValues.put("selected", new DataContainer("{}"));
    defaultValues.put("kitconfig", new DataContainer("{}"));
    return defaultValues;
  }
}
