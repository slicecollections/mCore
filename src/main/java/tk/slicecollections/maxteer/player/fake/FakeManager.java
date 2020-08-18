package tk.slicecollections.maxteer.player.fake;

import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.BookMeta;
import tk.slicecollections.maxteer.Core;
import tk.slicecollections.maxteer.player.role.Role;
import tk.slicecollections.maxteer.plugin.config.MConfig;
import tk.slicecollections.maxteer.utils.BukkitUtils;
import tk.slicecollections.maxteer.utils.StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Maxter
 */
public class FakeManager {

  public static final String STEVE =
    "eyJ0aW1lc3RhbXAiOjE1ODcxNTAzMTc3MjAsInByb2ZpbGVJZCI6IjRkNzA0ODZmNTA5MjRkMzM4NmJiZmM5YzEyYmFiNGFlIiwicHJvZmlsZU5hbWUiOiJzaXJGYWJpb3pzY2hlIiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS8xYTRhZjcxODQ1NWQ0YWFiNTI4ZTdhNjFmODZmYTI1ZTZhMzY5ZDE3NjhkY2IxM2Y3ZGYzMTlhNzEzZWI4MTBiIn19fQ==:syZ2Mt1vQeEjh/t8RGbv810mcfTrhQvnwEV7iLCd+5udVeroTa5NjoUehgswacTML3k/KxHZHaq4o6LmACHwsj/ivstW4PWc2RmVn+CcOoDKI3ytEm70LvGz0wAaTVKkrXHSw/RbEX/b7g7oQ8F67rzpiZ1+Z3TKaxbgZ9vgBQZQdwRJjVML2keI0669a9a1lWq3V/VIKFZc1rMJGzETMB2QL7JVTpQFOH/zXJGA+hJS5bRol+JG3LZTX93+DililM1e8KEjKDS496DYhMAr6AfTUfirLAN1Jv+WW70DzIpeKKXWR5ZeI+9qf48+IvjG8DhRBVFwwKP34DADbLhuebrolF/UyBIB9sABmozYdfit9uIywWW9+KYgpl2EtFXHG7CltIcNkbBbOdZy0Qzq62Tx6z/EK2acKn4oscFMqrobtioh5cA/BCRb9V4wh0fy5qx6DYHyRBdzLcQUfb6DkDx1uyNJ7R5mO44b79pSo8gdd9VvMryn/+KaJu2UvyCrMVUtOOzoIh4nCMc9wXOFW3jZ7ZTo4J6c28ouL98rVQSAImEd/P017uGvWIT+hgkdXnacVG895Y6ilXqJToyvf1JUQb4dgry0WTv6UTAjNgrm5a8mZx9OryLuI2obas97LCon1rydcNXnBtjUk0TUzdrvIa5zNstYZPchUb+FSnU=";
  public static final String ALEX =
    "eyJ0aW1lc3RhbXAiOjE1ODcxMzkyMDU4MzUsInByb2ZpbGVJZCI6Ijc1MTQ0NDgxOTFlNjQ1NDY4Yzk3MzlhNmUzOTU3YmViIiwicHJvZmlsZU5hbWUiOiJUaGFua3NNb2phbmciLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzNiNjBhMWY2ZDU2MmY1MmFhZWJiZjE0MzRmMWRlMTQ3OTMzYTNhZmZlMGU3NjRmYTQ5ZWEwNTc1MzY2MjNjZDMiLCJtZXRhZGF0YSI6eyJtb2RlbCI6InNsaW0ifX19fQ==:W60UUuAYlWfLFt5Ay3Lvd/CGUbKuuU8+HTtN/cZLhc0BC22XNgbY1btTite7ZtBUGiZyFOhYqQi+LxVWrdjKEAdHCSYWpCRMFhB1m0zEfu78yg4XMcFmd1v7y9ZfS45b3pLAJ463YyjDaT64kkeUkP6BUmgsTA2iIWvM33k6Tj3OAM39kypFSuH+UEpkx603XtxratD+pBjUCUvWyj2DMxwnwclP/uACyh0ZVrI7rC5xJn4jSura+5J2/j6Z/I7lMBBGLESt7+pGn/3/kArDE/1RShOvm5eYKqrTMRfK4n3yd1U1DRsMzxkU2AdlCrv1swT4o+Cq8zMI97CF/xyqk8z2L98HKlzLjtvXIE6ogljyHc9YsfU9XhHwZ7SKXRNkmHswOgYIQCSa1RdLHtlVjN9UdUyUoQIIO2AWPzdKseKJJhXwqKJ7lzfAtStErRzDjmjr7ld/5tFd3TTQZ8yiq3D6aRLRUnOMTr7kFOycPOPhOeZQlTjJ6SH3PWFsdtMMQsGzb2vSukkXvJXFVUM0TcwRZlqT5MFHyKBBPprIt0wVN6MmSKc8m5kdk7ZBU2ICDs/9Cd/fyzAIRDu3Kzm7egbAVK9zc1kXwGzowUkGGy1XvZxyRS5jF1zu6KzVgaXOGcrOLH4z/OHzxvbyW22/UwahWGN7MD4j37iJ7gjZDrk=";
  private static final MConfig CONFIG = Core.getInstance().getConfig("utils");
  public static Map<String, String> fakeNames = new HashMap<>();
  public static Map<String, Role> fakeRoles = new HashMap<>();
  public static Map<String, String> fakeSkins = new HashMap<>();

