package tk.slicecollections.maxteer.libraries.npclib;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.plugin.Plugin;
import tk.slicecollections.maxteer.libraries.npclib.api.event.*;
import tk.slicecollections.maxteer.libraries.npclib.api.npc.NPC;
import tk.slicecollections.maxteer.libraries.npclib.npc.skin.SkinUpdateTracker;
import tk.slicecollections.maxteer.nms.NMS;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Maxter
 */
public class NPCListeners implements Listener {

  private Plugin plugin;
  private SkinUpdateTracker updateTracker;
  private final Map<Player, Long> antiSpam;
  private final ListMultimap<ChunkCoord, NPCInfo> toRespawn = ArrayListMultimap.create();

  NPCListeners() {
    this.plugin = NPCLibrary.getPlugin();
    this.updateTracker = new SkinUpdateTracker();
    this.antiSpam = new HashMap<>();
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onPluginDisable(PluginDisableEvent evt) {
    if (plugin.equals(evt.getPlugin())) {
      this.updateTracker.reset();
      this.antiSpam.clear();
      NPCLibrary.unregisterAll();
    }
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onEntityDeath(EntityDeathEvent evt) {
    if (NPCLibrary.isNPC(evt.getEntity())) {
      NPC npc = NPCLibrary.getNPC(evt.getEntity());

      NPCDeathEvent event = new NPCDeathEvent(npc, evt.getEntity().getKiller());
      Bukkit.getPluginManager().callEvent(event);
      if (!event.isCancelled()) {
        npc.destroy();
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onEntityDamage(EntityDamageEvent evt) {
    if (NPCLibrary.isNPC(evt.getEntity())) {
      NPC npc = NPCLibrary.getNPC(evt.getEntity());
      if (!npc.isProtected()) {
        evt.setCancelled(false);
      }
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onNPCSpawn(NPCSpawnEvent evt) {
    this.updateTracker.onNPCSpawn(evt.getNPC());
  }

  @EventHandler(ignoreCancelled = true)
  public void onPlayerChangedWorld(PlayerChangedWorldEvent evt) {
    if (!NPCLibrary.isNPC(evt.getPlayer())) {
      return;
    }

    NMS.removeFromServerPlayerList(evt.getPlayer());
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onPlayerChangeWorld(PlayerChangedWorldEvent evt) {
    this.updateTracker.updatePlayer(evt.getPlayer(), 20, true);
  }

  @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
  public void onPlayerMove(PlayerMoveEvent evt) {
    this.updateTracker.onPlayerMove(evt.getPlayer());
  }

  @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
  public void onPlayerJoin(PlayerJoinEvent evt) {
    this.updateTracker.updatePlayer(evt.getPlayer(), 20 * 6, true);
  }

  @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
  public void onPlayerQuit(PlayerQuitEvent evt) {
    this.updateTracker.removePlayer(evt.getPlayer().getUniqueId());
    this.antiSpam.remove(evt.getPlayer());
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onPlayerRespawn(PlayerRespawnEvent evt) {
    this.updateTracker.updatePlayer(evt.getPlayer(), 15L, true);
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onPlayerTeleport(PlayerTeleportEvent evt) {
    this.updateTracker.updatePlayer(evt.getPlayer(), 15L, true);
  }

  @EventHandler
  public void onPlayerInteractEntity(PlayerInteractEntityEvent evt) {
    if (NPCLibrary.isNPC(evt.getRightClicked())) {
      NPC npc = NPCLibrary.getNPC(evt.getRightClicked());
      long last = antiSpam.get(evt.getPlayer()) == null ? 0 : antiSpam.get(evt.getPlayer()) - System.currentTimeMillis();
      if (last > 0) {
        return;
      }

      antiSpam.put(evt.getPlayer(), System.currentTimeMillis() + 100);
      Bukkit.getPluginManager().callEvent(new NPCRightClickEvent(npc, evt.getPlayer()));
    }
  }

  @EventHandler
  public void onNPCNeedsRespawn(NPCNeedsRespawnEvent evt) {
    toRespawn.put(toCoord(evt.getNPC().getCurrentLocation()), new NPCInfo(evt.getNPC(), evt.getNPC().getCurrentLocation()));
  }

  @EventHandler(ignoreCancelled = true)
  public void onWorldLoad(WorldLoadEvent evt) {
    for (ChunkCoord coord : toRespawn.keys()) {
      if (coord.world.equals(evt.getWorld().getName()) && evt.getWorld().isChunkLoaded(coord.x, coord.z)) {
        respawnAllFromCoord(coord);
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onWorldUnload(WorldUnloadEvent evt) {
    for (NPC npc : NPCLibrary.listNPCS()) {
      if (npc != null && npc.isSpawned() && npc.getCurrentLocation().getWorld().equals(evt.getWorld())) {
        boolean despawned = npc.despawn();
        if (evt.isCancelled() || !despawned) {
          for (ChunkCoord coord : toRespawn.keys()) {
            if (coord.world.equals(evt.getWorld().getName())) {
              respawnAllFromCoord(coord);
            }
          }
          return;
        }

        storeForRespawn(npc);
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onChunkLoad(ChunkLoadEvent evt) {
    respawnAllFromCoord(toCoord(evt.getChunk()));
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onChunkUnload(ChunkUnloadEvent evt) {
    ChunkCoord coord = toCoord(evt.getChunk());
    Location location = new Location(null, 0, 0, 0);
    for (NPC npc : NPCLibrary.listNPCS()) {
      if (npc != null && npc.isSpawned()) {
        location = npc.getEntity().getLocation(location);

        if (toCoord(location).equals(coord)) {
          if (!npc.despawn()) {
            evt.setCancelled(true);
            respawnAllFromCoord(coord);
            return;
          }

          this.toRespawn.put(coord, new NPCInfo(npc, location));
        }
      }
    }
  }

  @EventHandler
  public void onEntityDamage(EntityDamageByEntityEvent evt) {
    if (NPCLibrary.isNPC(evt.getEntity())) {
      NPC npc = NPCLibrary.getNPC(evt.getEntity());

      evt.setCancelled(npc.isProtected());
      if (evt.getDamager() instanceof Player) {
        Player player = (Player) evt.getDamager();
        long last = antiSpam.get(player) == null ? 0 : antiSpam.get(player) - System.currentTimeMillis();
        if (last > 0) {
          return;
        }

        antiSpam.put(player, System.currentTimeMillis() + 100);
        Bukkit.getPluginManager().callEvent(new NPCLeftClickEvent(npc, player));
      }
    }
  }

  private void respawnAllFromCoord(ChunkCoord coord) {
    for (ChunkCoord c : toRespawn.asMap().keySet()) {
      if (c.equals(coord)) {
        for (NPCInfo info : toRespawn.get(c)) {
          info.npc.spawn(info.location);
        }

        toRespawn.asMap().remove(c);
      }
    }
  }

  private void storeForRespawn(NPC npc) {
    toRespawn.put(toCoord(npc.getEntity().getLocation()), new NPCInfo(npc, npc.getEntity().getLocation()));
  }

  private ChunkCoord toCoord(Chunk chunk) {
    return new ChunkCoord(chunk);
  }

  private ChunkCoord toCoord(Location location) {
    return new ChunkCoord(location.getWorld().getName(), location.getBlockX() >> 4, location.getBlockZ() >> 4);
  }

  private static class NPCInfo {
    private NPC npc;
    private Location location;

    public NPCInfo(NPC npc, Location location) {
      this.npc = npc;
      this.location = location;
    }
  }


  private static class ChunkCoord {

    private String world;
    private int x, z;

    private ChunkCoord(Chunk chunk) {
      this(chunk.getWorld().getName(), chunk.getX(), chunk.getZ());
    }

    private ChunkCoord(String world, int x, int z) {
      this.world = world;
      this.x = x;
      this.z = z;
    }

    public boolean equals(Object obj) {
      if (!(obj instanceof ChunkCoord)) {
        return false;
      }
      if (this == obj) {
        return true;
      }

      ChunkCoord other = (ChunkCoord) obj;
      if (world == null) {
        if (other.world != null) {
          return false;
        }
      } else if (!world.equals(other.world)) {
        return false;
      }

      return this.x == other.x && this.z == other.z;
    }
  }
}
