package tk.slicecollections.maxteer.bungee;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.YamlConfiguration;
import tk.slicecollections.maxteer.bungee.cmd.Commands;
import tk.slicecollections.maxteer.bungee.listener.Listeners;
import tk.slicecollections.maxteer.database.Database;
import tk.slicecollections.maxteer.player.role.Role;
import tk.slicecollections.maxteer.utils.StringUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/**
 * @author Maxter
 */
public class Bungee extends Plugin {

  private static Bungee instance;

  public Bungee() {
    instance = this;
  }

  @Override
  public void onEnable() {
    saveDefaultConfig();

    Database.setupDatabase(
      config.getString("database.tipo"),
      config.getString("database.mysql.host"),
      config.getString("database.mysql.porta"),
      config.getString("database.mysql.nome"),
      config.getString("database.mysql.usuario"),
      config.getString("database.mysql.senha")
    );

    setupRoles();

    Commands.setupCommands();
    getProxy().getPluginManager().registerListener(this, new Listeners());

    getProxy().registerChannel("mCore");

    this.getLogger().info("O plugin foi ativado.");
  }

  @Override
  public void onDisable() {
    this.getLogger().info("O plugin foi desativado.");
  }

  private Configuration config;
  private Configuration utils;
  private Configuration roles;

  public void saveDefaultConfig() {
    for (String fileName : new String[] {"config", "roles", "utils"}) {
      File file = new File("plugins/mCore/" + fileName + ".yml");
      if (!file.exists()) {
        file.getParentFile().mkdirs();
        copyFile(Bungee.getInstance().getResourceAsStream(fileName + ".yml"), file);
      }

      try {
        if (fileName.equals("config")) {
          this.config = YamlConfiguration.getProvider(YamlConfiguration.class).load(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
        } else if (fileName.equals("utils")) {
          this.utils = YamlConfiguration.getProvider(YamlConfiguration.class).load(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
        } else {
          this.roles = YamlConfiguration.getProvider(YamlConfiguration.class).load(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
        }
      } catch (FileNotFoundException ex) {
        this.getLogger().log(Level.WARNING, "Cannot load " + fileName + ".yml: ", ex);
      }
    }
  }

  public Configuration getConfig() {
    return utils;
  }

  private void setupRoles() {
    for (String key : roles.getSection("roles").getKeys()) {
      String name = roles.getString("roles." + key + ".name");
      String prefix = roles.getString("roles." + key + ".prefix");
      String permission = roles.getString("roles." + key + ".permission");
      boolean broadcast = roles.getBoolean("roles." + key + ".broadcast", true);
      boolean alwaysVisible = roles.getBoolean("roles." + key + ".alwaysvisible", false);

      Role.listRoles().add(new Role(name, prefix, permission, alwaysVisible, broadcast));
    }

    if (Role.listRoles().isEmpty()) {
      Role.listRoles().add(new Role("&7Membro", "&7", "", false, false));
    }
  }

  public static Bungee getInstance() {
    return instance;
  }

  private static Map<String, String> fakeNames = new HashMap<>();

  public static void applyFake(ProxiedPlayer player, String fakeName) {
    player.disconnect(TextComponent.fromLegacyText(StringUtils.formatColors(getInstance().getConfig().getString("fake.kick-apply")).replace("\\n", "\n")));
    fakeNames.put(player.getName(), fakeName);
  }

  public static void removeFake(ProxiedPlayer player) {
    player.disconnect(TextComponent.fromLegacyText(StringUtils.formatColors(getInstance().getConfig().getString("fake.kick-remove")).replace("\\n", "\n")));
    fakeNames.remove(player.getName());
  }

  public static String getCurrent(String playerName) {
    return isFake(playerName) ? getFake(playerName) : playerName;
  }

  public static String getFake(String playerName) {
    return fakeNames.get(playerName);
  }

  public static boolean isFake(String playerName) {
    return fakeNames.containsKey(playerName);
  }

  public static boolean isUsable(String name) {
    return !fakeNames.containsKey(name) && !fakeNames.containsValue(name) && getInstance().getProxy().getPlayer(name) == null && !getInstance().getConfig()
      .getStringList("fake.blocked").contains(name);
  }

  private static Role role;

  public static Role getFakeRole() {
    if (role == null) {
      role = Role.getRoleByName(getInstance().getConfig().getString("fake.role"));
      if (role == null) {
        role = Role.getLastRole();
      }
    }

    return role;
  }

  public static List<String> listNicked() {
    return new ArrayList<>(fakeNames.keySet());
  }

  private static List<String> randoms;

  public static List<String> getRandomNicks() {
    if (randoms == null) {
      randoms = getInstance().getConfig().getStringList("fake.randoms");
    }

    return randoms;
  }

  /**
   * Copia um arquivo a partir de um InputStream.
   *
   * @param input O input para ser copiado.
   * @param out   O arquivo destinario.
   */
  public static void copyFile(InputStream input, File out) {
    FileOutputStream ou = null;
    try {
      ou = new FileOutputStream(out);
      byte[] buff = new byte[1024];
      int len;
      while ((len = input.read(buff)) > 0) {
        ou.write(buff, 0, len);
      }
    } catch (IOException ex) {
      getInstance().getLogger().log(Level.WARNING, "Failed at copy file " + out.getName() + "!", ex);
    } finally {
      try {
        if (ou != null) {
          ou.close();
        }
        if (input != null) {
          input.close();
        }
      } catch (IOException ignore) {}
    }
  }
}
