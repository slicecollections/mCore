package tk.slicecollections.maxteer.bukkit;

import com.google.common.collect.ImmutableList;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import tk.slicecollections.maxteer.Core;
import tk.slicecollections.maxteer.player.fake.FakeManager;

import java.util.ArrayList;
import java.util.List;

public class BukkitPartyManager {

  private static BukkitTask CLEAN_PARTIES;
  private static final List<BukkitParty> BUKKIT_PARTIES = new ArrayList<>();

  public static BukkitParty createParty(Player leader) {
    return createParty(leader.getName(), BukkitPartySizer.getPartySize(leader));
  }

  public static BukkitParty createParty(String leader, int size) {
    BukkitParty bp = new BukkitParty(leader, size);
    BUKKIT_PARTIES.add(bp);
    if (CLEAN_PARTIES == null && !FakeManager.isBungeeSide()) {
      CLEAN_PARTIES = new BukkitRunnable() {
        @Override
        public void run() {
          ImmutableList.copyOf(BUKKIT_PARTIES).forEach(BukkitParty::update);
        }
      }.runTaskTimer(Core.getInstance(), 0L, 40L);
    }

    return bp;
  }

  public static BukkitParty getLeaderParty(String player) {
    return BUKKIT_PARTIES.stream().filter(bp -> bp.isLeader(player)).findAny().orElse(null);
  }

  public static BukkitParty getMemberParty(String player) {
    return BUKKIT_PARTIES.stream().filter(bp -> bp.isMember(player)).findAny().orElse(null);
  }

  public static List<BukkitParty> listParties() {
    return BUKKIT_PARTIES;
  }
}
