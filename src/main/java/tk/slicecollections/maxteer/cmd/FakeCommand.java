package tk.slicecollections.maxteer.cmd;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tk.slicecollections.maxteer.player.fake.FakeManager;
import tk.slicecollections.maxteer.player.role.Role;
import tk.slicecollections.maxteer.utils.Validator;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Maxter
 */
public class FakeCommand extends Commands {

  public FakeCommand() {
    super("fake", "faker", "fakel");
  }

  @Override
  public void perform(CommandSender sender, String label, String[] args) {
    if (!(sender instanceof Player)) {
      sender.sendMessage("§cApenas jogadores podem utilizar este comando.");
      return;
    }

    Player player = (Player) sender;
    if (!player.hasPermission("mcore.cmd.fake") || (label.equalsIgnoreCase("fakel") && !player.hasPermission("mcore.cmd.fakelist"))) {
      player.sendMessage("§cVocê não possui permissão para utilizar este comando.");
      return;
    }

    if (label.equalsIgnoreCase("fake")) {
      String fakeName = args.length > 0 ? args[0] : null;
      if (args.length == 0) {
        List<String> list = FakeManager.getRandomNicks().stream().filter(FakeManager::isUsable).collect(Collectors.toList());
        Collections.shuffle(list);
        fakeName = list.stream().findFirst().orElse(null);
        if (fakeName == null) {
          player.sendMessage(" \n §cNenhum nickname aleatório está disponível no momento.\n §cVocê pode utilizar um nome diferente através do comando /fake [nome]\n ");
          return;
        }
      }

      if (!FakeManager.isUsable(fakeName)) {
        player.sendMessage("§cEste nickname falso não está disponível para uso.");
        return;
      }

      if (fakeName.length() > 16 || fakeName.length() < 4) {
        player.sendMessage("§cO nickname falso precisa conter de 4 a 16 caracteres.");
        return;
      }

      if (!Validator.isValidUsername(fakeName)) {
        player.sendMessage("§cO nickname falso não pode conter caracteres especiais.");
        return;
      }

      FakeManager.applyFake(player, fakeName);
    } else if (label.equalsIgnoreCase("faker")) {
      if (!FakeManager.isFake(player.getName())) {
        player.sendMessage("§cVocê não está utilizando um nickname falso.");
        return;
      }

      FakeManager.removeFake(player);
    } else {
      List<String> nicked = FakeManager.listNicked();
      StringBuilder sb = new StringBuilder();
      for (int index = 0; index < nicked.size(); index++) {
        sb.append(Role.getColored(nicked.get(index), false)).append(" §fé na verdade ").append(Role.getColored(nicked.get(index), true)).append(index + 1 == nicked.size() ? "" : "\n");
      }

      nicked.clear();
      if (sb.length() == 0) {
        sb.append("§cNão há nenhum usuário utilizando um nickname falso.");
      }

      player.sendMessage(" \n§eLista de nicknames falsos:\n \n" + sb.toString() + "\n ");
    }
  }
}
