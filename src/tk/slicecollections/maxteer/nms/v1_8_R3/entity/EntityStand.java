package tk.slicecollections.maxteer.nms.v1_8_R3.entity;

import java.lang.reflect.Field;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftArmorStand;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.ArmorStand;
import org.bukkit.util.EulerAngle;
import net.minecraft.server.v1_8_R3.DamageSource;
import net.minecraft.server.v1_8_R3.EntityArmorStand;
import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.MathHelper;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_8_R3.Vector3f;
import tk.slicecollections.maxteer.libraries.holograms.api.HologramLine;
import tk.slicecollections.maxteer.nms.interfaces.entity.IArmorStand;
import tk.slicecollections.maxteer.nms.v1_8_R3.utils.NullBoundingBox;

public class EntityStand extends EntityArmorStand implements IArmorStand {

  public EntityStand(Location toSpawn) {
    super(((CraftWorld) toSpawn.getWorld()).getHandle());
    setArms(false);
    setBasePlate(true);
    setInvisible(true);
    setGravity(false);
    setSmall(true);
    
    try {
      Field field = net.minecraft.server.v1_8_R3.EntityArmorStand.class.getDeclaredField("bi");
      field.setAccessible(true);
      field.set(this, 2147483647);
    } catch (Exception e) {
      e.printStackTrace();
    }
    a(new NullBoundingBox());
  }

  public boolean isInvulnerable(DamageSource source) {
    return true;
  }

  public void setCustomName(String customName) {}

  public void setCustomNameVisible(boolean visible) {}

  public void t_() {
    this.ticksLived = 0;
    super.t_();
  }

  public void makeSound(String sound, float f1, float f2) {}

  @Override
  public int getId() {
    return super.getId();
  }
  
  @Override
  public void setName(String text) {
    if (text != null && text.length() > 300) {
      text = text.substring(0, 300);
    }

    super.setCustomName(text == null ? "" : text);
    super.setCustomNameVisible(text != null && !text.isEmpty());
  }

  @Override
  public void killEntity() {
    super.die();
  }

  @Override
  public HologramLine getLine() {
    return null;
  }

  @Override
  public ArmorStand getEntity() {
    return (ArmorStand) getBukkitEntity();
  }

  @Override
  public CraftEntity getBukkitEntity() {
    if (bukkitEntity == null) {
      bukkitEntity = new CraftStand(this);
    }

    return super.getBukkitEntity();
  }

  @Override
  public void setLocation(double x, double y, double z) {
    super.setPosition(x, y, z);

    PacketPlayOutEntityTeleport teleportPacket = new PacketPlayOutEntityTeleport(getId(), MathHelper.floor(this.locX * 32.0D), MathHelper.floor(this.locY * 32.0D),
        MathHelper.floor(this.locZ * 32.0D), (byte) (int) (this.yaw * 256.0F / 360.0F), (byte) (int) (this.pitch * 256.0F / 360.0F), this.onGround);

    for (EntityHuman obj : world.players) {
      if (obj instanceof EntityPlayer) {
        EntityPlayer nmsPlayer = (EntityPlayer) obj;

        double distanceSquared = square(nmsPlayer.locX - this.locX) + square(nmsPlayer.locZ - this.locZ);
        if (distanceSquared < 8192.0 && nmsPlayer.playerConnection != null) {
          nmsPlayer.playerConnection.sendPacket(teleportPacket);
        }
      }
    }
  }

  private static double square(double num) {
    return num * num;
  }

  @Override
  public boolean isDead() {
    return dead;
  }

  static class CraftStand extends CraftArmorStand implements IArmorStand {

    public CraftStand(EntityStand entity) {
      super(entity.world.getServer(), entity);
    }
    
    @Override
    public int getId() {
      return ((EntityStand) entity).getId();
    }

    @Override
    public void setHeadPose(EulerAngle pose) {
      ((EntityStand) entity).setHeadPose(new Vector3f((float)pose.getX(), (float)pose.getY(), (float)pose.getZ()));
    }
    
    @Override
    public void setLeftArmPose(EulerAngle pose) {
      ((EntityStand) entity).setLeftArmPose(new Vector3f((float)pose.getX(), (float)pose.getY(), (float)pose.getZ()));
    }
    
    @Override
    public void setLeftLegPose(EulerAngle pose) {
      ((EntityStand) entity).setLeftLegPose(new Vector3f((float)pose.getX(), (float)pose.getY(), (float)pose.getZ()));
    }
    
    @Override
    public void setRightLegPose(EulerAngle pose) {
      ((EntityStand) entity).setRightLegPose(new Vector3f((float)pose.getX(), (float)pose.getY(), (float)pose.getZ()));
    }
    
    @Override
    public void setName(String text) {
      ((EntityStand) entity).setName(text);
    }

    @Override
    public void killEntity() {
      ((EntityStand) entity).killEntity();
    }

    @Override
    public HologramLine getLine() {
      return ((EntityStand) entity).getLine();
    }

    @Override
    public ArmorStand getEntity() {
      return (ArmorStand) this;
    }

    @Override
    public void setLocation(double x, double y, double z) {
      ((EntityStand) entity).setLocation(x, y, z);
    }
  }
}
