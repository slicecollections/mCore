package tk.slicecollections.maxteer.nms.v1_8_R3.entity;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.v1_8_R3.*;
import net.minecraft.server.v1_8_R3.WorldSettings.EnumGamemode;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import tk.slicecollections.maxteer.libraries.npclib.NPCLibrary;
import tk.slicecollections.maxteer.libraries.npclib.api.npc.NPC;
import tk.slicecollections.maxteer.libraries.npclib.api.npc.NPCAnimation;
import tk.slicecollections.maxteer.libraries.npclib.npc.ai.NPCHolder;
import tk.slicecollections.maxteer.libraries.npclib.npc.skin.Skin;
import tk.slicecollections.maxteer.libraries.npclib.npc.skin.SkinPacketTracker;
import tk.slicecollections.maxteer.libraries.npclib.npc.skin.SkinnableEntity;
import tk.slicecollections.maxteer.nms.NMS;
import tk.slicecollections.maxteer.nms.v1_8_R3.network.EmptyNetHandler;
import tk.slicecollections.maxteer.nms.v1_8_R3.utils.PlayerNavigation;
import tk.slicecollections.maxteer.nms.v1_8_R3.utils.controllers.PlayerControllerJump;
import tk.slicecollections.maxteer.nms.v1_8_R3.utils.controllers.PlayerControllerLook;
import tk.slicecollections.maxteer.nms.v1_8_R3.utils.controllers.PlayerControllerMove;
import tk.slicecollections.maxteer.utils.Utils;

/**
 * @author Maxter
 */
public class EntityNPCPlayer extends EntityPlayer implements NPCHolder, SkinnableEntity {

  private PlayerControllerJump controllerJump;
  private PlayerControllerLook controllerLook;
  private PlayerControllerMove controllerMove;
  private int jumpTicks = 0;
  private PlayerNavigation navigation;

  private final NPC npc;
  private Skin skin;
  private final SkinPacketTracker skinTracker;

  public EntityNPCPlayer(MinecraftServer server, WorldServer world, GameProfile profile, PlayerInteractManager manager, NPC npc) {
    super(server, world, profile, manager);

    this.npc = npc;
    if (npc != null) {
      manager.setGameMode(EnumGamemode.SURVIVAL);
      skinTracker = new SkinPacketTracker(this);
      initialise();
    } else {
      skinTracker = null;
    }
  }

  protected void a(double d0, boolean flag, Block block, BlockPosition blockposition) {
    if (npc == null || !npc.data().get(NPC.FLYABLE, false)) {
      super.a(d0, flag, block, blockposition);
    }
  }

  @Override
  public void collide(net.minecraft.server.v1_8_R3.Entity entity) {
    super.collide(entity);
  }

  @Override
  public boolean damageEntity(DamageSource damagesource, float f) {
   return super.damageEntity(damagesource, f);
  }

  public void die(DamageSource damagesource) {
    if (this.dead) {
      return;
    }

    super.die(damagesource);
    Bukkit.getScheduler().runTaskLater(NPCLibrary.getPlugin(), () -> world.removeEntity(EntityNPCPlayer.this), 35L);
  }

  @Override
  public void e(float f, float f1) {
    if (npc == null || !npc.data().get(NPC.FLYABLE, false)) {
      super.e(f, f1);
    }
  }

  @Override
  public void g(double d0, double d1, double d2) {
    if (npc == null || !npc.isProtected()) {
      super.g(d0, d1, d2);
    }
  }

  @Override
  public void g(float f, float f1) {
    if (npc == null || !npc.data().get(NPC.FLYABLE, false)) {
      super.g(f, f1);
    } else {
      NMS.flyingMoveLogic(getBukkitEntity(), f, f1);
    }
  }

  public PlayerControllerJump getControllerJump() {
    return controllerJump;
  }

  public PlayerControllerMove getControllerMove() {
    return controllerMove;
  }

  public PlayerNavigation getNavigation() {
    return navigation;
  }

  public CraftPlayer getBukkitEntity() {
    if (this.npc != null && bukkitEntity == null) {
      bukkitEntity = new PlayerNPC(this);
    }

    return super.getBukkitEntity();
  }

  public void initialise() {
    this.invulnerableTicks = 0;
    this.playerConnection = new EmptyNetHandler(this);
    this.playerConnection.networkManager.a(playerConnection);
    AttributeInstance range = this.getAttributeInstance(GenericAttributes.FOLLOW_RANGE);
    if (range == null) {
      range = this.getAttributeMap().b(GenericAttributes.FOLLOW_RANGE);
    }
    range.setValue(25.0D);
    this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(1.0);

    this.controllerJump = new PlayerControllerJump(this);
    this.controllerLook = new PlayerControllerLook(this);
    this.controllerMove = new PlayerControllerMove(this);
    this.navigation = new PlayerNavigation(this, world);
    NMS.setStepHeight(getBukkitEntity(), 1.0f);

    setSkinFlags((byte) 0xFF);
  }

  public boolean isNavigating() {
    return !this.getNavigation().m();
  }

  @Override
  public boolean k_() {
    if (npc == null || !npc.data().get(NPC.FLYABLE, false)) {
      return super.k_();
    }

    return false;
  }

  public void livingEntityBaseTick() {
    if (!this.world.isClientSide) {
      b(0, this.fireTicks > 0);
    }
    this.ay = this.az;
    this.aE = this.aF;
    if (this.hurtTicks > 0) {
      this.hurtTicks -= 1;
    }
    bi();
    this.aU = this.aT;
    this.aJ = this.aI;
    this.aL = this.aK;
    this.lastYaw = this.yaw;
    this.lastPitch = this.pitch;
  }

