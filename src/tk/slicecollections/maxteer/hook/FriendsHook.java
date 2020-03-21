package tk.slicecollections.maxteer.hook;

import java.sql.SQLException;
import javax.sql.rowset.CachedRowSet;
import tk.slicecollections.maxteer.database.Database;

public class FriendsHook {

  private static final boolean mFriends;

  static {
    mFriends = Database.getInstance().query("SELECT * FROM INFORMATION_SCHEMA.STATISTICS WHERE table_name = 'mamigos_perfis'") != null;
  }

  public static boolean isFriend(String player, String friend) {
    if (mFriends) {
      CachedRowSet rs = Database.getInstance().query("SELECT `friends` FROM `mamigos_perfis` WHERE LOWER(`name`) = ?", player.toLowerCase());
      if (rs != null) {
        try {
          return rs.getString("friends").toLowerCase().contains("\"" + friend.toLowerCase() + "\"");
        } catch (SQLException ex) {}
      }
    }

    return false;
  }
  
  public static boolean isBlacklisted(String player, String blacklisted) {
    if (mFriends) {
      CachedRowSet rs = Database.getInstance().query("SELECT `blacklist` FROM `mamigos_perfis` WHERE LOWER(`name`) = ?", player.toLowerCase());
      if (rs != null) {
        try {
          return rs.getString("blacklist").toLowerCase().contains("\"" + blacklisted.toLowerCase() + "\"");
        } catch (SQLException ex) {}
      }
    }

    return false;
  }
}
