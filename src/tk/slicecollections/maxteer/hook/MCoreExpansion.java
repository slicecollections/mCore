package tk.slicecollections.maxteer.hook;

import org.bukkit.entity.Player;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import tk.slicecollections.maxteer.Core;
import tk.slicecollections.maxteer.player.Profile;
import tk.slicecollections.maxteer.player.enums.PlayerVisibility;
import tk.slicecollections.maxteer.utils.StringUtils;

@SuppressWarnings("all")
public class MCoreExpansion extends PlaceholderExpansion {

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

    if (params.equals("status_jogadores")) {
      return profile.getPreferencesContainer().getPlayerVisibility().getName();
    } else if (params.equals("status_jogadores_nome")) {
      if (profile.getPreferencesContainer().getPlayerVisibility() == PlayerVisibility.TODOS) {
        return "§aON";
      }

      return "§cOFF";
    } else if (params.equals("status_jogadores_inksack")) {
      return profile.getPreferencesContainer().getPlayerVisibility().getInkSack();
    }else if (params.startsWith("SkyWars_")) {
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
      } else if (value.equals("coins")) {
        return StringUtils.formatNumber(profile.getCoins(table));
      }
    }

    return null;
  }
}
