package tk.slicecollections.maxteer.bungee.party;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;
import tk.slicecollections.maxteer.bungee.Bungee;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class BungeePartySizer {

  private static final Configuration CONFIG;
  private static final Map<String, Integer> SIZES;

  static {
    CONFIG = Bungee.getInstance().getConfig();
    if (!CONFIG.contains("party")) {
      CONFIG.set("party.size", new HashMap<>());
      CONFIG.set("party.size.role_master", 20);
      CONFIG.set("party.size.role_youtuber", 15);
      CONFIG.set("party.size.role_mvpplus", 10);
      CONFIG.set("party.size.role_mvp", 5);
    }

    SIZES = new LinkedHashMap<>();
    for (String key : CONFIG.getSection("party.size").getKeys()) {
      SIZES.put(key.replace("_", "."), CONFIG.getInt("party.size." + key));
    }
  }

  public static int getPartySize(ProxiedPlayer player) {
    for (Map.Entry<String, Integer> entry : SIZES.entrySet()) {
      if (player.hasPermission(entry.getKey())) {
        return entry.getValue();
      }
    }

    return 3;
  }
}
