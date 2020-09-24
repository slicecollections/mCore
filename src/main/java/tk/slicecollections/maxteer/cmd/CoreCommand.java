package tk.slicecollections.maxteer.cmd;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tk.slicecollections.maxteer.Core;
import tk.slicecollections.maxteer.database.Database;
import tk.slicecollections.maxteer.utils.SliceUpdater;

/**
 * @author Maxter
 */
public class CoreCommand extends Commands {

  public CoreCommand() {
    super("mcore", "mc");
  }

  @Override
  public void perform(CommandSender sender, String label, String[] args) {
    if (sender instanceof Player) {
      Player player = (Player) sender;
      if (!player.hasPermission("mcore.admin")) {
        player.sendMessage("§6" + Core.getInstance().getName() + " §7[" + Core.getInstance().getDescription().getVersion() + "] §f- §7Criado por §5Maxteer§7.");
        return;
      }

      if (args.length == 0) {
        player.sendMessage(" \n§3/mc atualizar §f- §7Atualizar o mCore.\n§3/mc converter §f- §7Converter seu Banco de Dados.\n ");
        return;
      }

      String action = args[0];
      if (action.equalsIgnoreCase("atualizar")) {
        if (SliceUpdater.UPDATER != null) {
          if (!SliceUpdater.UPDATER.canDownload) {
            player.sendMessage(
              " \n§6§l[MCORE]\n \n§aA atualização já está baixada, ela será aplicada na próxima reinicialização do servidor. Caso deseje aplicá-la agora, utilize o comando /stop.\n ");
            return;
          }
          SliceUpdater.UPDATER.canDownload = false;
          SliceUpdater.UPDATER.downloadUpdate(player);
        } else {
          player.sendMessage("§aO plugin já se encontra em sua última versão.");
        }
      } else if (action.equalsIgnoreCase("converter")) {
        player.sendMessage("§fBanco de Dados: " + Database.getInstance().getClass().getSimpleName().replace("Database", ""));
        Database.getInstance().convertDatabase(player);
      } else {
        player.sendMessage(" \n§3/mc atualizar §f- §7Atualizar o mCore.\n§3/mc converter §f- §7Converter seu Banco de Dados.\n ");
      }
    } else {
      sender.sendMessage("§cApenas jogadores podem utilizar este comando.");
    }
  }
}
