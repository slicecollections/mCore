package tk.slicecollections.maxteer;

import com.comphenix.protocol.ProtocolLibrary;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import tk.slicecollections.maxteer.achievements.Achievement;
import tk.slicecollections.maxteer.booster.Booster;
import tk.slicecollections.maxteer.cmd.Commands;
import tk.slicecollections.maxteer.database.Database;
import tk.slicecollections.maxteer.deliveries.Delivery;
import tk.slicecollections.maxteer.hook.MCoreExpansion;
import tk.slicecollections.maxteer.hook.protocollib.FakeAdapter;
import tk.slicecollections.maxteer.hook.protocollib.HologramAdapter;
import tk.slicecollections.maxteer.hook.protocollib.NPCAdapter;
import tk.slicecollections.maxteer.libraries.MinecraftVersion;
import tk.slicecollections.maxteer.libraries.holograms.HologramLibrary;
import tk.slicecollections.maxteer.libraries.npclib.NPCLibrary;
import tk.slicecollections.maxteer.listeners.Listeners;
import tk.slicecollections.maxteer.listeners.PluginMessageListener;
import tk.slicecollections.maxteer.nms.NMS;
import tk.slicecollections.maxteer.player.Profile;
import tk.slicecollections.maxteer.player.role.Role;
import tk.slicecollections.maxteer.plugin.MPlugin;
import tk.slicecollections.maxteer.plugin.config.MConfig;
import tk.slicecollections.maxteer.servers.ServerItem;
import tk.slicecollections.maxteer.titles.Title;
import tk.slicecollections.maxteer.utils.SliceUpdater;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Maxter
 */
public class Core extends MPlugin {

  private static Core instance;
  public static boolean validInit;
  public static final List<String> warnings = new ArrayList<>();
  public static final List<String> minigames = Arrays.asList("Sky Wars", "The Bridge");
  public static String minigame = "";

  @Override
  public void start() {
    instance = this;
  }

  @Override
  public void load() {}

  @Override
  public void enable() {
    if (!NMS.setupNMS()) {
      this.setEnabled(false);
      this.getLogger().warning("O plugin apenas funciona na versao 1_8_R3 (Atual: " + MinecraftVersion.getCurrentVersion().getVersion() + ")");
      return;
    }

    saveDefaultConfig();
    lobby = Bukkit.getWorlds().get(0).getSpawnLocation();

    // Remove o spawn-protection-size automaticamente
    if (Bukkit.getSpawnRadius() != 0) {
      Bukkit.setSpawnRadius(0);
    }

    if (Bukkit.getPluginManager().getPlugin("AntiVoid") != null) {
      warnings.add(" - AntiVoid");
    }
    if (Bukkit.getPluginManager().getPlugin("LegendChat") != null) {
      warnings.add(" - LegendChat");
    }
    if (Bukkit.getPluginManager().getPlugin("Essentials") != null) {
      warnings.add(" - Essentials");
    }
    if (Bukkit.getPluginManager().getPlugin("EssentialsChat") != null) {
      warnings.add(" - EssentialsChat");
    }
    if (Bukkit.getPluginManager().getPlugin("EssentialsSpawn") != null) {
      warnings.add(" - EssentialsSpawn");
    }
    if (Bukkit.getPluginManager().getPlugin("EssentialsProtect") != null) {
      warnings.add(" - EssentialsProtect");
    }
    if (Bukkit.getPluginManager().getPlugin("EssentialsAntiBuild") != null) {
      warnings.add(" - EssentialsAntiBuild");
    }
    if (Bukkit.getPluginManager().getPlugin("Multiverse-Core") != null) {
      warnings.add(" - Multiverse-Core");
    }

    PlaceholderAPI.registerExpansion(new MCoreExpansion());

    Database.setupDatabase(getConfig().getString("database.tipo"), getConfig().getString("database.mysql.host"), getConfig().getString("database.mysql.porta"),
      getConfig().getString("database.mysql.nome"), getConfig().getString("database.mysql.usuario"), getConfig().getString("database.mysql.senha"));

    NPCLibrary.setupNPCs(this);
    HologramLibrary.setupHolograms(this);

    setupRoles();
    Title.setupTitles();
    Booster.setupBoosters();
    Delivery.setupDeliveries();
    ServerItem.setupServers();
    Achievement.setupAchievements();

    Commands.setupCommands();
    Listeners.setupListeners();

    ProtocolLibrary.getProtocolManager().addPacketListener(new FakeAdapter());
    ProtocolLibrary.getProtocolManager().addPacketListener(new NPCAdapter());
    ProtocolLibrary.getProtocolManager().addPacketListener(new HologramAdapter());

    getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
    getServer().getMessenger().registerIncomingPluginChannel(this, "mCore", new PluginMessageListener());

    Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> new SliceUpdater(this, 4).run());

