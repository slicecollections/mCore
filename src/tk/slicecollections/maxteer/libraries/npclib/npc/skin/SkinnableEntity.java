package tk.slicecollections.maxteer.libraries.npclib.npc.skin;

import org.bukkit.entity.Player;
import tk.slicecollections.maxteer.libraries.npclib.api.NPC;

public interface SkinnableEntity {

  public NPC getNPC();

  public Player getEntity();

  public SkinPacketTracker getSkinTracker();

  public void setSkin(Skin skin);

  public Skin getSkin();

  public void setSkinFlags(byte flags);
}
