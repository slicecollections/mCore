package tk.slicecollections.maxteer.libraries.npclib.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import tk.slicecollections.maxteer.libraries.npclib.api.npc.NPC;

/**
 * @author Maxter
 */
public class NPCLeftClickEvent extends NPCEvent {

  private NPC npc;
  private Player player;

  public NPCLeftClickEvent(NPC npc, Player clicked) {
    this.npc = npc;
    this.player = clicked;
  }

  public NPC getNPC() {
    return npc;
  }

  public Player getPlayer() {
    return player;
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