    validInit = true;
    this.getLogger().info("O plugin foi ativado.");
  }

  @Override
  public void disable() {
    if (validInit) {
      Profile.listProfiles().forEach(Profile::saveSync);
    }

    File update = new File("plugins/mCore/update", "mCore.jar");
    if (update.exists()) {
      try {
        this.getFileUtils().deleteFile(new File("plugins/mCore.jar"));
        this.getFileUtils().copyFile(new FileInputStream(update), new File("plugins/mCore.jar"));
        this.getFileUtils().deleteFile(update.getParentFile());
        this.getLogger().info("Update do mCore aplicada.");
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }
    this.getLogger().info("O plugin foi desativado.");
  }

  private void setupRoles() {
    MConfig config = getConfig("roles");
    for (String key : config.getSection("roles").getKeys(false)) {
      String name = config.getString("roles." + key + ".name");
      String prefix = config.getString("roles." + key + ".prefix");
      String permission = config.getString("roles." + key + ".permission");
      boolean broadcast = config.getBoolean("roles." + key + ".broadcast", true);
      boolean alwaysVisible = config.getBoolean("roles." + key + ".alwaysvisible", false);

      Role.listRoles().add(new Role(name, prefix, permission, alwaysVisible, broadcast));
    }

    if (Role.listRoles().isEmpty()) {
      Role.listRoles().add(new Role("&7Membro", "&7", "", false, false));
    }
  }

  private static Location lobby;

  public static void setLobby(Location location) {
    lobby = location;
  }

  public static Location getLobby() {
    return lobby;
  }

  public static Core getInstance() {
    return instance;
  }

  private static BukkitTask QUEUE_TASK;
  private static final List<QueuePlayer> QUEUE = new ArrayList<>();

  public static void sendServer(Profile profile, String name) {
    if (!Core.getInstance().isEnabled()) {
      return;
    }

    Player player = profile.getPlayer();
    if (player != null) {
      player.closeInventory();
      QueuePlayer qp = QUEUE.stream().filter(qps -> qps.player.equals(player)).findFirst().orElse(null);
      if (qp != null) {
        if (qp.server.equalsIgnoreCase(name)) {
          qp.player.sendMessage("§cVocê já está na fila de conexão!");
        } else {
          qp.server = name;
        }
        return;
      }

      qp = new QueuePlayer(player, name);
      if (player.hasPermission("mcore.queue")) {
        int index = QUEUE.stream().filter(qps -> !qps.player.hasPermission("mcore.queue")).map(QUEUE::indexOf).min(Integer::compare).orElse(-1);
        if (index != -1) {
          player.sendMessage("§aVocê passou na frente de alguns jogadores na Fila e começou na Posição #" + (index + 1) + ".");
        }
        QUEUE.add(index == -1 ? 0 : index, qp);
      } else {
        QUEUE.add(qp);
      }
      if (QUEUE_TASK == null) {
        QUEUE_TASK = new BukkitRunnable() {
          private boolean send;
          private QueuePlayer current;

          @Override
          public void run() {
            int id = 1;
            for (QueuePlayer qp : new ArrayList<>(QUEUE)) {
              if (!qp.player.isOnline()) {
                QUEUE.remove(qp);
                qp.destroy();
                continue;
              }

              NMS.sendActionBar(qp.player, "§aVocê está na Fila para conectar-se a §8" + qp.server + " §7(Posição #" + id + ")");
              id++;
            }

            if (this.current != null) {
              if (!this.current.player.isOnline()) {
                QUEUE.remove(this.current);
                this.current.destroy();
                this.current = null;
                return;
              }

              if (this.send) {
                this.current.player.closeInventory();
                NMS.sendActionBar(this.current.player, "");
                this.current.player.sendMessage("§aConectando...");
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("Connect");
                out.writeUTF(this.current.server);
                this.current.player.sendPluginMessage(Core.getInstance(), "BungeeCord", out.toByteArray());
                QUEUE.remove(this.current);
                this.current.destroy();
                this.current = null;
                return;
              }

              profile.saveSync();
              this.send = true;
              return;
            }

            if (!QUEUE.isEmpty()) {
              this.current = QUEUE.get(0);
              this.send = false;
            }
          }
        }.runTaskTimerAsynchronously(Core.getInstance(), 0, 20);
      }
    }
  }

  protected static class QueuePlayer {
    protected Player player;
    protected String server;

    public QueuePlayer(Player player, String server) {
      this.player = player;
      this.server = server;
    }

    public void destroy() {
      this.player = null;
      this.server = null;
    }
  }
}
