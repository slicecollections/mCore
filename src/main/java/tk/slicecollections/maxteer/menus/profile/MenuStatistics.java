package tk.slicecollections.maxteer.menus.profile;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import tk.slicecollections.maxteer.Core;
import tk.slicecollections.maxteer.libraries.menu.PlayerMenu;
import tk.slicecollections.maxteer.menus.MenuProfile;
import tk.slicecollections.maxteer.player.Profile;
import tk.slicecollections.maxteer.utils.BukkitUtils;
import tk.slicecollections.maxteer.utils.enums.EnumSound;

/**
 * @author Maxter
 */
public class MenuStatistics extends PlayerMenu {

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
            if (evt.getSlot() == 11 || evt.getSlot() == 13 || evt.getSlot() == 15) {
              EnumSound.ITEM_PICKUP.play(this.player, 0.5F, 2.0F);
            } else if (evt.getSlot() == 31) {
              EnumSound.CLICK.play(this.player, 0.5F, 2.0F);
              new MenuProfile(profile);
            }
          }
        }
      }
    }
  }

  public MenuStatistics(Profile profile) {
    super(profile.getPlayer(), "Estatísticas", 4);

    this.setItem(11, BukkitUtils.deserializeItemStack(PlaceholderAPI.setPlaceholders(this.player,
      "GRASS : 1 : nome>&aSky Wars : desc>&eSolo:\n &8▪ &fAbates: &7%mCore_SkyWars_1v1kills%\n &8▪ &fMortes: &7%mCore_SkyWars_1v1deaths%\n &8▪ &fVitórias: &7%mCore_SkyWars_1v1wins%\n &8▪ &fPartidas: &7%mCore_SkyWars_1v1games%\n &8▪ &fAssistências: &7%mCore_SkyWars_1v1assists%\n " + "\n&eDupla:\n &8▪ &fAbates: &7%mCore_SkyWars_2v2kills%\n &8▪ &fMortes: &7%mCore_SkyWars_2v2deaths%\n &8▪ &fVitórias: &7%mCore_SkyWars_2v2wins%\n &8▪ &fPartidas: &7%mCore_SkyWars_2v2games%\n &8▪ &fAssistências: &7%mCore_SkyWars_2v2assists%\n \n&fCoins: &6%mCore_SkyWars_coins%")));

    this.setItem(13, BukkitUtils.deserializeItemStack(PlaceholderAPI.setPlaceholders(this.player,
      "STAINED_CLAY:11 : 1 : nome>&aThe Bridge : desc>&e1v1:\n &8▪ &fAbates: &7%mCore_TheBridge_1v1kills%\n &8▪ &fMortes: &7%mCore_TheBridge_1v1deaths%\n &8▪ &fPontos: &7%mCore_TheBridge_1v1points%\n &8▪ &fVitórias: &7%mCore_TheBridge_1v1wins%\n &8▪ &fPartidas: &7%mCore_TheBridge_1v1games%\n " + "\n&e2v2:\n &8▪ &fAbates: &7%mCore_TheBridge_2v2kills%\n &8▪ &fMortes: &7%mCore_TheBridge_2v2deaths%\n &8▪ &fPontos: &7%mCore_TheBridge_2v2points%\n &8▪ &fVitórias: &7%mCore_TheBridge_2v2wins%\n &8▪ &fPartidas: &7%mCore_TheBridge_2v2games%\n \n&eWinstreak:\n &8▪ &fDiário: &7%mCore_TheBridge_winstreak%\n \n&fCoins: &6%mCore_TheBridge_coins%")));

    this.setItem(15, BukkitUtils.deserializeItemStack(PlaceholderAPI.setPlaceholders(this.player,
      "BOW : 1 : nome>&aMurder : desc>&eClássico: \n &8▪ &fAbates: &7%mCore_Murder_classic_kills%\n &8▪ &fVitórias: &7%mCore_Murder_classic_wins%\n \n&eAssassinos: \n &8▪ &fAbates: &7%mCore_Murder_assassins_kills%\n &8▪ &fVitórias: &7%mCore_Murder_assassins_wins%\n \n&fCoins: &6%mCore_Murder_coins%")));

    this.setItem(31, BukkitUtils.deserializeItemStack("INK_SACK:1 : 1 : nome>&cVoltar"));

    this.register(Core.getInstance());
    this.open();
  }

  public void cancel() {
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
