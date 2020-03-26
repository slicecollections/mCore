package tk.slicecollections.maxteer.libraries.npclib.npc.skin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import com.google.common.base.Preconditions;
import tk.slicecollections.maxteer.libraries.npclib.NPCLibrary;
import tk.slicecollections.maxteer.libraries.npclib.api.NPC;
import tk.slicecollections.maxteer.utils.Utils;

public class SkinUpdateTracker {

  private final Map<UUID, PlayerTracker> playerTrackers = new HashMap<UUID, PlayerTracker>(Bukkit.getMaxPlayers() / 2);

  public SkinUpdateTracker() {
    Preconditions.checkNotNull(NPCLibrary.getPlugin());

    for (Player player : Bukkit.getOnlinePlayers()) {
      if (!NPCLibrary.isNPC(player)) {
        playerTrackers.put(player.getUniqueId(), new PlayerTracker(player));
      }
    }
  }

  private boolean canSee(Player player, SkinnableEntity skinnable) {
    Player entity = skinnable.getEntity();
    if (entity == null) {
      return false;
    }

    if (!player.canSee(entity)) {
      return false;
    }

    if (!player.getWorld().equals(entity.getWorld())) {
      return false;
    }

    Location playerLoc = player.getLocation(CACHE_LOCATION);
    Location skinLoc = entity.getLocation(NPC_LOCATION);

    double viewDistance = 50.0;
    viewDistance *= viewDistance;

    if (playerLoc.distanceSquared(skinLoc) > viewDistance) {
      return false;
    }

    return true;
  }

  private Iterable<NPC> getAllNPCs() {
    return NPCLibrary.listNPCS();
  }

  private List<SkinnableEntity> getNearbyNPCs(Player player, boolean reset) {
    List<SkinnableEntity> results = new ArrayList<>();
    getTracker(player, reset);
    for (NPC npc : getAllNPCs()) {
      SkinnableEntity skinnable = getSkinnable(npc);
      if (skinnable == null) {
        continue;
      }

      if (canSee(player, skinnable)) {
        results.add(skinnable);
      }
    }

    return results;
  }

  @Nullable
  private SkinnableEntity getSkinnable(NPC npc) {
    Entity entity = npc.getEntity();
    if (entity == null) {
      return null;
    }

    return entity instanceof SkinnableEntity ? (SkinnableEntity) entity : null;
  }

  public PlayerTracker getTracker(Player player, boolean reset) {
    PlayerTracker tracker = playerTrackers.get(player.getUniqueId());
    if (tracker == null) {
      tracker = new PlayerTracker(player);
      playerTrackers.put(player.getUniqueId(), tracker);
    } else if (reset) {
      tracker.hardReset(player);
    }

    return tracker;
  }

  public void onNPCSpawn(NPC npc) {
    Preconditions.checkNotNull(npc);
    SkinnableEntity skinnable = getSkinnable(npc);
    if (skinnable == null) {
      return;
    }

    // reset nearby players in case they are not looking at the NPC when it spawns.
    resetNearbyPlayers(skinnable);
  }

  public void onPlayerMove(Player player) {
    Preconditions.checkNotNull(player);
    PlayerTracker updateTracker = playerTrackers.get(player.getUniqueId());
    if (updateTracker == null) {
      return;
    }

    if (!updateTracker.shouldUpdate(player)) {
      return;
    }

    updatePlayer(player, 10, false);
  }

  public void removePlayer(UUID playerId) {
    Preconditions.checkNotNull(playerId);
    playerTrackers.remove(playerId);
  }

  public void reset() {
    for (Player player : Bukkit.getOnlinePlayers()) {
      if (NPCLibrary.isNPC(player)) {
        continue;
      }

      PlayerTracker tracker = playerTrackers.get(player.getUniqueId());
      if (tracker == null) {
        continue;
      }

      tracker.hardReset(player);
    }
  }

  private void resetNearbyPlayers(SkinnableEntity skinnable) {
    Entity entity = skinnable.getEntity();
    if (entity == null || !entity.isValid()) {
      return;
    }

    double viewDistance = 50.0;
    viewDistance *= viewDistance;
    Location location = entity.getLocation(NPC_LOCATION);
    List<Player> players = entity.getWorld().getPlayers();
    for (Player player : players) {
      if (NPCLibrary.isNPC(player)) {
        continue;
      }

      Location ploc = player.getLocation(CACHE_LOCATION);
      if (ploc.getWorld() != location.getWorld()) {
        continue;
      }

      if (ploc.distanceSquared(location) > viewDistance) {
        continue;
      }

      PlayerTracker tracker = playerTrackers.get(player.getUniqueId());
      if (tracker != null) {
        tracker.hardReset(player);
      }
    }
  }

  public void updatePlayer(final Player player, long delay, final boolean reset) {
    if (NPCLibrary.isNPC(player)) {
      return;
    }

    new BukkitRunnable() {
      @Override
      public void run() {
        List<SkinnableEntity> visible = getNearbyNPCs(player, reset);

        for (SkinnableEntity skinnable : visible) {
          skinnable.getSkinTracker().updateViewer(player);
        }
      }
    }.runTaskLater(NPCLibrary.getPlugin(), delay);
  }

  private class PlayerTracker {
    boolean hasMoved;
    final Location location = new Location(null, 0, 0, 0);
    float lowerBound;
    int rotationCount;
    float startYaw;
    float upperBound;

    PlayerTracker(Player player) {
      hardReset(player);
    }

    // reset all
    void hardReset(Player player) {
      this.hasMoved = false;
      this.rotationCount = 0;
      this.lowerBound = this.upperBound = this.startYaw = 0;
      reset(player);
    }

    // resets initial yaw and location to the players current location and yaw.
    void reset(Player player) {
      player.getLocation(this.location);
      if (rotationCount < 3) {
        float rotationDegrees = 90.0f;
        float yaw = Utils.clampYaw(this.location.getYaw());
        this.startYaw = yaw;
        this.upperBound = Utils.clampYaw(yaw + rotationDegrees);
        this.lowerBound = Utils.clampYaw(yaw - rotationDegrees);
        if (upperBound == -180.0 && startYaw > 0) {
          upperBound = 0;
        }
      }
    }

    boolean shouldUpdate(Player player) {
      Location currentLoc = player.getLocation(CACHE_LOCATION);

      if (!hasMoved) {
        hasMoved = true;
        return true;
      }

      if (rotationCount < 3) {
        float yaw = Utils.clampYaw(currentLoc.getYaw());
        boolean hasRotated;
        if (startYaw - 90 < -180 || startYaw + 90 > 180) {
          hasRotated = yaw > lowerBound && yaw < upperBound;
        } else {
          hasRotated = yaw < lowerBound || yaw > upperBound;
        }

        // update the first 3 times the player rotates. helps load skins around player
        // after the player logs/teleports.
        if (hasRotated) {
          rotationCount++;
          reset(player);
          return true;
        }
      }

      // make sure player is in same world
      if (!currentLoc.getWorld().equals(this.location.getWorld())) {
        reset(player);
        return true;
      }

      // update every time a player moves a certain distance
      double distance = currentLoc.distanceSquared(this.location);
      if (distance > MOVEMENT_SKIN_UPDATE_DISTANCE) {
        reset(player);
        return true;
      } else {
        return false;
      }
    }
  }

  private static final Location CACHE_LOCATION = new Location(null, 0, 0, 0);
  private static final int MOVEMENT_SKIN_UPDATE_DISTANCE = 50 * 50;
  private static final Location NPC_LOCATION = new Location(null, 0, 0, 0);
}
