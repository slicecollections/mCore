package tk.slicecollections.maxteer.listeners;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import tk.slicecollections.maxteer.bukkit.BukkitParty;
import tk.slicecollections.maxteer.bukkit.BukkitPartyManager;
import tk.slicecollections.maxteer.nms.NMS;
import tk.slicecollections.maxteer.party.PartyPlayer;
import tk.slicecollections.maxteer.player.fake.FakeManager;

import static tk.slicecollections.maxteer.party.PartyRole.MEMBER;

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
        try {
          JSONObject changes = (JSONObject) new JSONParser().parse(in.readUTF());
          String leader = changes.get("leader").toString();
          boolean delete = changes.containsKey("delete");
          BukkitParty party = BukkitPartyManager.getLeaderParty(leader);
          if (party == null) {
            if (delete) {
              return;
            }
            party = BukkitPartyManager.createParty(leader, 0);
          }

          if (delete) {
            party.delete();
            return;
          }

          if (changes.containsKey("newLeader")) {
            party.transfer(changes.get("newLeader").toString());
          }

          if (changes.containsKey("remove")) {
            party.listMembers().removeIf(pp -> pp.getName().equalsIgnoreCase(changes.get("remove").toString()));
          }

          for (Object object : (JSONArray) changes.get("members")) {
            if (!party.isMember(object.toString())) {
              party.listMembers().add(new PartyPlayer(object.toString(), MEMBER));
            }
          }
        } catch (ParseException ignore) {}
      }
    }
  }
}
