package tk.slicecollections.maxteer.player.fake;

import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import tk.slicecollections.maxteer.Core;
import tk.slicecollections.maxteer.libraries.profile.InvalidMojangException;
import tk.slicecollections.maxteer.libraries.profile.Mojang;
import tk.slicecollections.maxteer.player.role.Role;
import tk.slicecollections.maxteer.plugin.config.MConfig;
import tk.slicecollections.maxteer.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Maxter
 */
public class FakeManager {

  private static final MConfig CONFIG = Core.getInstance().getConfig("utils");
  private static Map<String, String> fakeNames = new HashMap<>();

  public static void applyFake(Player player, String fakeName) {
    if (!isBungeeSide()) {
      player.kickPlayer(StringUtils.formatColors(CONFIG.getString("fake.kick-apply")).replace("\\n", "\n"));
    }
    fakeNames.put(player.getName(), fakeName);
  }

  public static void removeFake(Player player) {
    if (!isBungeeSide()) {
      player.kickPlayer(StringUtils.formatColors(CONFIG.getString("fake.kick-remove")).replace("\\n", "\n"));
    }
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
    return !fakeNames.containsKey(name) && !fakeNames.containsValue(name) && Bukkit.getPlayer(name) == null && !CONFIG.getStringList("fake.blocked").contains(name);
  }

  private static Role role;

  public static Role getFakeRole() {
    if (role == null) {
      role = Role.getRoleByName(CONFIG.getString("fake.role"));
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
      randoms = CONFIG.getStringList("fake.randoms");
    }

    return randoms;
  }

  private static Boolean bungeeSide;

  public static boolean isBungeeSide() {
    if (bungeeSide == null) {
      bungeeSide = CONFIG.getBoolean("bungeecord");
    }

    return bungeeSide;
  }

  private static final Pattern REAL_PATTERN = Pattern.compile("(?i)mcorefakereal:\\w*");

  public static String replaceNickedPlayers(String original, boolean toFake) {
    String replaced = original;
    for (String name : listNicked()) {
      Matcher matcher = Pattern.compile("(?i)" + (toFake ? name : getFake(name))).matcher(replaced);

      while (matcher.find()) {
        replaced = replaced.replaceFirst(Pattern.quote(matcher.group()), Matcher.quoteReplacement(toFake ? getFake(name) : name));
      }
    }

    Matcher matcher = REAL_PATTERN.matcher(replaced);
    while (matcher.find()) {
      System.out.println(matcher.group());
      replaced = replaced.replaceFirst(Pattern.quote(matcher.group()), Matcher.quoteReplacement(
        fakeNames.entrySet().stream().filter(entry -> entry.getValue().equals(matcher.group().replace("mcorefakereal:", ""))).map(Map.Entry::getKey).findFirst().orElse("")));
    }
    return replaced;
  }

  public static WrappedGameProfile cloneProfile(WrappedGameProfile profile) {
    WrappedGameProfile gameProfile = profile.withName(getFake(profile.getName()));
    gameProfile.getProperties().clear();

    try {
      String id = Mojang.getUUID(gameProfile.getName());
      if (id != null) {
        String textures = Mojang.getSkinProperty(id);
        if (textures != null) {
          gameProfile.getProperties().put("textures", new WrappedSignedProperty(textures.split(" : ")[0], textures.split(" : ")[1], textures.split(" : ")[2]));
        }
      }
    } catch (InvalidMojangException ignore) {}

    return gameProfile;
  }
}
