package tk.slicecollections.maxteer.player.hotbar;

import static tk.slicecollections.maxteer.listeners.Listeners.DELAY_PLAYERS;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.entity.Player;
import tk.slicecollections.maxteer.menus.MenuProfile;
import tk.slicecollections.maxteer.menus.MenuServers;
import tk.slicecollections.maxteer.player.Profile;
import tk.slicecollections.maxteer.utils.StringUtils;

/**
 * @author Maxter
 */
public abstract class HotbarActionType {

  public abstract void execute(Profile profile, String action);

  private static Map<String, HotbarActionType> actionTypes = new HashMap<>();
  private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("###.#");

  static {
    actionTypes.put("comando", new HotbarActionType() {

      @Override
      public void execute(Profile profile, String action) {
        profile.getPlayer().performCommand(action);
      }
    });
    actionTypes.put("mensagem", new HotbarActionType() {

      @Override
      public void execute(Profile profile, String action) {
        profile.getPlayer().sendMessage(StringUtils.formatColors(action).replace("\\n", "\n"));
      }
    });
    actionTypes.put("core", new HotbarActionType() {
      
      @Override
      public void execute(Profile profile, String action) {
        if (action.equalsIgnoreCase("jogos")) {
          new MenuServers(profile);
        } else if (action.equalsIgnoreCase("perfil")) {
          new MenuProfile(profile);
        } else if (action.equalsIgnoreCase("jogadores")) {
          Player player = profile.getPlayer();
          long start = DELAY_PLAYERS.containsKey(player.getName()) ? DELAY_PLAYERS.get(player.getName()) : 0;
          if (start > System.currentTimeMillis()) {
            double time = (start - System.currentTimeMillis()) / 1000.0;
            if (time > 0.1) {
              String timeString = DECIMAL_FORMAT.format(time).replace(",", ".");
              if (timeString.endsWith("0")) {
                timeString = timeString.substring(0, timeString.lastIndexOf("."));
              }

              player.sendMessage("§cVocê precisa aguardar mais " + timeString + "s para utilizar novamente.");
              return;
            }
          }

          DELAY_PLAYERS.put(player.getName(), System.currentTimeMillis() + 3000);
          profile.getPreferencesContainer().changePlayerVisibility();
          switch (profile.getPreferencesContainer().getPlayerVisibility()) {
            case TODOS:
              player.sendMessage("§aVisibilidade de jogadores ativada.");
              break;
            case NENHUM:
              player.sendMessage("§cVisibilidade de jogadores desativada.");
              break;
          }
          profile.refreshPlayers();
        }
      }
    });
  }

  public static void addActionType(String name, HotbarActionType actionType) {
    actionTypes.put(name.toLowerCase(), actionType);
  }

  public static HotbarActionType fromName(String name) {
    return actionTypes.get(name.toLowerCase());
  }
}
