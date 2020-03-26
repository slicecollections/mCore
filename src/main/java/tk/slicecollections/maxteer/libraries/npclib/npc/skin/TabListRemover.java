package tk.slicecollections.maxteer.libraries.npclib.npc.skin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import com.google.common.base.Preconditions;
import tk.slicecollections.maxteer.libraries.npclib.NPCLibrary;
import tk.slicecollections.maxteer.nms.NMS;

class TabListRemover {

  private final Map<UUID, PlayerEntry> pending = new HashMap<>(Bukkit.getMaxPlayers() / 2);

  TabListRemover() {
    Bukkit.getScheduler().runTaskTimer(NPCLibrary.getPlugin(), new Sender(), 2, 2);
  }

  public void cancelPackets(Player player) {
    Preconditions.checkNotNull(player);

    PlayerEntry entry = pending.remove(player.getUniqueId());
    if (entry == null) {
      return;
    }

    for (SkinnableEntity entity : entry.toRemove) {
      entity.getSkinTracker().notifyRemovePacketCancelled(player.getUniqueId());
    }
  }

  public void cancelPackets(Player player, SkinnableEntity skinnable) {
    Preconditions.checkNotNull(player);
    Preconditions.checkNotNull(skinnable);

    PlayerEntry entry = pending.get(player.getUniqueId());
    if (entry == null) {
      return;
    }

    if (entry.toRemove.remove(skinnable)) {
      skinnable.getSkinTracker().notifyRemovePacketCancelled(player.getUniqueId());
    }

    if (entry.toRemove.isEmpty()) {
      pending.remove(player.getUniqueId());
    }
  }

  private PlayerEntry getEntry(Player player) {
    PlayerEntry entry = pending.get(player.getUniqueId());
    if (entry == null) {
      entry = new PlayerEntry(player);
      pending.put(player.getUniqueId(), entry);
    }

    return entry;
  }

  public void sendPacket(Player player, SkinnableEntity entity) {
    Preconditions.checkNotNull(player);
    Preconditions.checkNotNull(entity);

    PlayerEntry entry = getEntry(player);

    entry.toRemove.add(entity);
  }

  private class Sender implements Runnable {

    private Sender() {}

    public void run() {
      int maxPacketEntries = 15;

      Iterator<Map.Entry<UUID, PlayerEntry>> entryIterator = pending.entrySet().iterator();
      while (entryIterator.hasNext()) {

        Map.Entry<UUID, PlayerEntry> mapEntry = entryIterator.next();
        PlayerEntry entry = mapEntry.getValue();

        int listSize = Math.min(maxPacketEntries, entry.toRemove.size());
        boolean sendAll = listSize == entry.toRemove.size();

        List<SkinnableEntity> skinnableList = new ArrayList<SkinnableEntity>(listSize);

        int i = 0;
        Iterator<SkinnableEntity> skinIterator = entry.toRemove.iterator();
        while (skinIterator.hasNext()) {
          if (i >= maxPacketEntries) {
            break;
          }

          SkinnableEntity skinnable = skinIterator.next();
          skinnableList.add(skinnable);

          skinIterator.remove();
          i++;
        }

        if (entry.player.isOnline()) {
          NMS.sendTabListRemove(entry.player, skinnableList);
        }

        // notify skin trackers that a remove packet has been sent to a player
        for (SkinnableEntity entity : skinnableList) {
          entity.getSkinTracker().notifyRemovePacketSent(entry.player.getUniqueId());
        }

        if (sendAll) {
          entryIterator.remove();
        }
      }
    }
  }

  private class PlayerEntry {

    Player player;
    Set<SkinnableEntity> toRemove = new HashSet<>(25);

    PlayerEntry(Player player) {
      this.player = player;
    }
  }
}
