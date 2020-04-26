package tk.slicecollections.maxteer.utils;

import static java.lang.Integer.parseInt;

import java.util.Iterator;
import java.util.Random;

import com.avaje.ebean.validation.NotNull;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

/**
 * Interface para criação de CuboId através de localizações do Bukkit.
 *
 * @author Maxter
 */
public class CubeID implements Iterable<Block> {

  private String world;
  private int xmax, xmin, ymax, ymin, zmax, zmin;

  public CubeID(Location l1, Location l2) {
    this.world = l1.getWorld().getName();
    this.xmax = Math.max(l1.getBlockX(), l2.getBlockX());
    this.xmin = Math.min(l1.getBlockX(), l2.getBlockX());
    this.ymax = Math.max(l1.getBlockY(), l2.getBlockY());
    this.ymin = Math.min(l1.getBlockY(), l2.getBlockY());
    this.zmax = Math.max(l1.getBlockZ(), l2.getBlockZ());
    this.zmin = Math.min(l1.getBlockZ(), l2.getBlockZ());
  }

  public CubeID(String serializedCube) {
    String[] split = serializedCube.split("; ");
    this.world = split[0];
    this.xmax = parseInt(split[1]);
    this.xmin = parseInt(split[2]);
    this.ymax = parseInt(split[3]);
    this.ymin = parseInt(split[4]);
    this.zmax = parseInt(split[5]);
    this.zmin = parseInt(split[6]);
  }

  public CubeIterator iterator() {
    return new CubeIterator(this);
  }

  public Location getRandomLocation() {
    int x = new Random().nextInt(xmax - xmin) + 1;
    int y = new Random().nextInt(xmax - xmin) + 1;
    int z = new Random().nextInt(xmax - xmin) + 1;
    return new Location(Bukkit.getWorld(world), xmin + x, ymin + y, zmin + z);
  }

  public Location getCenterLocation() {
    double x = xmin + ((xmax + 1) - xmin) / 2.0, z = zmin + ((zmax + 1) - zmin) / 2.0;
    World world = Bukkit.getWorld(this.world);
    return new Location(world, x, ymax - 10, z);
  }

  public boolean contains(Location loc) {
    return loc != null && loc.getWorld().getName().equals(world) && loc.getBlockX() >= xmin && loc.getBlockX() <= xmax && loc.getBlockY() >= ymin && loc.getBlockY() <= ymax && loc
      .getBlockZ() >= zmin && loc.getBlockZ() <= zmax;
  }

  public String getWorld() {
    return world;
  }

  public int getXmin() {
    return xmin;
  }

  public int getXmax() {
    return xmax;
  }

  @Override
  public String toString() {
    return world + "; " + xmax + "; " + xmin + "; " + ymax + "; " + ymin + "; " + zmax + "; " + zmin;
  }

  public void setXmin(int xmin) {
    this.xmin = xmin;
  }

  public int getYmax() {
    return ymax;
  }

  public int getYmin() {
    return ymin;
  }

  public int getZmax() {
    return zmax;
  }

  public int getZmin() {
    return zmin;
  }

  public class CubeIterator implements Iterator<Block> {
    String world;
    CubeID cuboId;
    int baseX, baseY, baseZ, sizeX, sizeY, sizeZ, x, y, z;

    public CubeIterator(CubeID cuboId) {
      x = y = z = 0;
      baseX = getXmin();
      baseY = getYmin();
      baseZ = getZmin();
      this.cuboId = cuboId;
      this.world = cuboId.getWorld();
      sizeX = Math.abs(getXmax() - getXmin()) + 1;
      sizeY = Math.abs(getYmax() - getYmin()) + 1;
      sizeZ = Math.abs(getZmax() - getZmin()) + 1;
    }

    public boolean hasNext() {
      return x < sizeX && y < sizeY && z < sizeZ;
    }

    public Block next() {
      Block block = Bukkit.getWorld(world).getBlockAt(baseX + x, baseY + y, baseZ + z);
      if (++x >= sizeX) {
        x = 0;
        if (++y >= sizeY) {
          y = 0;
          ++z;
        }
      }

      return block;
    }

    public void remove() {
      // Do anything
    }
  }
}
