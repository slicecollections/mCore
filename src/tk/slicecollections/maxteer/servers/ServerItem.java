package tk.slicecollections.maxteer.servers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import tk.slicecollections.maxteer.Core;
import tk.slicecollections.maxteer.player.Profile;
import tk.slicecollections.maxteer.plugin.config.MConfig;
import tk.slicecollections.maxteer.servers.balancer.BaseBalancer;
import tk.slicecollections.maxteer.servers.balancer.Server;
import tk.slicecollections.maxteer.servers.balancer.type.LeastConnection;
import tk.slicecollections.maxteer.servers.balancer.type.MostConnection;

public class ServerItem {

  private int slot;
  private String icon;
  private BaseBalancer<Server> balancer;

  public ServerItem(int slot, String icon, BaseBalancer<Server> baseBalancer) {
    this.slot = slot;
    this.icon = icon;
    this.balancer = baseBalancer;
  }

  // organizar
  public void connect(Profile profile) {
    Server server = balancer.next();
    if (server != null) {
      Core.sendServer(profile, server.getName());
    }
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
      ServerItem si = new ServerItem(CONFIG.getInt("items." + key + ".slot"), CONFIG.getString("items." + key + ".icon"),
          key.equalsIgnoreCase("lobby") ? new MostConnection<>() : new LeastConnection<>());
      SERVERS.add(si);
      CONFIG.getStringList("items." + key + ".servernames").forEach(server -> {
        si.getBalancer().add(server, new Server(server, CONFIG.getInt("items." + key + ".max-players")));
      });
    }

    new BukkitRunnable() {
      @Override
      public void run() {
        SERVERS.forEach(server -> server.getBalancer().keySet().forEach(servername -> writeCount(servername)));
      }
    }.runTaskTimer(Core.getInstance(), 0, 40);
  }

  public static Collection<ServerItem> listServers() {
    return SERVERS;
  }

  public static boolean alreadyQuerying(String servername) {
    return SERVERS.stream().filter(si -> si.getBalancer().keySet().contains(servername)).findAny().isPresent();
  }

  public static int getServerCount(ServerItem serverItem) {
    return serverItem.getBalancer().getTotalNumber();
  }

  public static int getServerCount(String servername) {
    return SERVER_COUNT.get(servername) == null ? 0 : SERVER_COUNT.get(servername);
  }

  @SuppressWarnings("unchecked")
  public static void writeCount(String server) {
    Iterator<Player> itr = (Iterator<Player>) Bukkit.getOnlinePlayers().iterator();
    if (!itr.hasNext()) {
      return;
    }

    Player fake = itr.next();
    if (fake == null) {
      return;
    }

    ByteArrayDataOutput out = ByteStreams.newDataOutput();
    out.writeUTF("PlayerCount");
    out.writeUTF(server);
    fake.sendPluginMessage(Core.getInstance(), "BungeeCord", out.toByteArray());
  }
}
