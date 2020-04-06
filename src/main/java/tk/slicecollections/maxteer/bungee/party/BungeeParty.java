package tk.slicecollections.maxteer.bungee.party;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import tk.slicecollections.maxteer.Manager;
import tk.slicecollections.maxteer.party.Party;
import tk.slicecollections.maxteer.party.PartyPlayer;
import tk.slicecollections.maxteer.player.role.Role;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import static tk.slicecollections.maxteer.party.PartyRole.LEADER;

@SuppressWarnings("unchecked")
public class BungeeParty extends Party {

  public BungeeParty(String leader, int slots) {
    super(leader, slots);
    this.sendData();
  }

  @Override
  public void delete() {
    this.sendData("delete", "true");
    BungeePartyManager.listParties().remove(this);
    this.destroy();
  }

  @Override
  public void transfer(String name) {
    PartyPlayer newLeader = this.getPlayer(name);
    this.sendData("newLeader", newLeader.getName());
    this.leader.setRole(newLeader.getRole());
    newLeader.setRole(LEADER);
    this.leader = newLeader;
  }

  @Override
  public void join(String member) {
    super.join(member);
    this.summonMember(member);
    this.sendData();
  }

  @Override
  public void leave(String member) {
    String leader = this.getLeader();
    this.members.removeIf(pp -> pp.getName().equalsIgnoreCase(member));
    this.sendData("remove", member);
    if (this.members.isEmpty()) {
      this.delete();
      return;
    }

    String prefixed = Role.getPrefixed(member);
    if (leader.equals(member)) {
      this.sendData("newLeader", this.members.get(0).getName());
      this.leader = this.members.get(0);
      this.leader.setRole(LEADER);
      this.broadcast(" \n" + prefixed + " §ase tornou o novo Líder da Party!\n ");
    }
    this.broadcast(" \n" + prefixed + " §asaiu da Party!\n ");
  }

  @Override
  public void kick(String member) {
    super.kick(member);
    this.sendData("remove", member);
  }

  public void sendData(ServerInfo serverInfo) {
    this.sendData(null, null, Collections.singleton(serverInfo));
  }

  public void summonMember(String member) {
    this.summonMembers(null, Collections.singleton(member));
  }

  public void summonMembers(ServerInfo serverInfo) {
    this.summonMembers(serverInfo, this.members.stream().map(PartyPlayer::getName).collect(Collectors.toList()));
  }

  private void summonMembers(ServerInfo serverInfo, Collection<String> members) {
    if (serverInfo == null) {
      ProxiedPlayer leader = (ProxiedPlayer) Manager.getPlayer(this.getLeader());
      serverInfo = leader != null && leader.getServer() != null ? leader.getServer().getInfo() : null;
    }

    if (serverInfo != null) {
      String leader = Role.getPrefixed(this.getLeader());
      ServerInfo finalServerInfo = serverInfo;
      members.forEach(member -> {
        if (isLeader(member)) {
          return;
        }

        ProxiedPlayer player = (ProxiedPlayer) Manager.getPlayer(member);
        if (player != null && (player.getServer() == null || !player.getServer().getInfo().getName().equals(finalServerInfo.getName()))) {
          player.connect(finalServerInfo);
          player.sendMessage(TextComponent.fromLegacyText(" \n" + leader + " §apuxou você para o servidor.\n "));
        }
      });
    }
  }

  private void sendData() {
    this.sendData(null, null);
  }

  private void sendData(String extraKey, String extraValue) {
    this.sendData(extraKey, extraValue, ProxyServer.getInstance().getServers().values());
  }

  private void sendData(String extraKey, String extraValue, Collection<ServerInfo> serverInfos) {
    JSONObject changes = new JSONObject();
    changes.put("leader", this.leader.getName());
    if (extraKey != null) {
      changes.put(extraKey, extraValue);
    }
    JSONArray members = new JSONArray();
    listMembers().forEach(member -> members.add(member.getName()));
    changes.put("members", members);

    ByteArrayDataOutput out = ByteStreams.newDataOutput();
    out.writeUTF("PARTY");
    out.writeUTF(changes.toString());
    serverInfos.forEach(info -> info.sendData("mCore", out.toByteArray()));
  }
}
