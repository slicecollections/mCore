package tk.slicecollections.maxteer.menus.profile;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import tk.slicecollections.maxteer.Core;
import tk.slicecollections.maxteer.booster.Booster;
import tk.slicecollections.maxteer.booster.Booster.BoosterType;
import tk.slicecollections.maxteer.booster.NetworkBooster;
import tk.slicecollections.maxteer.libraries.menu.PlayerMenu;
import tk.slicecollections.maxteer.menus.MenuProfile;
import tk.slicecollections.maxteer.menus.profile.boosters.MenuBoostersList;
import tk.slicecollections.maxteer.player.Profile;
import tk.slicecollections.maxteer.player.role.Role;
import tk.slicecollections.maxteer.utils.BukkitUtils;
import tk.slicecollections.maxteer.utils.TimeUtils;
import tk.slicecollections.maxteer.utils.enums.EnumSound;

/**
 * @author Maxter
 */
public class MenuBoosters extends PlayerMenu {

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
            if (evt.getSlot() == 12) {
              EnumSound.ITEM_PICKUP.play(this.player, 0.5F, 2.0F);
              new MenuBoostersList<>(profile, "Pessoais", BoosterType.PRIVATE);
            } else if (evt.getSlot() == 14) {
              EnumSound.ITEM_PICKUP.play(this.player, 0.5F, 2.0F);
              new MenuBoostersList<>(profile, "Gerais", BoosterType.NETWORK);
            } else if (evt.getSlot() == 30) {
              EnumSound.CLICK.play(this.player, 0.5F, 2.0F);
              new MenuProfile(profile);
            } else if (evt.getSlot() == 31) {
              EnumSound.ITEM_PICKUP.play(this.player, 0.5F, 2.0F);
            }
          }
        }
      }
    }
  }

  public MenuBoosters(Profile profile) {
    super(profile.getPlayer(), "Multiplicadores", 4);

    this.setItem(12, BukkitUtils.deserializeItemStack(
      "POTION : 1 : nome>&aMultiplicadores Pessoais : desc>&7Concede um &6Multiplicador de Coins &7apenas\n&7para &bVOCÊ &7em todos os minigames do servidor\n&7por um curto período de tempo.\n \n&eClique para ver seus multiplicadores!"));
    this.setItem(14, BukkitUtils.deserializeItemStack(
      "POTION:8232 : 1 : esconder>tudo : nome>&aMultiplicadores Gerais : desc>&7Concede um &6Multiplicador de Coins &7para\n&bTODOS &7os jogadores em apenas um minigame\n&7por um curto período de tempo.\n \n&eClique para ver seus multiplicadores!"));

    String booster = profile.getBoostersContainer().getEnabled();
    StringBuilder result = new StringBuilder(), network = new StringBuilder();
    for (int index = 0; index < Core.minigames.size(); index++) {
      String minigame = Core.minigames.get(index);
      NetworkBooster nb = Booster.getNetworkBooster(minigame);
      network.append(" &8• &b").append(minigame).append(": ")
        .append(nb == null ? "&cDesativado" : "&6" + nb.getMultiplier() + "x &7por " + Role.getColored(nb.getBooster()) + " &8(" + TimeUtils.getTimeUntil(nb.getExpires()) + ")")
        .append(index + 1 == Core.minigames.size() ? "" : "\n");
    }
    result.append("&fMultiplicador Pessoal ativo:\n ");
    if (booster == null) {
      result.append("&cVocê não possui nenhum multiplicador ativo.");
    } else {
      String[] splitted = booster.split(":");
      double all = 50.0 * Double.parseDouble(splitted[0]);
      result.append("&8• &6Multiplicador ").append(splitted[0]).append("x &8(").append(TimeUtils.getTimeUntil(Long.parseLong(splitted[1])))
        .append(")\n \n&fCálculo:\n &7Com o multiplicador ativo ao receber &650 Coins &7o\n &7total recebido será equivalente a &6").append((int) all).append(" Coins&7.");
    }
    this.setItem(30, BukkitUtils.deserializeItemStack("INK_SACK:1 : 1 : nome>&cVoltar"));
    this.setItem(31, BukkitUtils.deserializeItemStack(
      "PAPER : 1 : nome>&aMultiplicadores de Crédito : desc>&7Os Multiplicadores são acumulativos. Quanto mais\n&7multiplicadores ativos, maior será o bônus recebido.\n \n&fMultiplicadores Gerais:\n" + network
        .toString() + "\n \n" + result.toString()));

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
