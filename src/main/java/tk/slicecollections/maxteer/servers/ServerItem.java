package tk.slicecollections.maxteer.servers;

import org.bukkit.scheduler.BukkitRunnable;
import tk.slicecollections.maxteer.Core;
import tk.slicecollections.maxteer.player.Profile;
import tk.slicecollections.maxteer.plugin.config.MConfig;
import tk.slicecollections.maxteer.servers.balancer.BaseBalancer;
import tk.slicecollections.maxteer.servers.balancer.Server;
import tk.slicecollections.maxteer.servers.balancer.type.LeastConnection;

import java.util.*;

/**
 * @author Maxter
 */
public class ServerItem {

  private String key;
  private int slot;
  private String icon;
  private BaseBalancer<Server> balancer;

  public ServerItem(String key, int slot, String icon, BaseBalancer<Server> baseBalancer) {
    this.key = key;
    this.slot = slot;
    this.icon = icon;
    this.balancer = baseBalancer;
  }

  public void connect(Profile profile) {
    Server server = balancer.next();
    if (server != null) {
      Core.sendServer(profile, server.getName());
    } else {
      profile.getPlayer().sendMessage("§cNão foi possível se conectar a esse servidor no momento!");
    }
  }

  public String getKey() {
    return this.key;
  }

  public int getSlot() {
    return this.slot;
  }

  public String getIcon() {
    return this.icon;
  }

  public BaseBalancer<Server> getBalancer() {
    return this.balancer;
  }

  private static final List<ServerItem> SERVERS = new ArrayList<>();
  public static final MConfig CONFIG = Core.getInstance().getConfig("servers");
  public static final List<Integer> DISABLED_SLOTS = CONFIG.getIntegerList("disabled-slots");
  public static final Map<String, Integer> SERVER_COUNT = new HashMap<>();

  public static void setupServers() {
    for (String key : CONFIG.getSection("items").getKeys(false)) {
      ServerItem si = new ServerItem(key, CONFIG.getInt("items." + key + ".slot"), CONFIG.getString("items." + key + ".icon"), new LeastConnection<>());
      SERVERS.add(si);
      CONFIG.getStringList("items." + key + ".servernames").forEach(server -> {
        if (server.split(" ; ").length < 2) {
          return;
        }

        si.getBalancer().add(server, new Server(server.split(" ; ")[0], server.split(" ; ")[1], CONFIG.getInt("items." + key + ".max-players")));
      });
    }

    new BukkitRunnable() {
      @Override
      public void run() {
        SERVERS.forEach(server -> server.getBalancer().getList().forEach(Server::fetch));
      }
    }.runTaskTimerAsynchronously(Core.getInstance(), 0, 40);
  }

  public static Collection<ServerItem> listServers() {
    return SERVERS;
  }

  public static ServerItem getServerItem(String key) {
    return SERVERS.stream().filter(si -> si.getKey().equals(key)).findFirst().orElse(null);
  }

  public static boolean alreadyQuerying(String servername) {
    return SERVERS.stream().anyMatch(si -> si.getBalancer().keySet().contains(servername));
  }

  public static int getServerCount(ServerItem serverItem) {
    return serverItem.getBalancer().getTotalNumber();
  }

  public static int getServerCount(String servername) {
    return SERVER_COUNT.get(servername) == null ? 0 : SERVER_COUNT.get(servername);
  }
}
