package tk.slicecollections.maxteer.bungee;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.YamlConfiguration;
import tk.slicecollections.maxteer.bungee.cmd.Commands;
import tk.slicecollections.maxteer.bungee.listener.Listeners;
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

  public void saveDefaultConfig() {
    File file = new File("plugins/mCore/utils.yml");
    if (!file.exists()) {
      file.getParentFile().mkdirs();
      copyFile(Bungee.getInstance().getResourceAsStream("utils.yml"), file);
    }

    try {
      this.config = YamlConfiguration.getProvider(YamlConfiguration.class).load(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
    } catch (FileNotFoundException ex) {
      this.getLogger().log(Level.WARNING, "Cannot load utils.yml: ", ex);
    }
  }

  public Configuration getConfig() {
    return config;
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
