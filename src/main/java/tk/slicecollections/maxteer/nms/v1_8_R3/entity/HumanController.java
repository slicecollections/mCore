package tk.slicecollections.maxteer.nms.v1_8_R3.entity;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.v1_8_R3.PlayerInteractManager;
import net.minecraft.server.v1_8_R3.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import tk.slicecollections.maxteer.libraries.npclib.NPCLibrary;
import tk.slicecollections.maxteer.libraries.npclib.api.NPC;
import tk.slicecollections.maxteer.libraries.npclib.npc.AbstractEntityController;
import tk.slicecollections.maxteer.libraries.npclib.npc.skin.SkinnableEntity;
import tk.slicecollections.maxteer.nms.NMS;

import java.util.UUID;

/**
 * @author Maxter
 */
public class HumanController extends AbstractEntityController {

  @Override
  protected Entity createEntity(Location location, NPC npc) {
    WorldServer nmsWorld = ((CraftWorld) location.getWorld()).getHandle();
    UUID uuid = npc.getUUID();
    GameProfile profile = new GameProfile(uuid, npc.getName().substring(0, Math.min(npc.getName().length(), 16)));

    EntityNPCPlayer handle = new EntityNPCPlayer(nmsWorld.getMinecraftServer(), nmsWorld, profile, new PlayerInteractManager(nmsWorld), npc);

    handle.setPositionRotation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    Bukkit.getScheduler().scheduleSyncDelayedTask(NPCLibrary.getPlugin(), () -> {
      if (getBukkitEntity() != null && getBukkitEntity().isValid()) {
        NMS.removeFromPlayerList(handle.getBukkitEntity());
      }
    }, 20);
    handle.getBukkitEntity().setSleepingIgnored(true);

    return handle.getBukkitEntity();
  }

  @Override
  public Player getBukkitEntity() {
    return (Player) super.getBukkitEntity();
  }

  @Override
  public void remove() {
    Player entity = getBukkitEntity();
    if (entity != null) {
      NMS.removeFromWorld(entity);
      SkinnableEntity skinnable = NMS.getSkinnable(entity);
      skinnable.getSkinTracker().onRemoveNPC();
    }

    super.remove();
  }
}
