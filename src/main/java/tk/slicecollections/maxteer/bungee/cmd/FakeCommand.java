package tk.slicecollections.maxteer.bungee.cmd;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import tk.slicecollections.maxteer.bungee.Bungee;
import tk.slicecollections.maxteer.player.role.Role;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static tk.slicecollections.maxteer.bungee.Bungee.ALEX;
import static tk.slicecollections.maxteer.bungee.Bungee.STEVE;

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

    if (Bungee.getRandomNicks().stream().noneMatch(Bungee::isUsable)) {
      player.sendMessage(TextComponent.fromLegacyText(" \n §c§lALTERAR NICKNAME\n \n §cNenhum nickname está disponível para uso no momento.\n "));
      return;
    }

    if (args.length == 0) {
      Bungee.sendRole(player, null);
      return;
    }

    String roleName = args[0];
    if (!Bungee.isFakeRole(roleName)) {
      Bungee.sendRole(player, "VILLAGER_NO");
      return;
    }

    if (Role.getRoleByName(roleName) == null) {
      Bungee.sendRole(player, "VILLAGER_NO");
      return;
    }

    if (args.length == 1) {
      Bungee.sendSkin(player, roleName, "ORB_PICKUP");
      return;
    }

    String skin = args[1];
    if (!skin.equalsIgnoreCase("alex") && !skin.equalsIgnoreCase("steve")) {
      Bungee.sendSkin(player, roleName, "VILLAGER_NO");
      return;
    }

    List<String> enabled = Bungee.getRandomNicks().stream().filter(Bungee::isUsable).collect(Collectors.toList());
    String fakeName = enabled.isEmpty() ? null : enabled.get(ThreadLocalRandom.current().nextInt(enabled.size()));
    if (fakeName == null) {
      player.sendMessage(TextComponent.fromLegacyText(" \n §c§lALTERAR NICKNAME\n \n §cNenhum nickname está disponível para uso no momento.\n "));
      return;
    }

    enabled.clear();
    Bungee.applyFake(player, fakeName, roleName, skin.equalsIgnoreCase("steve") ? STEVE : ALEX);
  }
}
