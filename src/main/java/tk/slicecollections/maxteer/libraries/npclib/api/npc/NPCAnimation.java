package tk.slicecollections.maxteer.libraries.npclib.api.npc;

public enum NPCAnimation {

  SWING_ARM(0),
  DAMAGE(1),
  EAT_FOOD(3),
  CRITICAL_HIT(4),
  MAGIC_CRITICAL_HIT(5),
  CROUCH(104),
  UNCROUCH(105);

  private final int id;

  private NPCAnimation(int id) {
    this.id = id;
  }

  public int getId() {
    return id;
  }
}
