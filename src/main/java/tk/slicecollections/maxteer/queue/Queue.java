package tk.slicecollections.maxteer.queue;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import tk.slicecollections.maxteer.Core;
import tk.slicecollections.maxteer.nms.NMS;
import tk.slicecollections.maxteer.player.Profile;

import java.util.ArrayList;
import java.util.List;

public class Queue {

  private BukkitTask task;
  private List<QueuePlayer> players;

  public Queue() {
    this.players = new ArrayList<>();
  }

  public void queue(Player player, Profile profile, String server) {
    this.players.add(new QueuePlayer(player, profile, server));
    if (this.task == null) {
      this.task = new BukkitRunnable() {
        private boolean send, saving;
        private QueuePlayer current;

        @Override
        public void run() {
          if (this.current == null && !players.isEmpty()) {
            this.current = players.get(0);
            this.send = false;
            this.saving = false;
          }

          int id = 1;
          List<QueuePlayer> toRemove = new ArrayList<>();
          for (QueuePlayer qp : players) {
            if (!qp.player.isOnline()) {
              toRemove.remove(qp);
              qp.destroy();
              continue;
            }

            NMS.sendActionBar(qp.player, "§aVocê está na Fila para conectar-se a §8" + qp.server + " §7(Posição #" + id + ")");
            id++;
          }

          players.removeAll(toRemove);
          toRemove.clear();

          if (this.current != null) {
            if (!this.current.player.isOnline()) {
              players.remove(this.current);
              this.current.destroy();
              this.current = null;
              return;
            }

            if (this.send) {
              this.current.player.closeInventory();
              NMS.sendActionBar(this.current.player, "");
              this.current.player.sendMessage("§aConectando...");
              ByteArrayDataOutput out = ByteStreams.newDataOutput();
              out.writeUTF("Connect");
              out.writeUTF(this.current.server);
              this.current.player.sendPluginMessage(Core.getInstance(), "BungeeCord", out.toByteArray());
              players.remove(this.current);
              this.current.destroy();
              this.current = null;
              return;
            }

            if (!this.saving) {
              this.saving = true;
              this.current.profile.saveSync();
              this.send = true;
            }
          }
        }
      }.runTaskTimerAsynchronously(Core.getInstance(), 0, 20);
    }
  }

  public QueuePlayer getQueuePlayer(Player player) {
    return this.players.stream().filter(qp -> qp.player.equals(player)).findFirst().orElse(null);
  }

  public static final Queue VIP = new Queue();
  public static final Queue MEMBER = new Queue();
}
