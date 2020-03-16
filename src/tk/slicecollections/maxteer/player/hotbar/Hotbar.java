package tk.slicecollections.maxteer.player.hotbar;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import me.clip.placeholderapi.PlaceholderAPI;
import tk.slicecollections.maxteer.player.Profile;
import tk.slicecollections.maxteer.utils.BukkitUtils;

public class Hotbar {

  private String id;
  private List<HotbarButton> buttons;

  public Hotbar(String id) {
    this.id = id;
    this.buttons = new ArrayList<>();
  }

  public String getName() {
    return this.id;
  }
  
  public List<HotbarButton> getButtons() {
    return this.buttons;
  }

  public void apply(Profile profile) {
    Player player = profile.getPlayer();

    player.getInventory().clear();
    player.getInventory().setArmorContents(null);

    this.buttons.stream().filter(button -> button.getSlot() >= 0 && button.getSlot() <= 8).forEach(button -> {
      ItemStack icon = BukkitUtils.deserializeItemStack(PlaceholderAPI.setPlaceholders(player, button.getIcon().replace("%perfil%", "")));
      player.getInventory().setItem(button.getSlot(), button.getIcon().contains("%perfil%") ? BukkitUtils.putProfileOnSkull(player, icon) : icon);
    });

    player.updateInventory();
  }

  public HotbarButton compareButton(Player player, ItemStack item) {
    return this.buttons.stream().filter(button -> button.getSlot() >= 0 && button.getSlot() <= 8 && player.getInventory().getItem(button.getSlot()).equals(item)).findFirst()
        .orElse(null);
  }

  private static final List<Hotbar> HOTBARS = new ArrayList<>();

  public static void addHotbar(Hotbar hotbar) {
    HOTBARS.add(hotbar);
  }

  public static Hotbar getHotbarById(String id) {
    return HOTBARS.stream().filter(hb -> hb.getName().equalsIgnoreCase(id)).findFirst().orElse(null);
  }
}
