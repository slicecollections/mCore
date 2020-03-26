package tk.slicecollections.maxteer.libraries.menu;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import tk.slicecollections.maxteer.utils.BukkitUtils;

import java.util.*;
import java.util.Map.Entry;

/**
 * @author Maxter
 */
public class PagedMenu {

  protected int rows;
  protected String name;
  protected int currentPage = 1;
  protected List<Menu> menus = new ArrayList<>();
  protected Map<Menu, Integer> id = new HashMap<>();
  protected Map<Integer, ItemStack> slots = new HashMap<>();

  public int previousPage = 45, nextPage = 53;
  public String previousStack = "INK_SACK:8 : 1 : nome>&aPágina {page}", nextStack = "INK_SACK:10 : 1 : nome>&aPágina {page}";

  public PagedMenu(String name) {
    this(name, 1);
  }

  public PagedMenu(String name, int rows) {
    this.rows = rows > 6 ? 6 : Math.max(rows, 1);
    this.name = name;
  }

  public void open(Player player) {
    player.openInventory(menus.get(0).getInventory());
  }

  public void openPrevious(Player player, Inventory inv) {
    int currentPage = id.get(getCurrent(inv));
    if (currentPage == 1) {
      return;
    }

    player.openInventory(menus.get(currentPage - 2).getInventory());
  }

  public void openNext(Player player, Inventory inv) {
    int currentPage = id.get(getCurrent(inv));
    if (currentPage + 1 > menus.size()) {
      return;
    }

    player.openInventory(menus.get(currentPage).getInventory());
  }

  public void onlySlots(Integer... slots) {
    onlySlots(Arrays.asList(slots));
  }

  public void onlySlots(List<Integer> slots) {
    for (int slot = 0; slot < rows * 9; slot++) {
      if (!slots.contains(slot)) {
        this.slots.put(slot, null);
      }
    }
  }

  public void removeSlots(int... slots) {
    removeSlotsWith(null, slots);
  }

  public void removeSlotsWith(ItemStack item, int... slots) {
    for (int slot : slots) {
      this.slots.put(slot, item);
    }
  }

  protected int lastListSize = -1;

  public void setItems(List<ItemStack> items) {
    if (items.size() == lastListSize) {
      updateItems(items);
      return;
    }

    this.menus.forEach(menu -> menu.getInventory().getViewers().forEach(player -> player.closeInventory()));
    this.menus.clear();
    this.lastListSize = items.size();
    List<List<ItemStack>> splitted = split(items);
    if (splitted.isEmpty()) {
      splitted.add(new ArrayList<>());
    }

    for (int i = 0; i < splitted.size(); i++) {
      List<ItemStack> list = splitted.get(i);
      Menu menu = new Menu(name, this.rows);
      for (Entry<Integer, ItemStack> entry : this.slots.entrySet()) {
        menu.getSlots().remove(entry.getKey());
        if (entry.getValue() != null) {
          menu.setItem(entry.getKey(), entry.getValue());
        }
      }

      menu.setItems(list);
      if (splitted.size() > 1) {
        if (i > 0 && previousPage != -1) {
          menu.setItem(previousPage, BukkitUtils.deserializeItemStack(previousStack.replace("{page}", String.valueOf(i))));
        }
        if (i + 1 != splitted.size() && nextPage != -1) {
          menu.setItem(nextPage, BukkitUtils.deserializeItemStack(nextStack.replace("{page}", String.valueOf(i + 2))));
        }
      }
      this.menus.add(menu);
      this.id.put(menu, i + 1);
    }
  }

  public void updateItems(List<ItemStack> items) {
    List<List<ItemStack>> splitted = split(items);
    if (splitted.isEmpty()) {
      splitted.add(new ArrayList<>());
    }

    for (int i = 0; i < splitted.size(); i++) {
      Menu menu = menus.get(i);
      for (Entry<Integer, ItemStack> entry : this.slots.entrySet()) {
        if (entry.getValue() != null) {
          menu.setItem(entry.getKey(), entry.getValue());
        }
      }

      menu.setItems(splitted.get(i));
    }
  }

  public Menu getCurrent(Inventory inv) {
    for (Menu menu : menus) {
      if (menu.getInventory().equals(inv)) {
        return menu;
      }
    }

    return menus.get(0);
  }

  public Inventory getCurrentInventory() {
    return menus.get(currentPage - 1).getInventory();
  }

  private List<List<ItemStack>> split(List<ItemStack> items) {
    List<List<ItemStack>> list = new ArrayList<>();

    List<ItemStack> toadd = new ArrayList<>();
    for (int size = 1; size - 1 < items.size(); size++) {
      toadd.add(items.get(size - 1));
      if (size % ((this.rows * 9) - this.slots.size()) == 0) {
        list.add(toadd);
        toadd = new ArrayList<>();
      }

      if (size == items.size()) {
        if (!toadd.isEmpty()) {
          list.add(toadd);
        }
        break;
      }
    }

    return list;
  }
}
