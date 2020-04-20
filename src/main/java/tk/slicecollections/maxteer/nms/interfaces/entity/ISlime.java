package tk.slicecollections.maxteer.nms.interfaces.entity;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Slime;
import tk.slicecollections.maxteer.libraries.holograms.api.HologramLine;

/**
 * @author Maxter
 */
public interface ISlime {

  public void setPassengerOf(Entity entity);

  public void setLocation(double x, double y, double z);

  public boolean isDead();

  public void killEntity();

  public Slime getEntity();

  public HologramLine getLine();
}
