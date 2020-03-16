package tk.slicecollections.maxteer.libraries.npclib.npc.skin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import com.google.common.base.Preconditions;
import tk.slicecollections.maxteer.libraries.npclib.NPCLibrary;
import tk.slicecollections.maxteer.libraries.npclib.api.NPC;
import tk.slicecollections.maxteer.nms.NMS;

public class SkinPacketTracker {

  private final SkinnableEntity entity;
  private final Map<UUID, PlayerEntry> inProgress = new HashMap<>(Bukkit.getMaxPlayers() / 2);

  private boolean isRemoved;

  public SkinPacketTracker(SkinnableEntity entity) {
    Preconditions.checkNotNull(entity);

    this.entity = entity;

    if (LISTENER == null) {
      LISTENER = new PlayerListener();
      Bukkit.getPluginManager().registerEvents(LISTENER, NPCLibrary.getPlugin());
    }
  }

  public boolean shouldRemoveFromTabList() {
    return entity.getNPC().data().get(NPC.TAB_LIST_KEY, true);
  }

  void notifyRemovePacketCancelled(UUID playerId) {
    inProgress.remove(playerId);
  }

  void notifyRemovePacketSent(UUID playerId) {
    PlayerEntry entry = inProgress.get(playerId);
    if (entry == null)
      return;

    if (entry.removeCount == 0)
      return;

    entry.removeCount -= 1;
    if (entry.removeCount == 0) {
      inProgress.remove(playerId);
    } else {
      scheduleRemovePacket(entry);
    }
  }

  public void onRemoveNPC() {
    isRemoved = true;

    for (Player player : Bukkit.getOnlinePlayers()) {
      if (NPCLibrary.isNPC(player)) {
        continue;
      }

      // send packet now and later to ensure removal from player list
      NMS.sendTabListRemove(player, entity.getEntity());
      TAB_LIST_REMOVER.sendPacket(player, entity);
    }
  }

  public void onSpawnNPC() {
    isRemoved = false;
    new BukkitRunnable() {
      @Override
      public void run() {
        if (!entity.getNPC().isSpawned()) {
          return;
        }

        updateNearbyViewers(50.0);
      }
    }.runTaskLater(NPCLibrary.getPlugin(), 20);
  }

  private void scheduleRemovePacket(final PlayerEntry entry) {
    if (isRemoved || NPCLibrary.getPlugin() == null || !NPCLibrary.getPlugin().isEnabled()) {
      return;
    }

    entry.removeTask = Bukkit.getScheduler().runTaskLater(NPCLibrary.getPlugin(), new Runnable() {
      @Override
      public void run() {
        if (shouldRemoveFromTabList()) {
          TAB_LIST_REMOVER.sendPacket(entry.player, entity);
        }
      }
    }, PACKET_DELAY_REMOVE);
  }

  private void scheduleRemovePacket(PlayerEntry entry, int count) {
    if (!shouldRemoveFromTabList()) {
      return;
    }

    entry.removeCount = count;
    scheduleRemovePacket(entry);
  }

  public void updateNearbyViewers(double radius) {
    radius *= radius;

    World world = entity.getEntity().getWorld();
    Player from = entity.getEntity();
    Location location = from.getLocation();

    for (Player player : world.getPlayers()) {
      if (player == null || NPCLibrary.isNPC(player)) {
        continue;
      }

      player.getLocation(CACHE_LOCATION);
      if (!player.canSee(from) || !location.getWorld().equals(CACHE_LOCATION.getWorld())) {
        continue;
      }

      if (location.distanceSquared(CACHE_LOCATION) > radius) {
        continue;
      }

      updateViewer(player);
    }
  }

  public void updateViewer(final Player player) {
    Preconditions.checkNotNull(player);

    if (isRemoved || NPCLibrary.isNPC(player)) {
      return;
    }

    PlayerEntry entry = inProgress.get(player.getUniqueId());
    if (entry != null) {
      entry.cancel();
    } else {
      entry = new PlayerEntry(player);
    }

    TAB_LIST_REMOVER.cancelPackets(player, entity);

    inProgress.put(player.getUniqueId(), entry);
    if (entity.getSkin() != null) {
      entity.getSkin().apply(entity);
    }
    NMS.sendTabListAdd(player, entity.getEntity());

    scheduleRemovePacket(entry, 2);
  }

  private class PlayerEntry {

    Player player;
    int removeCount;
    BukkitTask removeTask;

    PlayerEntry(Player player) {
      this.player = player;
    }

    // cancel previous packet tasks so they do not interfere with
    // new tasks
    void cancel() {
      if (removeTask != null) {
        removeTask.cancel();
      }

      removeCount = 0;
    }
  }

  private static class PlayerListener implements Listener {

    @EventHandler
    private void onPlayerQuit(PlayerQuitEvent event) {
      TAB_LIST_REMOVER.cancelPackets(event.getPlayer());
    }
  }

  private static final Location CACHE_LOCATION = new Location(null, 0, 0, 0);
  private static PlayerListener LISTENER;
  private static final int PACKET_DELAY_REMOVE = 1;
  private static final TabListRemover TAB_LIST_REMOVER = new TabListRemover();
}
