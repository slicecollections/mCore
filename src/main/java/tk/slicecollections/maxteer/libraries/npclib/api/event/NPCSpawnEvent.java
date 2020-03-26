package tk.slicecollections.maxteer.libraries.npclib.api.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import tk.slicecollections.maxteer.libraries.npclib.api.NPC;

/**
 * @author Maxter
 */
public class NPCSpawnEvent extends NPCEvent implements Cancellable {

  private NPC npc;
  private boolean cancelled;

  public NPCSpawnEvent(NPC npc) {
    this.npc = npc;
  }

  public NPC getNPC() {
    return npc;
  }

  public void setCancelled(boolean cancelled) {
    this.cancelled = cancelled;
  }

  public boolean isCancelled() {
    return cancelled;
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
