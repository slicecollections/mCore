package tk.slicecollections.maxteer;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.clip.placeholderapi.PlaceholderAPI;
import tk.slicecollections.maxteer.achievements.Achievement;
import tk.slicecollections.maxteer.booster.Booster;
import tk.slicecollections.maxteer.cmd.Commands;
import tk.slicecollections.maxteer.database.Database;
import tk.slicecollections.maxteer.deliveries.Delivery;
import tk.slicecollections.maxteer.hook.MCoreExpansion;
import tk.slicecollections.maxteer.libraries.MinecraftVersion;
import tk.slicecollections.maxteer.libraries.holograms.HologramLibrary;
import tk.slicecollections.maxteer.libraries.npclib.NPCLibrary;
import tk.slicecollections.maxteer.listeners.Listeners;
import tk.slicecollections.maxteer.nms.NMS;
import tk.slicecollections.maxteer.player.Profile;
import tk.slicecollections.maxteer.player.role.Role;
import tk.slicecollections.maxteer.plugin.MPlugin;
import tk.slicecollections.maxteer.servers.ServerItem;
import tk.slicecollections.maxteer.titles.Title;

public class Core extends MPlugin implements PluginMessageListener {

  private static Core instance;
  private static boolean validInit;
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

    PlaceholderAPI.registerExpansion(new MCoreExpansion());

    Database.setupDatabase();

    NPCLibrary.setupNPCs(this);
    HologramLibrary.setupHolograms(this);

    Role.setupRoles();
    Title.setupTitles();
    Booster.setupBoosters();
    Delivery.setupDeliveries();
    ServerItem.setupServers();
    Achievement.setupAchievements();

    Commands.setupCommands();
    Listeners.setupListeners();

    getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
    getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", this);

    validInit = true;
    this.getLogger().info("O plugin foi ativado.");
  }

  @Override
  public void disable() {
    if (validInit) {
      Profile.listProfiles().forEach(Profile::saveSync);
    }

    this.getLogger().info("O plugin foi desativado.");
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

  @Override
  public void onPluginMessageReceived(String channel, Player receiver, byte[] msg) {
    if (channel.equals("BungeeCord")) {
      ByteArrayDataInput in = ByteStreams.newDataInput(msg);

      String subChannel = in.readUTF();
      if (subChannel.equals("PlayerCount")) {
        try {
          String server = in.readUTF();
          ServerItem.SERVER_COUNT.put(server, in.readInt());
        } catch (Exception ignored) {}
      }
    }
  }

  public static void sendServer(Profile profile, String name) {
    Player player = profile.getPlayer();
    if (player != null) {
      profile.saveSync();
      player.closeInventory();
      ByteArrayDataOutput out = ByteStreams.newDataOutput();
      out.writeUTF("Connect");
      out.writeUTF(name);
      player.sendPluginMessage(Core.getInstance(), "BungeeCord", out.toByteArray());
    }
  }
}