  private void moveOnCurrentHeading() {
    if (aY) {
      if (onGround && jumpTicks == 0) {
        bF();
        jumpTicks = 10;
      }
    } else {
      jumpTicks = 0;
    }
    aZ *= 0.98F;
    ba *= 0.98F;
    bb *= 0.9F;
    g(aZ, ba);
    NMS.setHeadYaw(getBukkitEntity(), yaw);
    if (jumpTicks > 0) {
      jumpTicks--;
    }
  }

  @Override
  public void t_() {
    super.t_();
    if (npc == null) {
      return;
    }

    this.noclip = isSpectator();
    livingEntityBaseTick();
    boolean navigating = this.isNavigating();
    updatePackets(navigating);
    if (!navigating && npc.data().get(NPC.GRAVITY, false) && getBukkitEntity() != null && Utils.isLoaded(getBukkitEntity().getLocation())) {
      g(0, 0);
    }

    if (Math.abs(this.motX) < 0.00499999988824129D && Math.abs(this.motY) < 0.00499999988824129D && Math.abs(this.motZ) < 0.00499999988824129D) {
      this.motX = this.motY = this.motZ = 0;
    }
    if (navigating) {
      if (!this.getNavigation().m()) {
        this.getNavigation().k();
      }

      this.moveOnCurrentHeading();
    }
    this.startNavigating();
    this.controllerMove.c();
    this.controllerLook.a();
    this.controllerJump.b();

    if (noDamageTicks > 0) {
      noDamageTicks--;
    }

    npc.update();
  }

  private void startNavigating() {
    Location location = this.getNPC().getWalkingTo();
    if (location == null) {
      Entity following = this.getNPC().getFollowing();
      if (following != null) {
        location = following.getLocation();
        if (!location.getWorld().equals(this.getBukkitEntity().getWorld())) {
          this.getNPC().setFollowing(null);
          return;
        }

        double distance = location.distance(this.getBukkitEntity().getLocation());
        if (distance > this.getAttributeInstance(GenericAttributes.FOLLOW_RANGE).getValue()) {
          this.getNPC().setFollowing(null);
          return;
        }

        this.getNavigation().a(location.getX() + 1, location.getY(), location.getZ() + 1, this.isSprinting() ? 1.3 : 1.0);
      }
    }

    if (location != null) {
      double distance = location.distance(this.getBukkitEntity().getLocation());
      if (distance > this.getAttributeInstance(GenericAttributes.FOLLOW_RANGE).getValue() || distance < 1) {
        this.getNPC().finishNavigation();
        return;
      }

      this.getNPC().setWalkingTo(null);
      this.getNavigation().a(location.getX(), location.getY(), location.getZ(), this.isSprinting() ? 1.3 : 1.0);
    } else if (this.getNPC().isNavigating() && !this.isNavigating()) {
      this.getNPC().finishNavigation();
    }
  }

  public void playAnimation(NPCAnimation animation) {
    PacketPlayOutAnimation packet = new PacketPlayOutAnimation(this, animation.getId());
    for (Entity player : getEntity().getNearbyEntities(64.0, 64.0, 64.0)) {
      if (player instanceof Player && !(player instanceof PlayerNPC)) {
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
      }
    }
  }

  private int ticks = 0;

  private void updatePackets(boolean navigating) {
    if (ticks++ > 30) {
      ticks = 0;
      Packet<?>[] packets = new Packet<?>[navigating ? 5 : 6];
      if (!navigating) {
        packets[5] = new PacketPlayOutEntityHeadRotation(this, (byte) MathHelper.d(aK * 256.0F / 360.0F));
      }
      for (int i = 0; i < 5; i++) {
        packets[i] = new PacketPlayOutEntityEquipment(getId(), i, getEquipment(i));
      }

      PacketPlayOutEntityTeleport teleport = new PacketPlayOutEntityTeleport(this);
      for (Entity player : getEntity().getNearbyEntities(64.0, 64.0, 64.0)) {
        if (player instanceof Player && !(player instanceof PlayerNPC)) {
          if (!navigating) {
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(teleport);
          }
          for (Packet<?> packet : packets) {
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
          }
        }
      }

      NMS.removeFromPlayerList(getBukkitEntity());
    }
  }

  @Override
  public Player getEntity() {
    return getBukkitEntity();
  }

  @Override
  public NPC getNPC() {
    return npc;
  }

  @Override
  public SkinPacketTracker getSkinTracker() {
    return skinTracker;
  }

  @Override
  public void setSkin(Skin skin) {
    if (skin != null) {
      skin.apply(this);
    }

    this.skin = skin;
  }

  @Override
  public Skin getSkin() {
    return skin;
  }

  @Override
  public void setSkinFlags(byte flags) {
    try {
      getDataWatcher().watch(10, flags);
    } catch (NullPointerException e) {
      getDataWatcher().a(10, flags);
    }
  }

  static class PlayerNPC extends CraftPlayer implements NPCHolder, SkinnableEntity {

    private NPC npc;

    public PlayerNPC(EntityNPCPlayer entity) {
      super(entity.world.getServer(), entity);
      this.npc = entity.npc;
    }

    @Override
    public Player getEntity() {
      return this;
    }

    @Override
    public SkinPacketTracker getSkinTracker() {
      return ((SkinnableEntity) entity).getSkinTracker();
    }

    @Override
    public NPC getNPC() {
      return npc;
    }

    @Override
    public void setSkin(Skin skin) {
      ((SkinnableEntity) entity).setSkin(skin);
    }

    @Override
    public Skin getSkin() {
      return ((SkinnableEntity) entity).getSkin();
    }

    @Override
    public void setSkinFlags(byte flags) {
      ((SkinnableEntity) entity).setSkinFlags(flags);
    }
  }
}
