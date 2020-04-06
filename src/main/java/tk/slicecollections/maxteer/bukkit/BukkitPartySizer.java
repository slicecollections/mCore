package tk.slicecollections.maxteer.bukkit;

import org.bukkit.entity.Player;
import tk.slicecollections.maxteer.Core;
import tk.slicecollections.maxteer.plugin.config.MConfig;

import java.util.LinkedHashMap;
import java.util.Map;

public class BukkitPartySizer {

  private static final MConfig CONFIG;
  private static final Map<String, Integer> SIZES;

  static {
    CONFIG = Core.getInstance().getConfig("utils");
    if (!CONFIG.contains("party")) {
      CONFIG.createSection("party.size");
      CONFIG.set("party.size.role_master", 20);
      CONFIG.set("party.size.role_youtuber", 15);
      CONFIG.set("party.size.role_mvpplus", 10);
      CONFIG.set("party.size.role_mvp", 5);
    }

    SIZES = new LinkedHashMap<>();
    for (String key : CONFIG.getSection("party.size").getKeys(false)) {
      SIZES.put(key.replace("_", "."), CONFIG.getInt("party.size." + key));
    }
  }

  public static int getPartySize(Player player) {
    for (Map.Entry<String, Integer> entry : SIZES.entrySet()) {
      if (player.hasPermission(entry.getKey())) {
        return entry.getValue();
      }
    }

    return 3;
  }
}
