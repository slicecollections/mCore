package tk.slicecollections.maxteer.hook;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import tk.slicecollections.maxteer.Core;
import tk.slicecollections.maxteer.cash.CashManager;
import tk.slicecollections.maxteer.player.Profile;
import tk.slicecollections.maxteer.player.enums.PlayerVisibility;
import tk.slicecollections.maxteer.player.role.Role;
import tk.slicecollections.maxteer.servers.ServerItem;
import tk.slicecollections.maxteer.utils.StringUtils;

import java.text.SimpleDateFormat;

/**
 * @author Maxter
 */
@SuppressWarnings("all")
public class MCoreExpansion extends PlaceholderExpansion {

  private static final SimpleDateFormat MURDER_FORMAT = new SimpleDateFormat("mm:ss");

  @Override
  public boolean canRegister() {
    return true;
  }

  @Override
  public String getAuthor() {
    return "Maxteer";
  }

  @Override
  public String getIdentifier() {
    return "mCore";
  }

  @Override
  public String getVersion() {
    return Core.getInstance().getDescription().getVersion();
  }

  @Override
  public String onPlaceholderRequest(Player player, String params) {
    Profile profile = null;
    if (player == null || (profile = Profile.getProfile(player.getName())) == null) {
      return "";
    }

    if (params.startsWith("online")) {
      if (params.contains("online_")) {
        String server = params.replace("online_", "");
        ServerItem si = ServerItem.getServerItem(server);
        if (si != null) {
          return StringUtils.formatNumber(si.getBalancer().getTotalNumber());
        }

        return "entry invalida";
      }

      long online = 0;
      for (ServerItem si : ServerItem.listServers()) {
        online += si.getBalancer().getTotalNumber();
      }
      return StringUtils.formatNumber(online);
    } else if (params.equals("role")) {
      return Role.getPlayerRole(player).getName();
    } else if (params.equals("cash")) {
      return StringUtils.formatNumber(CashManager.getCash(player));
    } else if (params.equals("status_jogadores")) {
      return profile.getPreferencesContainer().getPlayerVisibility().getName();
    } else if (params.equals("status_jogadores_nome")) {
      if (profile.getPreferencesContainer().getPlayerVisibility() == PlayerVisibility.TODOS) {
        return "§aON";
      }

      return "§cOFF";
    } else if (params.equals("status_jogadores_inksack")) {
      return profile.getPreferencesContainer().getPlayerVisibility().getInkSack();
    } else if (params.startsWith("SkyWars_")) {
      String table = "mCoreSkyWars";
      String value = params.replace("SkyWars_", "");
      if (value.equals("kills") || value.equals("deaths") || value.equals("assists") || value.equals("games") || value.equals("wins")) {
        return StringUtils.formatNumber(profile.getStats(table, "1v1" + value, "2v2" + value));
      } else if (value.equals("1v1kills") || value.equals("1v1deaths") || value.equals("1v1assists") || value.equals("1v1games") || value.equals("1v1wins")) {
        return StringUtils.formatNumber(profile.getStats(table, value));
      } else if (value.equals("2v2kills") || value.equals("2v2deaths") || value.equals("2v2assists") || value.equals("2v2games") || value.equals("2v2wins")) {
        return StringUtils.formatNumber(profile.getStats(table, value));
      } else if (value.equals("coins")) {
        return StringUtils.formatNumber(profile.getCoins(table));
      }
    } else if (params.startsWith("TheBridge_")) {
      String table = "mCoreTheBridge";
      String value = params.replace("TheBridge_", "");
      if (value.equals("kills") || value.equals("deaths") || value.equals("games") || value.equals("points") || value.equals("wins")) {
        return StringUtils.formatNumber(profile.getStats(table, "1v1" + value, "2v2" + value));
      } else if (value.equals("1v1kills") || value.equals("1v1deaths") || value.equals("1v1games") || value.equals("1v1points") || value.equals("1v1wins")) {
        return StringUtils.formatNumber(profile.getStats(table, value));
      } else if (value.equals("2v2kills") || value.equals("2v2deaths") || value.equals("2v2games") || value.equals("2v2points") || value.equals("2v2wins")) {
        return StringUtils.formatNumber(profile.getStats(table, value));
      } else if (value.equals("winstreak")) {
        return StringUtils.formatNumber(profile.getDailyStats(table, "laststreak", value));
      } else if (value.equals("coins")) {
        return StringUtils.formatNumber(profile.getCoins(table));
      }
    } else if (params.startsWith("Murder_")) {
      String table = "mCoreMurder";
      String value = params.replace("Murder_", "");
      if (value.startsWith("classic_")) {
        String data = value.replace("classic_", "");
        if (data.equals("kills") || data.equals("bowkills") || data.equals("knifekills") || data.equals("thrownknifekills") || data.equals("wins") || data
          .equals("detectivewins") || data.equals("killerwins")) {
          return StringUtils.formatNumber(profile.getStats(table, "cl" + data));
        } else if (data.equals("quickestdetective") || data.equals("quickestkiller")) {
          return MURDER_FORMAT.format(profile.getStats(table, "cl" + data) * 1000);
        }
      } else if (value.startsWith("assassins_")) {
        String data = value.replace("assassins_", "");
        if (data.equals("kills") || data.equals("thrownknifekills") || data.equals("wins")) {
          return StringUtils.formatNumber(profile.getStats(table, "as" + data));
        }
      } else if (value.equals("coins")) {
        return StringUtils.formatNumber(profile.getCoins(table));
      }
    }

    return null;
  }
}
