package tk.slicecollections.maxteer.player.role;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import tk.slicecollections.maxteer.Core;
import tk.slicecollections.maxteer.database.Database;
import tk.slicecollections.maxteer.player.fake.FakeManager;
import tk.slicecollections.maxteer.plugin.config.MConfig;
import tk.slicecollections.maxteer.utils.StringUtils;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Maxter
 */
public class Role {

  private int id;
  private String name;
  private String prefix;
  private String permission;

  private boolean alwaysVisible;
  private boolean broadcast;

  public Role(String name, String prefix, String permission, boolean alwaysVisible, boolean broadcast) {
    this.id = ROLES.size();
    this.name = StringUtils.formatColors(name);
    this.prefix = StringUtils.formatColors(prefix);
    this.permission = permission;
    this.alwaysVisible = alwaysVisible;
    this.broadcast = broadcast;
  }

  public int getId() {
    return this.id;
  }

  public String getName() {
    return this.name;
  }

  public String getPrefix() {
    return this.prefix;
  }

  public String getPermission() {
    return this.permission;
  }

  public boolean isDefault() {
    return this.permission.isEmpty();
  }

  public boolean isAlwaysVisible() {
    return this.alwaysVisible;
  }

  public boolean isBroadcast() {
    return this.broadcast;
  }

  public boolean has(Player player) {
    return this.isDefault() || player.hasPermission(this.permission);
  }

  private static final List<Role> ROLES = new ArrayList<>();

  public static void setupRoles() {
    MConfig config = Core.getInstance().getConfig("roles");
    for (String key : config.getSection("roles").getKeys(false)) {
      String name = config.getString("roles." + key + ".name");
      String prefix = config.getString("roles." + key + ".prefix");
      String permission = config.getString("roles." + key + ".permission");
      boolean broadcast = config.getBoolean("roles." + key + ".broadcast", true);
      boolean alwaysVisible = config.getBoolean("roles." + key + ".alwaysvisible", false);

      ROLES.add(new Role(name, prefix, permission, alwaysVisible, broadcast));
    }

    if (ROLES.size() == 0) {
      ROLES.add(new Role("&7Membro", "&7", "", false, false));
    }
  }

  public static String getPrefixed(String name) {
    return getPrefixed(name, false);
  }

  public static String getColored(String name) {
    return getColored(name, false);
  }

  public static String getPrefixed(String name, boolean removeFake) {
    return getTaggedName(name, false, removeFake);
  }

  public static String getColored(String name, boolean removeFake) {
    return getTaggedName(name, true, removeFake);
  }

  private static String getTaggedName(String name, boolean onlyColor, boolean removeFake) {
    String prefix = "&7";
    if (!removeFake && FakeManager.isFake(name)) {
      prefix = FakeManager.getFakeRole().getPrefix();
      if (onlyColor) {
        prefix = StringUtils.getLastColor(prefix);
      }

      return prefix + FakeManager.getFake(name);
    }

    Player target = Bukkit.getPlayerExact(name);
    if (target != null) {
      prefix = getPlayerRole(target).getPrefix();
      if (onlyColor) {
        prefix = StringUtils.getLastColor(prefix);
      }
      return prefix + name;
    }

    CachedRowSet rs = Database.getInstance().query("SELECT `name`, `role` FROM `mCoreProfile` WHERE LOWER(`name`) = ?", name.toLowerCase());
    if (rs != null) {
      try {
        prefix = getRoleByName(rs.getString("role")).getPrefix();
        if (onlyColor) {
          prefix = StringUtils.getLastColor(prefix);
        }
        name = rs.getString("name");
        if (!removeFake && FakeManager.isFake(name)) {
          name = FakeManager.getFake(name);
        }
        return prefix + name;
      } catch (SQLException ignored) {}
    }

    return prefix + name;
  }

  public static Role getRoleByName(String name) {
    for (Role role : ROLES) {
      if (StringUtils.stripColors(role.getName()).equalsIgnoreCase(name)) {
        return role;
      }
    }

    return ROLES.get(ROLES.size() - 1);
  }

  public static Role getRoleByPermission(String permission) {
    for (Role role : ROLES) {
      if (role.getPermission().equals(permission)) {
        return role;
      }
    }

    return null;
  }

  public static Role getPlayerRole(Player player) {
    return getPlayerRole(player, false);
  }

  public static Role getPlayerRole(Player player, boolean removeFake) {
    if (!removeFake && FakeManager.isFake(player.getName())) {
      return FakeManager.getFakeRole();
    }

    for (Role role : ROLES) {
      if (role.has(player)) {
        return role;
      }
    }

    return getLastRole();
  }

  public static Role getLastRole() {
    return ROLES.get(ROLES.size() - 1);
  }
}
