package tk.slicecollections.maxteer.libraries.npclib.api;

import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import tk.slicecollections.maxteer.libraries.npclib.api.metadata.MetadataStore;
import tk.slicecollections.maxteer.libraries.npclib.trait.NPCTrait;

public interface NPC {

  public boolean spawn(Location location);

  public boolean despawn();
  
  public void destroy();
  
  public void update();
  
  public MetadataStore data();

  public void addTrait(NPCTrait trait);
  
  public void addTrait(Class<? extends NPCTrait> traitClass);
  
  public void removeTrait(Class<? extends NPCTrait> traitClass);
  
  public boolean isSpawned();
  
  public boolean isProtected();
  
  public <T extends NPCTrait> T getTrait(Class<T> traitClass);

  public Entity getEntity();
  
  public Location getCurrentLocation();

  public UUID getUUID();

  public String getName();
  
  public static final String PROTECTED_KEY = "protected",
      TAB_LIST_KEY = "hide-from-tablist",
      HIDE_BY_TEAMS_KEY = "hide-by-teams",
      FLYABLE = "flyable",
      GRAVITY = "gravity";
}
