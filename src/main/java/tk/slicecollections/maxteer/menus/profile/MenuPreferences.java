package tk.slicecollections.maxteer.menus.profile;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import tk.slicecollections.maxteer.Core;
import tk.slicecollections.maxteer.database.data.container.PreferencesContainer;
import tk.slicecollections.maxteer.libraries.menu.PlayerMenu;
import tk.slicecollections.maxteer.menus.MenuProfile;
import tk.slicecollections.maxteer.player.Profile;
import tk.slicecollections.maxteer.player.enums.BloodAndGore;
import tk.slicecollections.maxteer.player.enums.PlayerVisibility;
import tk.slicecollections.maxteer.player.enums.PrivateMessages;
import tk.slicecollections.maxteer.player.enums.ProtectionLobby;
import tk.slicecollections.maxteer.utils.BukkitUtils;
import tk.slicecollections.maxteer.utils.StringUtils;
import tk.slicecollections.maxteer.utils.enums.EnumSound;

/**
 * @author Maxter
 */
public class MenuPreferences extends PlayerMenu {

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
            if (evt.getSlot() == 10 || evt.getSlot() == 11 || evt.getSlot() == 12 || evt.getSlot() == 14 || evt.getSlot() == 15 || evt.getSlot() == 16) {
              EnumSound.CLICK.play(this.player, 0.5F, 2.0F);
            } else if (evt.getSlot() == 20) {
              EnumSound.ITEM_PICKUP.play(this.player, 0.5F, 2.0F);
              profile.getPreferencesContainer().changePlayerVisibility();
              if (!profile.playingGame()) {
                profile.refreshPlayers();
              }
              new MenuPreferences(profile);
            } else if (evt.getSlot() == 21) {
              EnumSound.ITEM_PICKUP.play(this.player, 0.5F, 2.0F);
              profile.getPreferencesContainer().changePrivateMessages();
              new MenuPreferences(profile);
            } else if (evt.getSlot() == 23) {
              EnumSound.ITEM_PICKUP.play(this.player, 0.5F, 2.0F);
              profile.getPreferencesContainer().changeBloodAndGore();
              new MenuPreferences(profile);
            } else if (evt.getSlot() == 24) {
              EnumSound.ITEM_PICKUP.play(this.player, 0.5F, 2.0F);
              profile.getPreferencesContainer().changeProtectionLobby();
              new MenuPreferences(profile);
            } else if (evt.getSlot() == 40) {
              EnumSound.CLICK.play(this.player, 0.5F, 2.0F);
              new MenuProfile(profile);
            }
          }
        }
      }
    }
  }

  public MenuPreferences(Profile profile) {
    super(profile.getPlayer(), "Preferências", 5);

    PreferencesContainer pc = profile.getPreferencesContainer();

    PlayerVisibility pv = pc.getPlayerVisibility();
    this.setItem(11, BukkitUtils.deserializeItemStack("ITEM_FRAME : 1 : nome>&aJogadores : desc>&7Ative ou desative os\n&7jogadores no lobby."));
    this.setItem(20, BukkitUtils.deserializeItemStack(
      "INK_SACK:" + pv.getInkSack() + " : 1 : nome>" + pv.getName() + " : desc>&fEstado: &7" + StringUtils.stripColors(pv.getName()) + "\n \n&eClique para modificar!"));

    PrivateMessages pm = pc.getPrivateMessages();
    this.setItem(12, BukkitUtils.deserializeItemStack("PAPER : 1 : nome>&aMensagens privadas : desc>&7Ative ou desative as mensagens\n&7enviadas através do tell."));
    this.setItem(21, BukkitUtils.deserializeItemStack(
      "INK_SACK:" + pm.getInkSack() + " : 1 : nome>" + pm.getName() + " : desc>&fEstado: &7" + StringUtils.stripColors(pm.getName()) + "\n \n&eClique para modificar!"));

    BloodAndGore bg = pc.getBloodAndGore();
    this.setItem(14, BukkitUtils.deserializeItemStack("REDSTONE : 1 : nome>&aViolência : desc>&7Ative ou desative as partículas\n&7de sangue no PvP."));
    this.setItem(23, BukkitUtils.deserializeItemStack(
      "INK_SACK:" + bg.getInkSack() + " : 1 : nome>" + bg.getName() + " : desc>&fEstado: &7" + StringUtils.stripColors(bg.getName()) + "\n \n&eClique para modificar!"));

    ProtectionLobby pl = pc.getProtectionLobby();
    this.setItem(15, BukkitUtils.deserializeItemStack("NETHER_STAR : 1 : nome>&aProteção no /lobby : desc>&7Ative ou desative o pedido de\n&7confirmação ao utilizar /lobby."));
    this.setItem(24, BukkitUtils.deserializeItemStack(
      "INK_SACK:" + pl.getInkSack() + " : 1 : nome>" + pl.getName() + " : desc>&fEstado: &7" + StringUtils.stripColors(pl.getName()) + "\n \n&eClique para modificar!"));

    this.setItem(40, BukkitUtils.deserializeItemStack("INK_SACK:1 : 1 : nome>&cVoltar"));

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
