package tk.slicecollections.maxteer.database;

import com.mongodb.BasicDBObject;
import com.mongodb.client.*;
import com.mongodb.client.model.Collation;
import com.mongodb.client.model.CollationStrength;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import tk.slicecollections.maxteer.Manager;
import tk.slicecollections.maxteer.booster.NetworkBooster;
import tk.slicecollections.maxteer.database.cache.RoleCache;
import tk.slicecollections.maxteer.database.data.DataContainer;
import tk.slicecollections.maxteer.database.data.DataTable;
import tk.slicecollections.maxteer.database.data.interfaces.DataTableInfo;
import tk.slicecollections.maxteer.player.role.Role;
import tk.slicecollections.maxteer.reflection.Accessors;
import tk.slicecollections.maxteer.reflection.acessors.MethodAccessor;
import tk.slicecollections.maxteer.utils.StringUtils;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;

public class MongoDBDatabase extends Database {

  private String url;

  private MongoClient client;
  private MongoDatabase database;
  private MongoCollection<Document> collection;
  private Collation collation;
  private UpdateOptions updateOptions;
  private ExecutorService executor;

  private List<String> tables;

  public MongoDBDatabase(String mongoURL) {
    this.url = mongoURL;

    this.openConnection();
    this.executor = Executors.newCachedThreadPool();

    this.collation = Collation.builder().locale("en_US").collationStrength(CollationStrength.SECONDARY).build();
    this.updateOptions = new UpdateOptions().collation(this.collation);
    this.tables =
      DataTable.listTables().stream().map(DataTable::getInfo).map(DataTableInfo::name).filter(name -> !name.equalsIgnoreCase("mCoreProfile")).collect(Collectors.toList());

    if (!Manager.BUNGEE) {
      Object pluginManager = Accessors.getMethod(org.bukkit.Bukkit.class, "getPluginManager").invoke(null);
      MethodAccessor registerEvents = Accessors.getMethod(pluginManager.getClass(), "registerEvents", org.bukkit.event.Listener.class, org.bukkit.plugin.Plugin.class);
      registerEvents.invoke(pluginManager, new tk.slicecollections.maxteer.database.conversor.MongoDBConversor(), tk.slicecollections.maxteer.Core.getInstance());
    }
  }

  @Override
  public void convertDatabase(Object player) {
    if (!Manager.BUNGEE) {
      if (tk.slicecollections.maxteer.database.conversor.MongoDBConversor.CONVERT != null) {
        ((org.bukkit.entity.Player) player).sendMessage("§cUma conversão de Banco de Dados está em andamento.");
        return;
      }

      tk.slicecollections.maxteer.database.conversor.MongoDBConversor.CONVERT = new String[5];
      ((org.bukkit.entity.Player) player).sendMessage("§aIniciando conversão §8(MySQL -> MongoDB)");
      ((org.bukkit.entity.Player) player).sendMessage("§aInsira a Host do MySQL!");
      ((org.bukkit.entity.Player) player).sendMessage("§cVocê pode cancelar essa Operação ao digitar 'cancelar' (sem aspas).");
    }
  }

  @Override
  public void setupBoosters() {
    if (!Manager.BUNGEE) {
      MongoCollection<Document> collection = this.database.getCollection("mCoreNetworkBooster");
      for (String mg : tk.slicecollections.maxteer.Core.minigames) {
        if (collection.find(new BasicDBObject("_id", mg)).first() == null) {
          this.executor.execute(() -> collection.insertOne(new Document("_id", mg).append("booster", "Maxteer").append("multiplier", 1.0).append("expires", 0L)));
        }
      }
    }
  }

  @Override
  public void setBooster(String minigame, String booster, double multiplier, long expires) {
    this.executor.execute(() -> this.database.getCollection("mCoreNetworkBooster")
      .updateOne(Filters.eq("_id", minigame), new BasicDBObject("$set", new BasicDBObject("booster", booster).append("multiplier", multiplier).append("expires", expires))));
  }

