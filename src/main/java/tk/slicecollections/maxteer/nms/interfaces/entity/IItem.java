package tk.slicecollections.maxteer.nms.interfaces.entity;

import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import tk.slicecollections.maxteer.libraries.holograms.api.HologramLine;

/**
 * @author Maxter
 */
public interface IItem {

  public void setPassengerOf(Entity entity);

  public void setItemStack(ItemStack item);

  public void setLocation(double x, double y, double z);

  public boolean isDead();

  public void killEntity();

  public Item getEntity();

  public HologramLine getLine();
}
