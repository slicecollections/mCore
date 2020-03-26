package tk.slicecollections.maxteer.libraries.npclib.api.trait;

/**
 * @author Maxter
 */
public interface Trait {
  
  /**
   * chamado ao Rastreio ser adicionado.
   */
  public void onAttach();
  
  /**
   * chamado ao Rastreio ser removido.
   */
  public void onRemove();
  
  /**
   * chamado ao NPC ser spawnado.
   */
  public void onSpawn();
  
  /**
   * chamado ao NPC ser despawnado.
   */
  public void onDespawn();
}