  private static TextComponent FAKE_ROLES;
  private static TextComponent FAKE_SKINS;

  public static void setupFake() {
    if (CONFIG.get("fake.role") instanceof  String) {
      CONFIG.set("fake.role", Arrays.asList(CONFIG.getString("fake.role")));
    }
    FAKE_ROLES = new TextComponent("");
    for (BaseComponent component : TextComponent.fromLegacyText("§5§lALTERAR NICKNAME\n \n§0Escolha o cargo que gostaria de utilizar enquanto está disfarçado:\n ")) {
      FAKE_ROLES.addExtra(component);
    }
    for (String roleName : CONFIG.getStringList("fake.role")) {
      Role role = Role.getRoleByName(roleName);
      if (role != null) {
        TextComponent component = new TextComponent("\n §0▪ " + role.getName());
        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("§7Seu nickname será exibido como '" + role.getPrefix() + "Nickname'§7.")));
        component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/fake " + roleName));
        FAKE_ROLES.addExtra(component);
      }
    }
    FAKE_SKINS = new TextComponent("");
    for (BaseComponent component : TextComponent.fromLegacyText("§5§lALTERAR NICKNAME\n \n§0Enquanto disfarçado, sua skin será alterada para ajudar a te camuflar.\n \n§0Escolha sua skin:\n ")) {
      FAKE_SKINS.addExtra(component);
    }
    TextComponent STEVE = new TextComponent("\n §0▪ §7Steve");
    STEVE.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("§7Você irá obter a aparência de Steve.")));
    STEVE.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/fake {role} steve"));
    TextComponent ALEX = new TextComponent("\n §0▪ §7Alex");
    ALEX.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("§7Você irá obter a aprência da Alex.")));
    ALEX.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/fake {role} alex"));
    FAKE_SKINS.addExtra(STEVE);
    FAKE_SKINS.addExtra(ALEX);
  }

  public static void sendRole(Player player) {
    ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
    BookMeta meta = (BookMeta) book.getItemMeta();
    meta.setAuthor("whereismywaifu");
    meta.setTitle("Escolher cargo");
    book.setItemMeta(meta);
    book = BukkitUtils.setNBTList(book, "pages", Collections.singletonList(ComponentSerializer.toString(FAKE_ROLES)));
    BukkitUtils.openBook(player, book);
  }

  public static void sendSkin(Player player, String role) {
    ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
    BookMeta meta = (BookMeta) book.getItemMeta();
    meta.setAuthor("whereismywaifu");
    meta.setTitle("Escolher cargo");
    book.setItemMeta(meta);
    book = BukkitUtils.setNBTList(book, "pages", Collections.singletonList(ComponentSerializer.toString(FAKE_SKINS).replace("{role}", role)));
    BukkitUtils.openBook(player, book);
  }

  public static void applyFake(Player player, String fakeName, String role, String skin) {
    if (!isBungeeSide()) {
      player.kickPlayer(StringUtils.formatColors(CONFIG.getString("fake.kick-apply")).replace("\\n", "\n"));
    }
    fakeNames.put(player.getName(), fakeName);
    fakeRoles.put(player.getName(), Role.getRoleByName(role));
    fakeSkins.put(player.getName(), skin);
  }

  public static void removeFake(Player player) {
    if (!isBungeeSide()) {
      player.kickPlayer(StringUtils.formatColors(CONFIG.getString("fake.kick-remove")).replace("\\n", "\n"));
    }
    fakeNames.remove(player.getName());
    fakeRoles.remove(player.getName());
    fakeSkins.remove(player.getName());
  }

  public static String getCurrent(String playerName) {
    return isFake(playerName) ? getFake(playerName) : playerName;
  }

  public static String getFake(String playerName) {
    return fakeNames.get(playerName);
  }

  public static Role getRole(String playerName) {
    return fakeRoles.getOrDefault(playerName, Role.getLastRole());
  }

  public static String getSkin(String playerName) {
    return fakeSkins.getOrDefault(playerName, STEVE);
  }

  public static boolean isFake(String playerName) {
    return fakeNames.containsKey(playerName);
  }

  public static boolean isUsable(String name) {
    return !fakeNames.containsKey(name) && !fakeNames.containsValue(name) && Bukkit.getPlayer(name) == null;
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

  public static boolean isFakeRole(String roleName) {
    return CONFIG.getStringList("fake.role").stream().anyMatch(role -> role.equalsIgnoreCase(roleName));
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
      replaced = replaced.replaceFirst(Pattern.quote(matcher.group()), Matcher.quoteReplacement(
        fakeNames.entrySet().stream().filter(entry -> entry.getValue().equals(matcher.group().replace("mcorefakereal:", ""))).map(Map.Entry::getKey).findFirst().orElse("")));
    }
    return replaced;
  }

  public static WrappedGameProfile cloneProfile(WrappedGameProfile profile) {
    WrappedGameProfile gameProfile = profile.withName(getFake(profile.getName()));
    gameProfile.getProperties().clear();

    try {
      String[] texture = FakeManager.getSkin(profile.getName()).split(":");
      gameProfile.getProperties().put("textures", new WrappedSignedProperty("textures", texture[0], texture[1]));
    } catch (Exception ignore) {}

    return gameProfile;
  }
}
