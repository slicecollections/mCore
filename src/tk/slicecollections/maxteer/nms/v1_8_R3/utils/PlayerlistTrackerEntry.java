package tk.slicecollections.maxteer.nms.v1_8_R3.utils;

import java.lang.reflect.Field;
import org.bukkit.entity.Player;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.EntityTrackerEntry;
import tk.slicecollections.maxteer.libraries.npclib.npc.skin.SkinnableEntity;
import tk.slicecollections.maxteer.nms.v1_8_R3.entity.EntityNPCPlayer;

/**
 * 
 * @author Maxter
 */
public class PlayerlistTrackerEntry extends EntityTrackerEntry {

  private static Field U;

  static {
    try {
      U = EntityTrackerEntry.class.getDeclaredField("u");
      U.setAccessible(true);
    } catch (ReflectiveOperationException e) {
      e.printStackTrace();
    }
  }

  static boolean getU(EntityTrackerEntry entry) {
    try {
      return (boolean) U.get(entry);
    } catch (ReflectiveOperationException e) {
      e.printStackTrace();
    }

    return false;
  }

  public PlayerlistTrackerEntry(Entity entity, int i, int j, boolean flag) {
    super(entity, i, j, flag);
  }

  public PlayerlistTrackerEntry(EntityTrackerEntry entry) {
    this(entry.tracker, entry.b, entry.c, getU(entry));
  }

  @Override
  public void updatePlayer(EntityPlayer entityplayer) {
    if (entityplayer instanceof EntityNPCPlayer) {
      return;
    }

    if (entityplayer != tracker && c(entityplayer)) {
      if (!trackedPlayers.contains(entityplayer)
          && (entityplayer.u().getPlayerChunkMap().a(entityplayer, tracker.ae, tracker.ag)
              || tracker.attachedToPlayer)) {
        if (tracker instanceof SkinnableEntity) {
          SkinnableEntity entity = (SkinnableEntity) tracker;

          Player player = entity.getEntity();
          if (entityplayer.getBukkitEntity().canSee(player)) {
            entity.getSkinTracker().updateViewer(entityplayer.getBukkitEntity());
          }
        }
      }
    }
    
    super.updatePlayer(entityplayer);
  }
}
