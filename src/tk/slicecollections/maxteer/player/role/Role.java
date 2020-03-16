package tk.slicecollections.maxteer.player.role;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.rowset.CachedRowSet;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import tk.slicecollections.maxteer.Core;
import tk.slicecollections.maxteer.database.Database;
import tk.slicecollections.maxteer.plugin.config.MConfig;
import tk.slicecollections.maxteer.utils.StringUtils;

public class Role {

  private int id;
  private String name;
  private String prefix;
  private String permission;

  private boolean broadcast;

  public Role(String name, String prefix, String permission, boolean broadcast) {
    this.id = ROLES.size();
    this.name = StringUtils.formatColors(name);
    this.prefix = StringUtils.formatColors(prefix);
    this.permission = permission;
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

      ROLES.add(new Role(name, prefix, permission, broadcast));
    }

    if (ROLES.size() == 0) {
      ROLES.add(new Role("&7Membro", "&7", "", false));
    }
  }
  
  public static String getPrefixed(String name) {
    return getTaggedName(name, false);
  }
  
  public static String getColored(String name) {
    return getTaggedName(name, true);
  }

  private static String getTaggedName(String name, boolean onlyColor) {
    String prefix = "&7";
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
        return prefix + rs.getString("name");
      } catch (SQLException ex) {}
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
    for (Role role : ROLES) {
      if (role.has(player)) {
        return role;
      }
    }

    return ROLES.get(ROLES.size() - 1);
  }
}