  @Override
  public NetworkBooster getBooster(String minigame) {
    try {
      Document document = this.executor.submit(() -> this.database.getCollection("mCoreNetworkBooster").find(new BasicDBObject("_id", minigame)).first()).get();
      if (document != null) {
        String booster = document.getString("booster");
        double multiplier = document.getDouble("multiplier");
        long expires = document.getLong("expires");
        if (expires > System.currentTimeMillis()) {
          return new NetworkBooster(booster, multiplier, expires);
        }
      }
    } catch (Exception ignored) {}

    return null;
  }

  @Override
  public String getRankAndName(String player) {
    try {
      Document document = this.executor
        .submit(() -> this.collection.find(new BasicDBObject("_id", player.toLowerCase())).projection(fields(include("_id", "role"))).collation(this.collation).first()).get();
      if (document != null) {
        String result = document.getString("role") + " : " + document.getString("_id");
        RoleCache.setCache(player, document.getString("role"), document.getString("_id"));
        return result;
      }
    } catch (Exception ignored) {}
    return null;
  }

  @Override
  public boolean getPreference(String player, String id, boolean def) {
    boolean preference = true;
    try {
      Document document = this.executor
        .submit(() -> this.collection.find(new BasicDBObject("_id", player.toLowerCase())).projection(fields(include("preferences"))).collation(this.collation).first()).get();
      if (document != null) {
        preference = ((JSONObject) new JSONParser().parse(document.getString("preferences"))).get(id).equals(0L);
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }

    return preference;
  }

  @Override
  public List<String[]> getLeaderBoard(String table, String... columns) {
    List<String[]> result = new ArrayList<>();
    String name = columns[0].equals("1v1kills") ? "totalkills" : columns[0].equals("1v1wins") ? "totalwins" : columns[0].equals("1v1points") ? "totalpoints" : columns[0];
    try {
      MongoCursor<Document> cursor = this.executor
        .submit(() -> this.collection.find().projection(fields(include("_id", "role", table + "." + name))).sort(new BasicDBObject(table + "." + name, -1)).limit(10).cursor())
        .get();
      while (cursor.hasNext()) {
        Document document = cursor.next();
        Document subDocument = document.get(table, Document.class);
        long count = (subDocument == null || !subDocument.containsKey(name) ? 0L : Long.parseLong(subDocument.get(name).toString()));
        result
          .add(new String[] {StringUtils.getLastColor(Role.getRoleByName(document.getString("role")).getPrefix()) + document.getString("_id"), StringUtils.formatNumber(count)});
      }
      cursor.close();
    } catch (Exception ignore) {}

    return result;
  }

  @Override
  public void close() {
    this.executor.shutdownNow().forEach(Runnable::run);
    this.client.close();
  }

  public void openConnection() {
    this.client = MongoClients.create(this.url);
    this.database = this.client.getDatabase("mCore");
    this.collection = this.database.getCollection("Profile");
    LOGGER.info("Conectado ao MongoDB");
  }

  @Override
  public Map<String, Map<String, DataContainer>> load(String name) {
    Map<String, Map<String, DataContainer>> tableMap = new HashMap<>();

    List<String> includes = new ArrayList<>();
    for (DataTable table : DataTable.listTables()) {
      Map<String, DataContainer> containerMap = table.getDefaultValues();
      if (table.getInfo().name().contains("SkyWars") || table.getInfo().name().contains("TheBridge")) {
        String assists = table.getInfo().name().contains("SkyWars") ? "assists" : "points";
        for (String stats : new String[] {"kills", "deaths", assists, "wins", "games"}) {
          containerMap.put("monthly" + stats, new DataContainer(0L));
        }
        containerMap.put("month", new DataContainer((Calendar.getInstance().get(Calendar.MONTH) + 1) + "/" + Calendar.getInstance().get(Calendar.YEAR)));
      }

      String prefix = table.getInfo().name().equalsIgnoreCase("mcoreprofile") ? "" : table.getInfo().name() + ".";
      containerMap.keySet().forEach(key -> includes.add(prefix + key));
      tableMap.put(table.getInfo().name(), containerMap);
    }

    Document document;
    try {
      document = this.executor.submit(() -> this.collection.find(new BasicDBObject("_id", name)).projection(fields(include(includes))).collation(this.collation).first()).get();
    } catch (InterruptedException | ExecutionException ex) {
      LOGGER.log(Level.SEVERE, "Nao foi possível carregar os dados do perfil: ", ex);
      return tableMap;
    }

    if (document != null) {
      tableMap.values().forEach(map -> map.values().forEach(dc -> dc.setUpdated(true)));
      for (String key : document.keySet()) {
        if (key.equalsIgnoreCase("_id") || key.equalsIgnoreCase("totalkills") || key.equalsIgnoreCase("totalwins") || key.equalsIgnoreCase("totalpoints")) {
          continue;
        }

        if (this.tables.contains(key)) {
          Document subDocument = document.get(key, Document.class);
          subDocument.keySet().forEach(subKey -> tableMap.get(key).put(subKey, new DataContainer(subDocument.get(subKey))));
          continue;
        }

        tableMap.get("mCoreProfile").put(key, new DataContainer(document.get(key)));
      }
    } else {
      Document insert = new Document();
      insert.put("_id", name);
      for (Map.Entry<String, Map<String, DataContainer>> tables : tableMap.entrySet()) {
        if (this.tables.contains(tables.getKey())) {
          Document table = new Document();
          for (Map.Entry<String, DataContainer> containers : tables.getValue().entrySet()) {
            table.put(containers.getKey(), containers.getValue().get());
          }
          insert.put(tables.getKey(), table);
          continue;
        }

        for (Map.Entry<String, DataContainer> containers : tables.getValue().entrySet()) {
          insert.put(containers.getKey(), containers.getValue().get());
        }
      }
      this.executor.execute(() -> collection.insertOne(insert));
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
    final Document save = new Document();
    for (DataTable table : DataTable.listTables()) {
      Map<String, DataContainer> rows = tableMap.get(table.getInfo().name());
      if (rows.values().stream().noneMatch(DataContainer::isUpdated)) {
        continue;
      }

      String prefix = table.getInfo().name().equalsIgnoreCase("mcoreprofile") ? "" : table.getInfo().name() + ".";
      if (table.getInfo().name().contains("SkyWars") || table.getInfo().name().contains("TheBridge")) {
        save.put(prefix + "totalkills", rows.get("1v1kills").getAsLong() + rows.get("2v2kills").getAsLong());
        save.put(prefix + "totalwins", rows.get("1v1wins").getAsLong() + rows.get("2v2wins").getAsLong());
        if (table.getInfo().name().contains("TheBridge")) {
          save.put(prefix + "totalpoints", rows.get("1v1points").getAsLong() + rows.get("2v2points").getAsLong());
        }
      }

      for (Map.Entry<String, DataContainer> entry : rows.entrySet()) {
        if (entry.getValue().isUpdated()) {
          entry.getValue().setUpdated(false);
          save.put(prefix + entry.getKey(), entry.getValue().get());
        }
      }
    }

    if (save.isEmpty()) {
      return;
    }

    if (async) {
      this.executor.execute(() -> this.collection.updateOne(Filters.eq("_id", name), new Document("$set", save), this.updateOptions));
    } else {
      this.collection.updateOne(Filters.eq("_id", name), new Document("$set", save), this.updateOptions);
    }
  }

  @Override
  public String exists(String name) {
    try {
      return Objects
        .requireNonNull(this.executor.submit(() -> this.collection.find(new BasicDBObject("_id", name)).projection(fields(include("_id"))).collation(collation)).get().first())
        .getString("_id");
    } catch (Exception ex) {
      return null;
    }
  }

  public MongoDatabase getDatabase() {
    return this.database;
  }

  public MongoCollection<Document> getCollection() {
    return this.collection;
  }

  public ExecutorService getExecutor() {
    return this.executor;
  }
}
