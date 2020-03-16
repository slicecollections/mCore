package tk.slicecollections.maxteer.libraries.npclib.npc;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import tk.slicecollections.maxteer.libraries.npclib.api.NPC;
import tk.slicecollections.maxteer.libraries.npclib.api.npc.EntityController;

public abstract class AbstractEntityController implements EntityController {

  private Entity bukkitEntity;

  protected abstract Entity createEntity(Location location, NPC npc);

  @Override
  public void spawn(Location location, NPC npc) {
    bukkitEntity = createEntity(location, npc);
  }

  @Override
  public void remove() {
    if (bukkitEntity != null) {
      bukkitEntity.remove();
      bukkitEntity = null;
    }
  }

  @Override
  public Entity getBukkitEntity() {
    return bukkitEntity;
  }
}
