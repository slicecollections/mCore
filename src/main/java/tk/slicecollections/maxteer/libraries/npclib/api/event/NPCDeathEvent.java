package tk.slicecollections.maxteer.libraries.npclib.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import tk.slicecollections.maxteer.libraries.npclib.api.npc.NPC;

/**
 * @author Maxter
 */
public class NPCDeathEvent extends NPCEvent implements Cancellable {

  private NPC npc;
  private Player killer;
  private boolean cancelled;

  public NPCDeathEvent(NPC npc, Player killer) {
    this.npc = npc;
    this.killer = killer;
  }

  public NPC getNPC() {
    return npc;
  }

  public Player getKiller() {
    return killer;
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
