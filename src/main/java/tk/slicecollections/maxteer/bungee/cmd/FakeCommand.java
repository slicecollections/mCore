package tk.slicecollections.maxteer.bungee.cmd;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import tk.slicecollections.maxteer.bungee.Bungee;
import tk.slicecollections.maxteer.utils.Validator;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Maxter
 */
public class FakeCommand extends Commands {

  public FakeCommand() {
    super("fake");
  }

  @Override
  public void perform(CommandSender sender, String[] args) {
    if (!(sender instanceof ProxiedPlayer)) {
      sender.sendMessage(TextComponent.fromLegacyText("§cApenas jogadores podem utilizar este comando."));
      return;
    }

    ProxiedPlayer player = (ProxiedPlayer) sender;
    if (!player.hasPermission("mcore.cmd.fake")) {
      player.sendMessage(TextComponent.fromLegacyText("§cVocê não possui permissão para utilizar este comando."));
      return;
    }

    String fakeName = args.length > 0 ? args[0] : null;
    if (args.length == 0) {
      List<String> list = Bungee.getRandomNicks().stream().filter(Bungee::isUsable).collect(Collectors.toList());
      Collections.shuffle(list);
      fakeName = list.stream().findFirst().orElse(null);
      if (fakeName == null) {
        player.sendMessage(TextComponent.fromLegacyText(" \n §cNenhum nickname aleatório está disponível no momento.\n §cVocê pode utilizar um nome diferente através do comando /fake [nome]\n "));
        return;
      }
    }

    if (!Bungee.isUsable(fakeName)) {
      player.sendMessage(TextComponent.fromLegacyText("§cEste nickname falso não está disponível para uso."));
      return;
    }

    if (fakeName.length() > 16 || fakeName.length() < 4) {
      player.sendMessage(TextComponent.fromLegacyText("§cO nickname falso precisa conter de 4 a 16 caracteres."));
      return;
    }

    if (!Validator.isValidUsername(fakeName)) {
      player.sendMessage(TextComponent.fromLegacyText("§cO nickname falso não pode conter caracteres especiais."));
      return;
    }

    Bungee.applyFake(player, fakeName);
  }
}
