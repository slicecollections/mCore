package tk.slicecollections.maxteer.nms.interfaces;

import java.util.Collection;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import tk.slicecollections.maxteer.libraries.holograms.api.Hologram;
import tk.slicecollections.maxteer.libraries.holograms.api.HologramLine;
import tk.slicecollections.maxteer.libraries.npclib.npc.skin.SkinnableEntity;
import tk.slicecollections.maxteer.nms.interfaces.entity.IArmorStand;

public interface INMS {

  public IArmorStand createArmorStand(Location location, String name, HologramLine line);

  public Hologram getHologram(Entity entity);

  public boolean isHologramEntity(Entity entity);

  public void setValueAndSignature(Player player, String value, String signature);

  public void sendTabListAdd(Player player, Player listPlayer);

  public void sendTabListRemove(Player player, Collection<SkinnableEntity> skinnableEntities);

  public void sendTabListRemove(Player player, Player listPlayer);

  public void removeFromPlayerList(Player player);

  public void removeFromServerPlayerList(Player player);

  public boolean addToWorld(World world, Entity entity, SpawnReason reason);

  public void removeFromWorld(Entity entity);

  public void replaceTrackerEntry(Player player);

  public void sendPacket(Player player, Object packet);

  public void look(Entity entity, float yaw, float pitch);

  public void setHeadYaw(Entity entity, float yaw);

  public void setStepHeight(LivingEntity entity, float height);

  public float getStepHeight(LivingEntity entity);

  public SkinnableEntity getSkinnable(Entity entity);

  public void flyingMoveLogic(LivingEntity entity, float f, float f1);

  public void sendActionBar(Player player, String message);

  public void sendTitle(Player player, String title, String subtitle);

  public void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut);

  public void sendTabHeaderFooter(Player player, String header, String footer);
}
