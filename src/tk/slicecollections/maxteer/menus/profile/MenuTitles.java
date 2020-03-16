package tk.slicecollections.maxteer.menus.profile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import tk.slicecollections.maxteer.Core;
import tk.slicecollections.maxteer.libraries.menu.PagedPlayerMenu;
import tk.slicecollections.maxteer.menus.MenuProfile;
import tk.slicecollections.maxteer.player.Profile;
import tk.slicecollections.maxteer.titles.Title;
import tk.slicecollections.maxteer.titles.TitleManager;
import tk.slicecollections.maxteer.utils.BukkitUtils;
import tk.slicecollections.maxteer.utils.enums.EnumSound;

public class MenuTitles extends PagedPlayerMenu {

  @EventHandler
  public void onInventoryClick(InventoryClickEvent evt) {
    if (evt.getInventory().equals(this.getCurrentInventory())) {
      evt.setCancelled(true);

      if (evt.getWhoClicked().equals(this.player)) {
        Profile profile = Profile.getProfile(this.player.getName());
        if (profile == null) {
          this.player.closeInventory();
          return;
        }

        if (evt.getClickedInventory() != null && evt.getClickedInventory().equals(this.getCurrentInventory())) {
          ItemStack item = evt.getCurrentItem();

          if (item != null && item.getType() != Material.AIR) {
            if (evt.getSlot() == this.previousPage) {
              EnumSound.CLICK.play(this.player, 0.5F, 2.0F);
              this.openPrevious();
            } else if (evt.getSlot() == this.nextPage) {
              EnumSound.CLICK.play(this.player, 0.5F, 2.0F);
              this.openNext();
            } else if (evt.getSlot() == 40) {
              EnumSound.CLICK.play(this.player, 0.5F, 2.0F);
              new MenuProfile(profile);
            } else if (item != null) {
              Title title = this.titles.get(item);
              if (title != null) {
                if (!title.has(profile)) {
                  EnumSound.ENDERMAN_TELEPORT.play(this.player, 0.5F, 1.0F);
                  return;
                }

                EnumSound.ITEM_PICKUP.play(this.player, 0.5F, 2.0F);
                Title selected = profile.getSelectedContainer().getTitle();
                if (selected != null && title.equals(selected)) {
                  profile.getSelectedContainer().setTitle("0");
                  TitleManager.deselect(profile);
                } else {
                  profile.getSelectedContainer().setTitle(title.getId());
                  TitleManager.select(profile, title);
                }
                new MenuTitles(profile);
              }
            }
          }
        }

        profile = null;
      }
    }
  }

  private Map<ItemStack, Title> titles = new HashMap<>();

  public MenuTitles(Profile profile) {
    super(profile.getPlayer(), "Títulos", 5);
    this.previousPage = 36;
    this.nextPage = 44;
    this.onlySlots(10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25);

    this.removeSlotsWith(BukkitUtils.deserializeItemStack("INK_SACK:1 : 1 : nome>&cVoltar"), 40);

    List<ItemStack> items = new ArrayList<>();
    List<ItemStack> sub = new ArrayList<>();
    for (Title title : Title.listTitles()) {
      ItemStack item = title.getIcon(profile);
      this.titles.put(item, title);
      if (title.has(profile)) {
        items.add(item);
        continue;
      }

      sub.add(item);
    }

    items.addAll(sub);
    this.setItems(items);
    sub.clear();
    sub = null;
    items.clear();
    items = null;

    this.register(Core.getInstance());
    this.open();
  }

  public void cancel() {
    titles.clear();
    titles = null;
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
    if (evt.getPlayer().equals(this.player) && evt.getInventory().equals(this.getCurrentInventory())) {
      this.cancel();
    }
  }
}
