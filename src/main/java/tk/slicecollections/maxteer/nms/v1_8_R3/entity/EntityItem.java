package tk.slicecollections.maxteer.nms.v1_8_R3.entity;

import net.minecraft.server.v1_8_R3.*;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftItem;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.util.Vector;
import tk.slicecollections.maxteer.libraries.holograms.HologramLibrary;
import tk.slicecollections.maxteer.libraries.holograms.api.HologramLine;
import tk.slicecollections.maxteer.nms.interfaces.entity.IItem;

import java.lang.reflect.Field;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;

/**
 * @author Maxter
 */
public class EntityItem extends net.minecraft.server.v1_8_R3.EntityItem implements IItem {

  private HologramLine line;

  public EntityItem(World world, HologramLine line) {
    super(world);
    super.pickupDelay = 0;
    this.line = line;
  }

  @Override
  public void t_() {
    this.ticksLived = 0;
  }

  @Override
  public void inactiveTick() {
    this.ticksLived = 0;
  }

  @Override
  public void d(EntityHuman entityHuman) {
    if (entityHuman.locY < this.locY - 1.5 || entityHuman.locY > this.locY + 1.0) {
      return;
    }

    if (entityHuman instanceof EntityPlayer && line.getPickupHandler() != null) {
      line.getPickupHandler().onPickup((Player) entityHuman.getBukkitEntity());
    }
  }

  @Override
  public void b(NBTTagCompound nbttagcompound) { }

  @Override
  public boolean c(NBTTagCompound nbttagcompound) {
    return false;
  }

  @Override
  public boolean d(NBTTagCompound nbttagcompound) {
    return false;
  }

  @Override
  public void e(NBTTagCompound nbttagcompound) { }

  @Override
  public boolean isInvulnerable(DamageSource source) {
    return true;
  }

  @Override
  public void die() {}

  @Override
  public boolean isAlive() {
    return false;
  }

  @Override
  public CraftEntity getBukkitEntity() {
    if (super.bukkitEntity == null) {
      this.bukkitEntity = new CraftItem(this);
    }

    return this.bukkitEntity;
  }

  @Override
  public void setPassengerOf(Entity entity) {
    if (entity == null) {
      return;
    }

    net.minecraft.server.v1_8_R3.Entity nms = ((CraftEntity) entity).getHandle();
    try {
      Field pitchDelta = net.minecraft.server.v1_8_R3.Entity.class.getDeclaredField("ar");
      pitchDelta.setAccessible(true);
      pitchDelta.set(this, 0.0);
      Field yawDelta = net.minecraft.server.v1_8_R3.Entity.class.getDeclaredField("as");
      yawDelta.setAccessible(true);
      yawDelta.set(this, 0.0);
    } catch (ReflectiveOperationException ex) {
      HologramLibrary.LOGGER.log(Level.SEVERE, "Couldnt set rider pitch and yaw: ", ex);
    }

    if (this.vehicle != null) {
      this.vehicle.passenger = null;
    }

    this.vehicle = nms;
    nms.passenger = this;
  }

  @Override
  public void setItemStack(org.bukkit.inventory.ItemStack item) {
    ItemStack newItem = CraftItemStack.asNMSCopy(item);

    if (newItem == null) {
      newItem = new ItemStack(Blocks.BEDROCK);
    }

    if (newItem.getTag() == null) {
      newItem.setTag(new NBTTagCompound());
    }
    NBTTagCompound display = newItem.getTag().getCompound("display");

    if (!newItem.getTag().hasKey("display")) {
      newItem.getTag().set("display", display);
    }

    NBTTagList tagList = new NBTTagList();
    tagList.add(new NBTTagString("§0" + ThreadLocalRandom.current().nextInt()));
    display.set("Lore", tagList);

    setItemStack(newItem);
  }

  @Override
  public void setLocation(double x, double y, double z) {
    super.setPosition(x, y, z);
  }

  @Override
  public boolean isDead() {
    return this.dead;
  }

  @Override
  public void killEntity() {
    super.dead = true;
  }

  @Override
  public Item getEntity() {
    return (Item) this.getBukkitEntity();
  }

  @Override
  public HologramLine getLine() {
    return this.line;
  }

  public static class CraftItem extends org.bukkit.craftbukkit.v1_8_R3.entity.CraftItem implements IItem {
    public CraftItem(EntityItem entity) {
      super(entity.world.getServer(), entity);
    }

    @Override
    public void remove() {}

    @Override
    public void setVelocity(Vector vel) { }

    @Override
    public boolean teleport(Location loc) { return false; }

    @Override
    public boolean teleport(Entity entity) { return false; }

    @Override
    public boolean teleport(Location loc, PlayerTeleportEvent.TeleportCause cause) { return false; }

    @Override
    public boolean teleport(Entity entity, PlayerTeleportEvent.TeleportCause cause) { return false; }

    @Override
    public void setFireTicks(int ticks) { }

    @Override
    public boolean setPassenger(Entity entity) { return false; }

    @Override
    public boolean eject() { return false; }

    @Override
    public boolean leaveVehicle() { return false; }

    @Override
    public void playEffect(EntityEffect effect) { }

    @Override
    public void setCustomName(String name) { }

    @Override
    public void setCustomNameVisible(boolean flag) { }

    @Override
    public void setPickupDelay(int delay) { }

    @Override
    public void setPassengerOf(Entity entity) {
      ((EntityItem) this.entity).setPassengerOf(entity);
    }

    @Override
    public void setLocation(double x, double y, double z) {
      ((EntityItem) this.entity).setLocation(x, y, z);
    }

    @Override
    public void killEntity() {
      ((EntityItem) this.entity).killEntity();
    }

    @Override
    public void setItemStack(org.bukkit.inventory.ItemStack stack) {}

    @Override
    public Item getEntity() {
      return this;
    }

    @Override
    public HologramLine getLine() {
      return ((EntityItem) this.entity).getLine();
    }
  }
}
