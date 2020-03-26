package tk.slicecollections.maxteer.player.scoreboard;

import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Team;
import tk.slicecollections.maxteer.utils.StringUtils;

/**
 * @author Maxter
 */
public class VirtualTeam {

  private MScoreboard instance;

  private String name;
  private String prefix;
  private String entry;
  private String suffix;

  private int line;

  protected VirtualTeam(MScoreboard instance, String team, int line) {
    this.name = team;
    this.line = line;
    this.instance = instance;
  }

  public void destroy() {
    if (this.instance.getScoreboard() != null) {
      this.instance.getScoreboard().resetScores(entry);
      Team team = instance.getScoreboard().getTeam(name);
      if (team != null) {
        team.unregister();
      }
    }

    this.instance = null;
    this.name = null;
    this.prefix = null;
    this.entry = null;
    this.suffix = null;
    this.line = -1;
  }

  public void update() {
    Team team = this.instance.getScoreboard().getTeam(name);
    if (team == null) {
      team = this.instance.getScoreboard().registerNewTeam(name);
    }

    team.setPrefix(this.prefix);
    if (!team.hasEntry(this.entry)) {
      team.addEntry(this.entry);
    }

    team.setSuffix(this.suffix);
    this.instance.getObjective().getScore(this.entry).setScore(this.line);
  }

  public void setValue(String text) {
    if (text.length() > 32) {
      text = text.substring(0, 29) + "...";
    }

    text = StringUtils.translateAlternateColorCodes('&', text);

    this.entry = ChatColor.values()[this.line - 1].toString() + "§r";
    this.prefix = text.substring(0, Math.min(text.length(), 16));
    if (this.prefix.endsWith("§") && this.prefix.length() == 16) {
      this.prefix = this.prefix.substring(0, this.prefix.length() - 1);
      text = text.substring(this.prefix.length());
    } else {
      text = text.substring(Math.min(text.length(), prefix.length()));
    }

    this.suffix = StringUtils.getLastColor(this.prefix) + text;
    this.suffix = this.suffix.substring(0, Math.min(16, this.suffix.length()));
    if (this.suffix.endsWith("§")) {
      this.suffix = this.suffix.substring(0, this.suffix.length() - 1);
    }
  }
}
