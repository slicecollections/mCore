package tk.slicecollections.maxteer.libraries.npclib.api.event;

import org.bukkit.entity.Entity;
import org.bukkit.event.HandlerList;
import tk.slicecollections.maxteer.libraries.npclib.api.npc.NPC;

/**
 * @author Maxter
 */
public class NPCStopFollowingEvent extends NPCEvent {

  private NPC npc;
  private Entity target;

  public NPCStopFollowingEvent(NPC npc, Entity target) {
    this.npc = npc;
    this.target = target;
  }

  public NPC getNPC() {
    return npc;
  }

  public Entity getTarget() {
    return target;
  }

  @Override
  public HandlerList getHandlers() {
    return HANDLER_LIST;
  }

  private static final HandlerList HANDLER_LIST = new HandlerList();

  public static HandlerList getHandlerList() {
    return HANDLER_LIST;
  }
}
