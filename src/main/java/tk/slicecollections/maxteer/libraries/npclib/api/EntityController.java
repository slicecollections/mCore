package tk.slicecollections.maxteer.libraries.npclib.api;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import tk.slicecollections.maxteer.libraries.npclib.api.npc.NPC;

/**
 * @author Maxter
 */
public interface EntityController {
  
  void spawn(Location location, NPC npc);
  
  void remove();
  
  Entity getBukkitEntity();
}
