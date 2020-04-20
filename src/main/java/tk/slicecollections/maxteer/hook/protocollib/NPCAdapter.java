package tk.slicecollections.maxteer.hook.protocollib;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import org.bukkit.entity.Player;
import tk.slicecollections.maxteer.libraries.npclib.NPCLibrary;
import tk.slicecollections.maxteer.libraries.npclib.api.npc.NPC;

import java.util.ArrayList;
import java.util.List;

public class NPCAdapter extends PacketAdapter {

  public NPCAdapter() {
    super(params().plugin(NPCLibrary.getPlugin()).types(PacketType.Play.Server.ENTITY_STATUS, PacketType.Play.Server.NAMED_ENTITY_SPAWN, PacketType.Play.Server.PLAYER_INFO));
  }

  @Override
  public void onPacketSending(PacketEvent evt) {
    PacketContainer packet = evt.getPacket();

    Player player = evt.getPlayer();
    if (packet.getType() == PacketType.Play.Server.PLAYER_INFO) {
      List<PlayerInfoData> toSend = new ArrayList<>();
      boolean needsClone = false;
      for (PlayerInfoData data : packet.getPlayerInfoDataLists().read(0)) {
        NPC npc = NPCLibrary.findNPC(data.getProfile().getUUID());
        if (npc != null) {
          if (npc.data().get(NPC.COPY_PLAYER_SKIN, false)) {
            needsClone = true;
            data.getProfile().getProperties().clear();
            WrappedGameProfile profile = WrappedGameProfile.fromPlayer(player);
            profile.getProperties().get("textures").stream().findFirst().ifPresent(prop -> data.getProfile().getProperties().put("textures", prop));
          }
        }

        toSend.add(data);
      }

      if (!needsClone) {
        toSend.clear();
        return;
      }

      PacketContainer clone = new PacketContainer(PacketType.Play.Server.PLAYER_INFO);
      clone.getPlayerInfoAction().write(0, packet.getPlayerInfoAction().read(0));
      clone.getPlayerInfoDataLists().write(0, toSend);
      evt.setPacket(clone);
    }
  }

  @Override
  public void onPacketReceiving(PacketEvent evt) {}
}
