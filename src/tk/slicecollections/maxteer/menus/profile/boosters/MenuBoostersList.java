package tk.slicecollections.maxteer.menus.profile.boosters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import tk.slicecollections.maxteer.Core;
import tk.slicecollections.maxteer.achievements.Achievement;
import tk.slicecollections.maxteer.booster.Booster;
import tk.slicecollections.maxteer.booster.Booster.BoosterType;
import tk.slicecollections.maxteer.libraries.menu.PagedPlayerMenu;
import tk.slicecollections.maxteer.menus.profile.MenuBoosters;
import tk.slicecollections.maxteer.player.Profile;
import tk.slicecollections.maxteer.player.role.Role;
import tk.slicecollections.maxteer.utils.BukkitUtils;
import tk.slicecollections.maxteer.utils.StringUtils;
import tk.slicecollections.maxteer.utils.TimeUtils;
import tk.slicecollections.maxteer.utils.enums.EnumSound;

public class MenuBoostersList<T extends Achievement> extends PagedPlayerMenu {

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
              new MenuBoosters(profile);
            } else if (item != null) {
              Booster booster = this.boosters.get(item);
              if (booster != null) {
                if (type == BoosterType.NETWORK) {
                  if (!Booster.setNetworkBooster(Core.minigame, profile, booster)) {
                    EnumSound.ENDERMAN_TELEPORT.play(this.player, 0.5F, 1.0F);
                    this.player.sendMessage("§cJá existe um Multiplicador Geral para o " + Core.minigame + " ativo.");
                    this.player.closeInventory();
                    return;
                  }

                  EnumSound.LEVEL_UP.play(this.player, 0.5F, 1.0F);
                  Profile.listProfiles()
                      .forEach(pf -> pf.getPlayer()
                          .sendMessage(" \n " + StringUtils.getLastColor(Role.getPlayerRole(this.player).getPrefix()) + this.player.getName()
                              + " §7ativou um §6Multiplicador de Coins Geral§7.\n §bTODOS §7os jogadores recebem um bônus de " + booster.getMultiplier()
                              + "x §7nas partidas de §6" + Core.minigame + "§7.\n "));
                  this.player.closeInventory();
                } else {
                  if (!profile.getBoostersContainer().enable(booster)) {
                    EnumSound.ENDERMAN_TELEPORT.play(this.player, 0.5F, 1.0F);
                    this.player.sendMessage("§cVocê já possui um Multiplicador Pessoal ativo.");
                    this.player.closeInventory();
                    return;
                  }
                  
                  this.player.sendMessage(
                      "§aVocê ativou um §6Multiplicador de Coins " + booster.getMultiplier() + "x §8(" + TimeUtils.getTime(TimeUnit.HOURS.toMillis(booster.getHours())) + ")§a.");
                  new MenuBoosters(profile);
                }
              }
            }
          }
        }

        profile = null;
      }
    }
  }

  private BoosterType type;
  private Map<ItemStack, Booster> boosters = new HashMap<>();

  public MenuBoostersList(Profile profile, String name, BoosterType type) {
    super(profile.getPlayer(), "Multiplicadores " + name, 5);
    this.type = type;
    this.previousPage = 36;
    this.nextPage = 44;
    this.onlySlots(10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25);

    this.removeSlotsWith(BukkitUtils.deserializeItemStack("INK_SACK:1 : 1 : nome>&cVoltar"), 40);

    List<ItemStack> items = new ArrayList<>();
    List<Booster> boosters = profile.getBoostersContainer().getBoosters(type);
    for (Booster booster : boosters) {
      ItemStack icon = BukkitUtils.deserializeItemStack("POTION" + (type == BoosterType.NETWORK ? ":8232" : "") + " : 1 : esconder>tudo : nome>&aMultiplicador "
          + (type == BoosterType.NETWORK ? "Geral" : "Pessoal") + " : desc>&fMultiplicador: &6" + booster.getMultiplier() + "x\n&fDuração: &7"
          + TimeUtils.getTime(TimeUnit.HOURS.toMillis(booster.getHours())) + "\n \n&eClique para ativar o multiplicador!");
      items.add(icon);
      this.boosters.put(icon, booster);
    }

    if (items.size() == 0) {
      this.removeSlotsWith(BukkitUtils.deserializeItemStack("GLASS_BOTTLE : 1 : nome>&cVocê não possui nenhum Multiplicador!"), 22);
    }
    this.setItems(items);
    boosters.clear();
    boosters = null;
    items.clear();
    items = null;

    this.register(Core.getInstance());
    this.open();
  }

  public void cancel() {
    this.type = null;
    this.boosters.values().forEach(Booster::gc);
    this.boosters.clear();
    this.boosters = null;
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
