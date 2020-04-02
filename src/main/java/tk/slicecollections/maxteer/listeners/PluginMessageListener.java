package tk.slicecollections.maxteer.listeners;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import tk.slicecollections.maxteer.nms.NMS;
import tk.slicecollections.maxteer.player.fake.FakeManager;

/**
 * @author Maxter
 */
public class PluginMessageListener implements org.bukkit.plugin.messaging.PluginMessageListener {

  @Override
  public void onPluginMessageReceived(String channel, Player receiver, byte[] data) {
    if (channel.equals("mCore")) {
      ByteArrayDataInput in = ByteStreams.newDataInput(data);

      String subChannel = in.readUTF();
      if (subChannel.equals("FAKE")) {
        Player player = Bukkit.getPlayerExact(in.readUTF());
        if (player != null) {
          String name = in.readUTF();
          FakeManager.applyFake(player, name);
          NMS.refreshPlayer(player);
        }
      } else if (subChannel.equals("PARTY")) {

      }
    }
  }
}
