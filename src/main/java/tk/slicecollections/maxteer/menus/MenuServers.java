package tk.slicecollections.maxteer.menus;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import tk.slicecollections.maxteer.Core;
import tk.slicecollections.maxteer.libraries.menu.UpdatablePlayerMenu;
import tk.slicecollections.maxteer.player.Profile;
import tk.slicecollections.maxteer.servers.ServerItem;
import tk.slicecollections.maxteer.utils.BukkitUtils;
import tk.slicecollections.maxteer.utils.StringUtils;

import static tk.slicecollections.maxteer.servers.ServerItem.*;

/**
 * @author Maxter
 */
public class MenuServers extends UpdatablePlayerMenu {

  @EventHandler
  public void onInventoryClick(InventoryClickEvent evt) {
    if (evt.getInventory().equals(this.getInventory())) {
      evt.setCancelled(true);

      if (evt.getWhoClicked().equals(this.player)) {
        Profile profile = Profile.getProfile(this.player.getName());
        if (profile == null) {
          this.player.closeInventory();
          return;
        }

        if (evt.getClickedInventory() != null && evt.getClickedInventory().equals(this.getInventory())) {
          ItemStack item = evt.getCurrentItem();

          if (item != null && item.getType() != Material.AIR) {
            if (DISABLED_SLOTS.contains(evt.getSlot())) {
              this.player.sendMessage("§cVocê já está conectado a este servidor.");
              return;
            }

            listServers().stream().filter(s -> s.getSlot() == evt.getSlot()).findFirst().ifPresent(serverItem -> serverItem.connect(profile));
          }
        }
      }
    }
  }

  public MenuServers(Profile profile) {
    super(profile.getPlayer(), CONFIG.getString("title"), CONFIG.getInt("rows"));

    this.update();
    this.register(Core.getInstance(), 20);
    this.open();
  }

  @Override
  public void update() {
    for (ServerItem serverItem : listServers()) {
      this.setItem(serverItem.getSlot(),
        BukkitUtils.deserializeItemStack(serverItem.getIcon().replace("{players}", StringUtils.formatNumber(ServerItem.getServerCount(serverItem)))));
    }
  }

  public void cancel() {
    super.cancel();
    HandlerList.unregisterAll(this);
  }

  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent evt) {
    if (evt.getPlayer().equals(this.player)) {
      this.cancel();
    }
  }

  @EventHandler
  public void onInventoryClose(InventoryCloseEvent evt) {
    if (evt.getPlayer().equals(this.player) && evt.getInventory().equals(this.getInventory())) {
      this.cancel();
    }
  }
}
