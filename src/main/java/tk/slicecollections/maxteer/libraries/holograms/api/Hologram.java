package tk.slicecollections.maxteer.libraries.holograms.api;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Hologram {

  private String attached;

  private boolean spawned;
  private Location location;
  private Map<Integer, HologramLine> lines = new HashMap<>();

  public Hologram(Location location, String... lines) {
    this.location = location;

    int current = 0;
    for (String line : lines) {
      this.lines.put(++current,
          new HologramLine(this, location.clone().add(0, 0.33 * current, 0), line));
    }
  }

  public void setAttached(String player) {
    this.attached = player;
  }

  public Hologram spawn() {
    if (spawned) {
      return this;
    }

    this.lines.values().forEach(HologramLine::spawn);
    this.spawned = true;
    return this;
  }

  public Hologram despawn() {
    if (!spawned) {
      return this;
    }

    this.lines.values().forEach(HologramLine::despawn);
    this.spawned = false;
    return this;
  }

  public Hologram withLine(String line) {
    int l = 1;
    while (this.lines.containsKey(l)) {
      l++;
    }

    this.lines.put(l, new HologramLine(this, this.location.clone().add(0, 0.33 * l, 0), line));
    if (spawned) {
      this.lines.get(l).spawn();
    }

    return this;
  }

  public Hologram updateLine(int id, String line) {
    if (!this.lines.containsKey(id)) {
      return this;
    }

    HologramLine hl = this.lines.get(id);
    hl.setLine(line);
    return this;
  }

  public boolean canSee(Player player) {
    return this.attached == null || this.attached.equals(player.getName());
  }
  
  public boolean isSpawned() {  
    return this.spawned;
  }
  
  public Location getLocation() {
    return this.location;
  }

  public HologramLine getLine(int id) {
    return this.lines.get(id);
  }
  
  public Collection<HologramLine> getLines() {
    return this.lines.values();
  }
}
