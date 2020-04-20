package tk.slicecollections.maxteer.libraries.npclib.api.npc;

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

  void playAnimation(NPCAnimation animation);

  void addTrait(NPCTrait trait);

  void addTrait(Class<? extends NPCTrait> traitClass);

  void removeTrait(Class<? extends NPCTrait> traitClass);

  void finishNavigation();

  void setFollowing(Entity entity);

  void setWalkingTo(Location location);

  boolean isSpawned();

  boolean isProtected();

  boolean isNavigating();

  <T extends NPCTrait> T getTrait(Class<T> traitClass);

  Entity getEntity();

  Entity getFollowing();

  Location getWalkingTo();

  Location getCurrentLocation();

  UUID getUUID();

  String getName();
                             // boolean
  public static final String PROTECTED_KEY = "protected",
    // boolean
    TAB_LIST_KEY = "hide-from-tablist",
    // boolean
    HIDE_BY_TEAMS_KEY = "hide-by-teams",
    // boolean
    FLYABLE = "flyable",
    // boolean
    GRAVITY = "gravity",
    // nome do player
    ATTACHED_PLAYER = "only-for",
    // boolean
    COPY_PLAYER_SKIN = "copy-player";
}
