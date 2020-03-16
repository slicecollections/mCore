package tk.slicecollections.maxteer.listeners;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import tk.slicecollections.maxteer.Core;
import tk.slicecollections.maxteer.player.Profile;
import tk.slicecollections.maxteer.player.hotbar.HotbarButton;
import tk.slicecollections.maxteer.plugin.logger.MLogger;
import tk.slicecollections.maxteer.titles.TitleManager;

public class Listeners implements Listener {

  public static final MLogger LOGGER = ((MLogger) Core.getInstance().getLogger()).getModule("Listeners");
  public static final Map<String, Long> DELAY_PLAYERS = new HashMap<>();

  public static void setupListeners() {
    Bukkit.getPluginManager().registerEvents(new Listeners(), Core.getInstance());
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onPlayerLogin(PlayerLoginEvent evt) {
    LOGGER.run(Level.SEVERE, "Could not pass PlayerLoginEvent for ${n} v${v}", () -> {
      if (evt.getResult() == Result.ALLOWED) {
        Profile.createOrLoadProfile(evt.getPlayer().getName());
      }
    });
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onPlayerQuit(PlayerQuitEvent evt) {
    LOGGER.run(Level.SEVERE, "Could not pass PlayerQuitEvent for ${n} v${v}", () -> {
      Profile profile = Profile.unloadProfile(evt.getPlayer().getName());
      if (profile != null) {
        if (profile.getGame() != null) {
          profile.getGame().leave(profile, profile.getGame());
        } else {
          TitleManager.leaveServer(profile);
        }
        profile.save();
        profile.destroy();
      }

      DELAY_PLAYERS.remove(evt.getPlayer().getName());
    });
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onInventoryClick(InventoryClickEvent evt) {
    if (evt.getWhoClicked() instanceof Player) {
      Player player = (Player) evt.getWhoClicked();
      Profile profile = Profile.getProfile(player.getName());

      if (profile != null && profile.getHotbar() != null) {
        ItemStack item = evt.getCurrentItem();
        if (item != null && item.getType() != Material.AIR) {
          if (evt.getClickedInventory() != null && evt.getClickedInventory().equals(player.getInventory()) && item.hasItemMeta()) {
            HotbarButton button = profile.getHotbar().compareButton(player, item);
            if (button != null) {
              evt.setCancelled(true);
              button.getAction().execute(profile);
            }
          }
        }
      }
    }
  }

  @EventHandler
  public void onPlayerInteract(PlayerInteractEvent evt) {
    Player player = evt.getPlayer();
    Profile profile = Profile.getProfile(player.getName());

    if (profile != null && profile.getHotbar() != null) {
      ItemStack item = player.getItemInHand();
      if (item != null && item.hasItemMeta()) {
        HotbarButton button = profile.getHotbar().compareButton(player, item);
        if (button != null) {
          evt.setCancelled(true);
          button.getAction().execute(profile);
        }
      }
    }
  }
}
