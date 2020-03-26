package tk.slicecollections.maxteer.libraries.holograms;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.plugin.Plugin;
import tk.slicecollections.maxteer.libraries.holograms.api.Hologram;

/**
 * @author Maxter
 */
public class HologramListeners implements Listener {

  private Plugin plugin;
  private final ListMultimap<ChunkCoord, Hologram> toRespawn = ArrayListMultimap.create();

  public HologramListeners() {
    this.plugin = HologramLibrary.getPlugin();
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onPluginDisable(PluginDisableEvent evt) {
    if (this.plugin.equals(evt.getPlugin())) {
      HologramLibrary.unregisterAll();
    }
  }

  @EventHandler(ignoreCancelled = true)
  public void onWorldLoad(WorldLoadEvent evt) {
    for (ChunkCoord coord : this.toRespawn.keys()) {
      if (coord.world.equals(evt.getWorld().getName()) && evt.getWorld().isChunkLoaded(coord.x, coord.z)) {
        respawnAllFromCoord(coord);
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onWorldUnload(WorldUnloadEvent evt) {
    for (Hologram hologram : HologramLibrary.listHolograms()) {
      if (hologram != null && hologram.isSpawned() && hologram.getLocation().getWorld().equals(evt.getWorld())) {
        if (evt.isCancelled()) {
          for (ChunkCoord coord : this.toRespawn.keys()) {
            if (coord.world.equals(evt.getWorld().getName())) {
              respawnAllFromCoord(coord);
            }
          }
          return;
        }

        hologram.despawn();
        storeForRespawn(hologram);
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
    for (Hologram hologram : HologramLibrary.listHolograms()) {
      if (hologram != null && hologram.isSpawned()) {
        location = hologram.getLocation().clone();

        if (toCoord(location).equals(coord)) {
          hologram.spawn();
          if (hologram.isSpawned()) {
            evt.setCancelled(true);
            respawnAllFromCoord(coord);
            return;
          }

          this.toRespawn.put(coord, hologram);
        }
      }
    }
  }

  private void respawnAllFromCoord(ChunkCoord coord) {
    for (ChunkCoord c : toRespawn.asMap().keySet()) {
      if (c.equals(coord)) {
        for (Hologram hologram : toRespawn.get(c)) {
          hologram.spawn();
        }

        toRespawn.asMap().remove(c);
      }
    }
  }

  private void storeForRespawn(Hologram hologram) {
    toRespawn.put(toCoord(hologram.getLocation()), hologram);
  }

  private ChunkCoord toCoord(Chunk chunk) {
    return new ChunkCoord(chunk);
  }

  private ChunkCoord toCoord(Location location) {
    return new ChunkCoord(location.getWorld().getName(), location.getBlockX() >> 4, location.getBlockZ() >> 4);
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
