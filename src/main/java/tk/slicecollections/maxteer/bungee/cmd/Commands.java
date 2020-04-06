package tk.slicecollections.maxteer.bungee.cmd;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Command;
import tk.slicecollections.maxteer.bungee.Bungee;

/**
 * @author Maxter
 */
public abstract class Commands extends Command {

  public Commands(String name, String... aliases) {
    super(name, null, aliases);
    ProxyServer.getInstance().getPluginManager().registerCommand(Bungee.getInstance(), this);
  }

  public abstract void perform(CommandSender sender, String[] args);

  @Override
  public void execute(CommandSender sender, String[] args) {
    this.perform(sender, args);
  }

  public static void setupCommands() {
    new FakeCommand();
    new FakeResetCommand();
    new FakeListCommand();
    new PartyCommand();
    new PartyChatCommand();
  }
}
