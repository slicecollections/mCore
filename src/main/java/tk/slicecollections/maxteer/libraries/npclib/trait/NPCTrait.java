package tk.slicecollections.maxteer.libraries.npclib.trait;

import tk.slicecollections.maxteer.libraries.npclib.api.NPC;
import tk.slicecollections.maxteer.libraries.npclib.api.trait.Trait;

/**
 * @author Maxter
 */
public abstract class NPCTrait implements Trait {

  private NPC npc;

  public NPCTrait(NPC npc) {
    this.npc = npc;
  }

  public NPC getNPC() {
    return npc;
  }

  @Override
  public void onAttach() {}

  @Override
  public void onSpawn() {}

  @Override
  public void onDespawn() {}

  @Override
  public void onRemove() {}
}
