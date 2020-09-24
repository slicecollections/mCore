package tk.slicecollections.maxteer.cmd;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tk.slicecollections.maxteer.Manager;
import tk.slicecollections.maxteer.bukkit.BukkitParty;
import tk.slicecollections.maxteer.bukkit.BukkitPartyManager;
import tk.slicecollections.maxteer.player.role.Role;
import tk.slicecollections.maxteer.utils.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

import static tk.slicecollections.maxteer.party.PartyRole.LEADER;

/**
 * @author Maxter
 */
public class PartyCommand extends Commands {

  public PartyCommand() {
    super("party", "p");
  }

  @Override
  public void perform(CommandSender sender, String label, String[] args) {
    if (!(sender instanceof Player)) {
      sender.sendMessage("§cApenas jogadores podem utilizar este comando.");
      return;
    }

    Player player = (Player) sender;
    if (label.equalsIgnoreCase("p")) {
      if (args.length == 0) {
        player.sendMessage("§cUtilize /p [mensagem] para conversar com a sua Party.");
        return;
      }

      BukkitParty party = BukkitPartyManager.getMemberParty(player.getName());
      if (party == null) {
        player.sendMessage("§cVocê não pertence a uma Party.");
        return;
      }

      party.broadcast("§d[Party] " + Role.getPrefixed(player.getName()) + "§f: " + StringUtils.join(args, " "));
    } else {
      if (args.length == 0) {
        player.sendMessage(
          " \n§3/p [mensagem] §f- §7Comunicar-se com os membros.\n§3/party abrir §f- §7Tornar a party pública.\n§3/party fechar §f- §7Tornar a party privada.\n§3/party entrar [jogador] §f- §7Entrar em uma party pública.\n§3/party aceitar [jogador] §f- §7Aceitar uma solicitação.\n§3/party ajuda §f- §7Mostrar essa mensagem de ajuda.\n§3/party convidar [jogador] §f- §7Convidar um jogador.\n§3/party deletar §f- §7Deletar a party.\n§3/party expulsar [jogador] §f- §7Expulsar um membro.\n§3/party info §f- §7Informações da sua Party.\n§3/party negar [jogador] §f- §7Negar uma solicitação.\n§3/party sair §f- §7Sair da Party.\n§3/party transferir [jogador] §f- §7Transferir a Party para outro membro.\n ");
        return;
      }

      String action = args[0];
      if (action.equalsIgnoreCase("abrir")) {
        BukkitParty party = BukkitPartyManager.getMemberParty(player.getName());
        if (party == null) {
          player.sendMessage("§cVocê não pertence a uma Party.");
          return;
        }

        if (!party.isLeader(player.getName())) {
          player.sendMessage("§cVocê não é o Líder da Party.");
          return;
        }

        if (party.isOpen()) {
          player.sendMessage("§cSua party já é pública.");
          return;
        }

        party.setIsOpen(true);
        player.sendMessage("§aVocê abriu a party para qualquer jogador.");
      } else if (action.equalsIgnoreCase("fechar")) {
        BukkitParty party = BukkitPartyManager.getMemberParty(player.getName());
        if (party == null) {
          player.sendMessage("§cVocê não pertence a uma Party.");
          return;
        }

        if (!party.isLeader(player.getName())) {
          player.sendMessage("§cVocê não é o Líder da Party.");
          return;
        }

        if (!party.isOpen()) {
          player.sendMessage("§cSua party já é privada.");
          return;
        }

        party.setIsOpen(false);
        player.sendMessage("§cVocê fechou a party para apenas convidados.");
      } else if (action.equalsIgnoreCase("entrar")) {
        if (args.length == 1) {
          player.sendMessage("§cUtilize /party entrar [jogador]");
          return;
        }

        String target = args[1];
        if (target.equalsIgnoreCase(player.getName())) {
          player.sendMessage("§cVocê não pode entrar na party de você mesmo.");
          return;
        }

        BukkitParty party = BukkitPartyManager.getMemberParty(player.getName());
        if (party != null) {
          player.sendMessage("§cVocê já pertence a uma Party.");
          return;
        }

        party = BukkitPartyManager.getLeaderParty(target);
        if (party == null) {
          player.sendMessage("§c" + Manager.getCurrent(target) + " não é um Líder de Party.");
          return;
        }

        target = party.getName(target);
        if (!party.isOpen()) {
          player.sendMessage("§cA Party de " + Manager.getCurrent(target) + " está fechada apenas para convidados.");
          return;
        }

        if (!party.canJoin()) {
          player.sendMessage("§cA Party de " + Manager.getCurrent(target) + " está lotada.");
          return;
        }

        party.join(player.getName());
        player.sendMessage(" \n§aVocê entrou na Party de " + Role.getPrefixed(target) + "§a!\n ");
      } else if (action.equalsIgnoreCase("aceitar")) {
        if (args.length == 1) {
          player.sendMessage("§cUtilize /party aceitar [jogador]");
          return;
        }

        String target = args[1];
        if (target.equalsIgnoreCase(player.getName())) {
          player.sendMessage("§cVocê não pode aceitar convites de você mesmo.");
          return;
        }

        BukkitParty party = BukkitPartyManager.getMemberParty(player.getName());
        if (party != null) {
          player.sendMessage("§cVocê já pertence a uma Party.");
          return;
        }

        party = BukkitPartyManager.getLeaderParty(target);
        if (party == null) {
          player.sendMessage("§c" + Manager.getCurrent(target) + " não é um Líder de Party.");
          return;
        }

        target = party.getName(target);
        if (!party.isInvited(player.getName())) {
          player.sendMessage("§c" + Manager.getCurrent(target) + " não convidou você para Party.");
          return;
        }

        if (!party.canJoin()) {
          player.sendMessage("§cA Party de " + Manager.getCurrent(target) + " está lotada.");
          return;
        }

        party.join(player.getName());
        player.sendMessage(" \n§aVocê entrou na Party de " + Role.getPrefixed(target) + "§a!\n ");
      } else if (action.equalsIgnoreCase("ajuda")) {
        player.sendMessage(
          " \n§3/p [mensagem] §f- §7Comunicar-se com os membros.\n§3/party abrir §f- §7Tornar a party pública.\n§3/party fechar §f- §7Tornar a party privada.\n§3/party entrar [jogador] §f- §7Entrar em uma party pública.\n§3/party aceitar [jogador] §f- §7Aceitar uma solicitação.\n§3/party ajuda §f- §7Mostrar essa mensagem de ajuda.\n§3/party convidar [jogador] §f- §7Convidar um jogador.\n§3/party deletar §f- §7Deletar a party.\n§3/party expulsar [jogador] §f- §7Expulsar um membro.\n§3/party info §f- §7Informações da sua Party.\n§3/party negar [jogador] §f- §7Negar uma solicitação.\n§3/party sair §f- §7Sair da Party.\n§3/party transferir [jogador] §f- §7Transferir a Party para outro membro.\n ");
      } else if (action.equalsIgnoreCase("deletar")) {
        BukkitParty party = BukkitPartyManager.getMemberParty(player.getName());
        if (party == null) {
          player.sendMessage("§cVocê não pertence a uma Party.");
          return;
        }

        if (!party.isLeader(player.getName())) {
          player.sendMessage("§cVocê não é o Líder da Party.");
          return;
        }

        party.broadcast(" \n" + Role.getPrefixed(player.getName()) + " §adeletou a Party!\n ", true);
        party.delete();
        player.sendMessage("§aVocê deletou a Party.");
      } else if (action.equalsIgnoreCase("expulsar")) {
        if (args.length == 1) {
          player.sendMessage("§cUtilize /party expulsar [jogador]");
          return;
        }

        BukkitParty party = BukkitPartyManager.getLeaderParty(player.getName());
        if (party == null) {
          player.sendMessage("§cVocê não é um Líder de Party.");
          return;
        }

        String target = args[1];
        if (target.equalsIgnoreCase(player.getName())) {
          player.sendMessage("§cVocê não pode se expulsar.");
          return;
        }

        if (!party.isMember(target)) {
          player.sendMessage("§cEsse jogador não pertence a sua Party.");
          return;
        }

        target = party.getName(target);
        party.kick(target);
        party.broadcast(" \n" + Role.getPrefixed(player.getName()) + " §aexpulsou " + Role.getPrefixed(target) + " §ada Party!\n ");
      } else if (action.equalsIgnoreCase("info")) {
        BukkitParty party = BukkitPartyManager.getMemberParty(player.getName());
        if (party == null) {
          player.sendMessage("§cVocê não pertence a uma Party.");
          return;
        }

        List<String> members =
          party.listMembers().stream().filter(pp -> pp.getRole() != LEADER).map(pp -> (pp.isOnline() ? "§a" : "§c") + pp.getName()).collect(Collectors.toList());
        player.sendMessage(
          " \n§6Líder: " + Role.getPrefixed(party.getLeader()) + "\n§6Pública: " + (party.isOpen() ? "§aSim" : "§cNão") + "\n§6Limite de Membros: §f" + party.listMembers()
            .size() + "/" + party.getSlots() + "\n§6Membros: " + StringUtils.join(members, "§7, ") + "\n ");
      } else if (action.equalsIgnoreCase("negar")) {
        if (args.length == 1) {
          player.sendMessage("§cUtilize /party negar [jogador]");
          return;
        }

        String target = args[1];
        if (target.equalsIgnoreCase(player.getName())) {
          player.sendMessage("§cVocê não pode negar convites de você mesmo.");
          return;
        }

        BukkitParty party = BukkitPartyManager.getMemberParty(player.getName());
        if (party != null) {
          player.sendMessage("§cVocê já pertence a uma Party.");
          return;
        }

        party = BukkitPartyManager.getLeaderParty(target);
        if (party == null) {
          player.sendMessage("§c" + Manager.getCurrent(target) + " não é um Líder de Party.");
          return;
        }

        target = party.getName(target);
        if (!party.isInvited(player.getName())) {
          player.sendMessage("§c" + Manager.getCurrent(target) + " não convidou você para Party.");
          return;
        }

        party.reject(player.getName());
        player.sendMessage(" \n§aVocê negou o convite de Party de " + Role.getPrefixed(target) + "§a!\n ");
      } else if (action.equalsIgnoreCase("sair")) {
        BukkitParty party = BukkitPartyManager.getMemberParty(player.getName());
        if (party == null) {
          player.sendMessage("§cVocê não pertence a uma Party.");
          return;
        }

        party.leave(player.getName());
        player.sendMessage("§aVocê saiu da Party!");
      } else if (action.equalsIgnoreCase("transferir")) {
        if (args.length == 1) {
          player.sendMessage("§cUtilize /party transferir [jogador]");
          return;
        }

        BukkitParty party = BukkitPartyManager.getLeaderParty(player.getName());
        if (party == null) {
          player.sendMessage("§cVocê não é um Líder de Party.");
          return;
        }

        String target = args[1];
        if (target.equalsIgnoreCase(player.getName())) {
          player.sendMessage("§cVocê não pode transferir a Party para você mesmo.");
          return;
        }

        if (!party.isMember(target)) {
          player.sendMessage("§cEsse jogador não pertence a sua Party.");
          return;
        }

        target = party.getName(target);
        party.transfer(target);
        party.broadcast(" \n" + Role.getPrefixed(player.getName()) + " §atransferiu a liderança da Party para " + Role.getPrefixed(target) + "§a!\n ");
      } else {
        if (action.equalsIgnoreCase("convidar")) {
          if (args.length == 1) {
            player.sendMessage("§cUtilize /party convidar [jogador]");
            return;
          }

          action = args[1];
        }

        Player target = Bukkit.getPlayerExact(action);
        if (target == null) {
          player.sendMessage("§cUsuário não encontrado.");
          return;
        }

        action = target.getName();
        if (action.equalsIgnoreCase(player.getName())) {
          player.sendMessage("§cVocê não pode enviar convites para você mesmo.");
          return;
        }

        BukkitParty party = BukkitPartyManager.getMemberParty(player.getName());
        if (party == null) {
          party = BukkitPartyManager.createParty(player);
        }

        if (!party.isLeader(player.getName())) {
          player.sendMessage("§cApenas o Líder da Party pode enviar convites!");
          return;
        }

        if (!party.canJoin()) {
          player.sendMessage("§cA sua Party está lotada.");
          return;
        }

        if (party.isInvited(action)) {
          player.sendMessage("§cVocê já enviou um convite para " + Manager.getCurrent(action) + ".");
          return;
        }

        if (BukkitPartyManager.getMemberParty(action) != null) {
          player.sendMessage("§c" + Manager.getCurrent(action) + " já pertence a uma Party.");
          return;
        }

        party.invite(target);
        player.sendMessage(" \n" + Role.getPrefixed(action) + " §afoi convidado para a Party. Ele tem 60 segundos para aceitar ou negar esta solicitação.\n ");
      }
    }
  }
}
