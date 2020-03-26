package tk.slicecollections.maxteer.libraries.npclib.api;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import tk.slicecollections.maxteer.libraries.npclib.api.metadata.MetadataStore;
import tk.slicecollections.maxteer.libraries.npclib.trait.NPCTrait;

import java.util.UUID;

/**
 * @author Maxter
 */
public interface NPC {

  boolean spawn(Location location);

  boolean despawn();

  void destroy();

  void update();

  MetadataStore data();

  void addTrait(NPCTrait trait);

  void addTrait(Class<? extends NPCTrait> traitClass);

  void removeTrait(Class<? extends NPCTrait> traitClass);

  boolean isSpawned();

  boolean isProtected();

  <T extends NPCTrait> T getTrait(Class<T> traitClass);

  Entity getEntity();

  Location getCurrentLocation();

  UUID getUUID();

  String getName();

  public static final String PROTECTED_KEY = "protected",
    TAB_LIST_KEY = "hide-from-tablist",
    HIDE_BY_TEAMS_KEY = "hide-by-teams",
    FLYABLE = "flyable",
    GRAVITY = "gravity";
}
