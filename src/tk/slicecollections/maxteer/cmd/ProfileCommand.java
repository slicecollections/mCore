package tk.slicecollections.maxteer.cmd;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tk.slicecollections.maxteer.menus.MenuProfile;
import tk.slicecollections.maxteer.player.Profile;

public class ProfileCommand extends Commands {
    
  public ProfileCommand() {
    super("perfil");
  }

  @Override
  public void perform(CommandSender sender, String label, String[] args) {
    if (sender instanceof Player) {
      Player player = (Player) sender;

      Profile profile = Profile.getProfile(player.getName());
      if (profile != null) {
        new MenuProfile(profile);
      }
    }
  }
}
