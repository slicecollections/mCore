package tk.slicecollections.maxteer.nms.v1_8_R3.entity;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.v1_8_R3.*;
import net.minecraft.server.v1_8_R3.WorldSettings.EnumGamemode;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import tk.slicecollections.maxteer.libraries.npclib.NPCLibrary;
import tk.slicecollections.maxteer.libraries.npclib.api.NPC;
import tk.slicecollections.maxteer.libraries.npclib.npc.ai.NPCHolder;
import tk.slicecollections.maxteer.libraries.npclib.npc.skin.Skin;
import tk.slicecollections.maxteer.libraries.npclib.npc.skin.SkinPacketTracker;
import tk.slicecollections.maxteer.libraries.npclib.npc.skin.SkinnableEntity;
import tk.slicecollections.maxteer.nms.NMS;
import tk.slicecollections.maxteer.nms.v1_8_R3.network.EmptyNetHandler;
import tk.slicecollections.maxteer.utils.Utils;

/**
 * @author Maxter
 */
public class EntityNPCPlayer extends EntityPlayer implements NPCHolder, SkinnableEntity {

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

    if (npc == null) {
      super.die(damagesource);
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

  public CraftPlayer getBukkitEntity() {
    if (this.npc != null && bukkitEntity == null) {
      bukkitEntity = new PlayerNPC(this);
    }

    return super.getBukkitEntity();
  }

  public void initialise() {
    this.playerConnection = new EmptyNetHandler(this);
    this.playerConnection.networkManager.a(playerConnection);
    NMS.setStepHeight(getBukkitEntity(), 1.0f);

    setSkinFlags((byte) -1);
  }

  @Override
  public boolean k_() {
    if (npc == null || !npc.data().get(NPC.FLYABLE, false)) {
      return super.k_();
    }

    return false;
  }

  @Override
  public void t_() {
    super.t_();
    if (npc == null) {
      return;
    }

    updatePackets();
    if (npc.data().get(NPC.GRAVITY, false) && getBukkitEntity() != null && Utils.isLoaded(getBukkitEntity().getLocation())) {
      move(0.0D, -0.2D, 0.0D);
    }

    if (Math.abs(this.motX) < 0.00499999988824129D && Math.abs(this.motY) < 0.00499999988824129D && Math.abs(this.motZ) < 0.00499999988824129D) {
      this.motX = this.motY = this.motZ = 0.0D;
    }
    if (this.motX != 0.0D || this.motZ != 0.0D || this.motY != 0.0D) {
      g(0.0F, 0.0F);
    }

    if (noDamageTicks > 0) {
      noDamageTicks--;
    }

    npc.update();
  }

  private int ticks = 0;

  private void updatePackets() {
    if (ticks++ > 30) {
      ticks = 0;
      for (Entity player : getEntity().getNearbyEntities(64.0, 64.0, 64.0)) {
        if (player instanceof Player) {
          if (!(player instanceof PlayerNPC)) {
            Packet<?>[] packets = new Packet<?>[6];
            packets[5] = new PacketPlayOutEntityHeadRotation(this, (byte) MathHelper.d(aK * 256.0F / 360.0F));
            for (int i = 0; i < 5; i++) {
              packets[i] = new PacketPlayOutEntityEquipment(getId(), i, getEquipment(i));
            }

            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityTeleport(this));
            for (Packet<?> packet : packets) {
              ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
            }
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
