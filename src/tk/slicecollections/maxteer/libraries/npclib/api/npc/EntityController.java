package tk.slicecollections.maxteer.libraries.npclib.api.npc;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import tk.slicecollections.maxteer.libraries.npclib.api.NPC;

public interface EntityController {
  
  public void spawn(Location location, NPC npc);
  
  public void remove();
  
  public Entity getBukkitEntity();
}
