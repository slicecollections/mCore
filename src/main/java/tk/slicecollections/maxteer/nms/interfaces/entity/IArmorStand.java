package tk.slicecollections.maxteer.nms.interfaces.entity;

import org.bukkit.entity.ArmorStand;
import tk.slicecollections.maxteer.libraries.holograms.api.HologramLine;

/**
 * @author Maxter
 */
public interface IArmorStand {
  
  int getId();

  void setName(String name);

  void setLocation(double x, double y, double z);

  boolean isDead();

  void killEntity();

  ArmorStand getEntity();

  HologramLine getLine();
}
