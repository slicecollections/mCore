package tk.slicecollections.maxteer;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import tk.slicecollections.maxteer.player.role.Role;
import tk.slicecollections.maxteer.reflection.Accessors;
import tk.slicecollections.maxteer.reflection.acessors.MethodAccessor;

import java.lang.reflect.Array;

public class Manager {

  public static boolean BUNGEE;

  private static Object PROXY_SERVER;

  private static MethodAccessor GET_NAME;
  private static MethodAccessor GET_PLAYER;
  private static MethodAccessor GET_SPIGOT;
  private static MethodAccessor HAS_PERMISSION;
  private static MethodAccessor SEND_MESSAGE;
  private static MethodAccessor SEND_MESSAGE_COMPONENTS;

  private static MethodAccessor IS_FAKE;
  private static MethodAccessor GET_CURRENT;
  private static MethodAccessor GET_FAKE;
  private static MethodAccessor GET_FAKE_ROLE;

  static {
    try {
      Class<?> proxyServer = Class.forName("net.md_5.bungee.api.ProxyServer");
      Class<?> proxiedPlayer = Class.forName("net.md_5.bungee.api.connection.ProxiedPlayer");
      Class<?> bungeeMain = Class.forName("tk.slicecollections.maxteer.bungee.Bungee");
      PROXY_SERVER = Accessors.getMethod(proxyServer, "getInstance").invoke(null);
      GET_NAME = Accessors.getMethod(proxiedPlayer, "getName");
      GET_PLAYER = Accessors.getMethod(proxyServer, "getPlayer", String.class);
      HAS_PERMISSION = Accessors.getMethod(proxiedPlayer, "hasPermission", String.class);
      SEND_MESSAGE_COMPONENTS = Accessors.getMethod(proxiedPlayer, "sendMessage", BaseComponent[].class);
      IS_FAKE = Accessors.getMethod(bungeeMain, "isFake", String.class);
      GET_CURRENT = Accessors.getMethod(bungeeMain, "getCurrent", String.class);
      GET_FAKE = Accessors.getMethod(bungeeMain, "getFake", String.class);
      GET_FAKE_ROLE = Accessors.getMethod(bungeeMain, "getRole", String.class);
      BUNGEE = true;
    } catch (ClassNotFoundException ignore) {
      try {
        Class<?> player = Class.forName("org.bukkit.entity.Player");
        Class<?> spigot = Class.forName("org.bukkit.entity.Player$Spigot");
        Class<?> fakeManager = Class.forName("tk.slicecollections.maxteer.player.fake.FakeManager");
        GET_NAME = Accessors.getMethod(player, "getName");
        GET_PLAYER = Accessors.getMethod(Class.forName("org.bukkit.Bukkit"), "getPlayer", String.class);
        HAS_PERMISSION = Accessors.getMethod(player, "hasPermission", String.class);
        SEND_MESSAGE = Accessors.getMethod(player, "sendMessage", String.class);
        GET_SPIGOT = Accessors.getMethod(player, "spigot");
        SEND_MESSAGE_COMPONENTS = Accessors.getMethod(spigot, "sendMessage", BaseComponent[].class);
        IS_FAKE = Accessors.getMethod(fakeManager, "isFake", String.class);
        GET_CURRENT = Accessors.getMethod(fakeManager, "getCurrent", String.class);
        GET_FAKE = Accessors.getMethod(fakeManager, "getFake", String.class);
        GET_FAKE_ROLE = Accessors.getMethod(fakeManager, "getRole", String.class);
      } catch (ClassNotFoundException ex) {
        ex.printStackTrace();
      }
    }
  }

  public static void sendMessage(Object player, String message) {
    if (BUNGEE) {
      sendMessage(player, TextComponent.fromLegacyText(message));
      return;
    }

    SEND_MESSAGE.invoke(player, message);
  }

  public static void sendMessage(Object player, BaseComponent... components) {
    SEND_MESSAGE_COMPONENTS.invoke(BUNGEE ? player : GET_SPIGOT.invoke(player), new Object[] {components});
  }

  public static String getName(Object player) {
    return (String) GET_NAME.invoke(player);
  }

  public static Object getPlayer(String name) {
    return GET_PLAYER.invoke(BUNGEE ? PROXY_SERVER : null, name);
  }

  public static String getCurrent(String playerName) {
    return (String) GET_CURRENT.invoke(null, playerName);
  }

  public static String getFake(String playerName) {
    return (String) GET_FAKE.invoke(null, playerName);
  }

  public static Role getFakeRole(String playerName) {
    return (Role) GET_FAKE_ROLE.invoke(null, playerName);
  }

  public static boolean hasPermission(Object player, String permission) {
    return (boolean) HAS_PERMISSION.invoke(player, permission);
  }

  public static boolean isFake(String playerName) {
    return (boolean) IS_FAKE.invoke(null, playerName);
  }
}
