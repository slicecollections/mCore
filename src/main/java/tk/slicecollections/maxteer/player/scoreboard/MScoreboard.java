package tk.slicecollections.maxteer.player.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import tk.slicecollections.maxteer.player.scoreboard.scroller.ScoreboardScroller;
import tk.slicecollections.maxteer.utils.StringUtils;

/**
 * @author Maxter
 */
public abstract class MScoreboard {

  private Player player;
  private Objective objective;
  private Scoreboard scoreboard;
  private ScoreboardScroller scroller;

  private String display;
  private boolean health, healthTab;

  private VirtualTeam[] teams = new VirtualTeam[15];

  public MScoreboard() {}

  public void scroll() {
    if (this.scroller != null) {
      display(this.scroller.next());
    }
  }

  public void update() {}

  public void updateHealth() {
    if ((this.healthTab || this.health) && this.scoreboard != null) {
      for (Player player : Bukkit.getServer().getOnlinePlayers()) {
        int level = (int) player.getHealth();
        if (this.healthTab) {
          Objective objective = this.scoreboard.getObjective("healthPL");
          if (objective != null) {
            objective.getScore(player.getName()).setScore(level);
          }
        }

        if (this.health) {
          Objective objective = this.scoreboard.getObjective("healthBN");
          if (objective != null && objective.getScore(player.getName()).getScore() == 0) {
            objective.getScore(player.getName()).setScore(level);
          }
        }
      }
    }
  }

  public MScoreboard add(int line) {
    return add(line, "");
  }

  public MScoreboard add(int line, String name) {
    if (line > 15 || line < 1 || this.teams == null) {
      return this;
    }

    VirtualTeam team = getOrCreate(line);
    team.setValue(name);
    if (this.scoreboard != null) {
      team.update();
    }
    return this;
  }

  public MScoreboard remove(int line) {
    if (line > 15 || line < 1 || this.teams == null) {
      return this;
    }

    VirtualTeam team = this.teams[line - 1];
    if (team != null) {
      team.destroy();
      this.teams[line - 1] = null;
    }

    return this;
  }

  public MScoreboard to(Player player) {
    Player lastPlayer = this.player;
    this.player = player;
    if (this.scoreboard != null) {
      if (lastPlayer != null) {
        lastPlayer.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
      }

      player.setScoreboard(this.scoreboard);
    }

    return this;
  }

  public MScoreboard display(String display) {
    this.display = StringUtils.translateAlternateColorCodes('&', display);
    if (this.objective != null) {
      this.objective.setDisplayName(this.display.substring(0, Math.min(this.display.length(), 32)));
    }

    return this;
  }

  public MScoreboard scroller(ScoreboardScroller ss) {
    this.scroller = ss;
    return this;
  }

  public MScoreboard health() {
    this.health = !health;
    if (this.scoreboard != null) {
      if (!this.health) {
        this.scoreboard.getObjective("healthBN").unregister();
      } else {
        Objective health = this.scoreboard.registerNewObjective("healthBN", "health");
        health.setDisplayName("§c❤");
        health.setDisplaySlot(DisplaySlot.BELOW_NAME);
      }
    }

    return this;
  }

  public MScoreboard healthTab() {
    this.healthTab = !healthTab;
    if (this.scoreboard != null) {
      if (!this.healthTab) {
        this.scoreboard.getObjective("healthPL").unregister();
      } else {
        Objective health = this.scoreboard.registerNewObjective("healthPL", "dummy");
        health.setDisplaySlot(DisplaySlot.PLAYER_LIST);
      }
    }

    return this;
  }

  public MScoreboard build() {
    this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
    this.objective = scoreboard.registerNewObjective(getObjectiveName(), "dummy");
    this.objective.setDisplayName(this.display == null ? "" : this.display.substring(0, Math.min(this.display.length(), 32)));
    this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);

    if (this.player != null) {
      this.player.setScoreboard(this.scoreboard);
    }

    if (this.health) {
      Objective health = this.scoreboard.registerNewObjective("healthBN", "health");
      health.setDisplayName("§c❤");
      health.setDisplaySlot(DisplaySlot.BELOW_NAME);
    }

    if (this.healthTab) {
      Objective health = this.scoreboard.registerNewObjective("healthPL", "dummy");
      health.setDisplaySlot(DisplaySlot.PLAYER_LIST);
    }

    for (VirtualTeam team : this.teams) {
      if (team != null) {
        team.update();
      }
    }

    return this;
  }

  public void destroy() {
    this.objective.unregister();
    this.objective = null;
    if (this.health) {
      this.scoreboard.getObjective("healthBN").unregister();
    }
    if (this.healthTab) {
      this.scoreboard.getObjective("healthPL").unregister();
    }
    this.scoreboard = null;
    this.teams = null;
    this.player = null;
    this.display = null;
  }

  public VirtualTeam getTeam(int line) {
    if (line > 15 || line < 1) {
      return null;
    }

    return teams[line - 1];
  }

  public VirtualTeam getOrCreate(int line) {
    if (line > 15 || line < 1) {
      return null;
    }

    if (this.teams[line - 1] == null) {
      this.teams[line - 1] = new VirtualTeam(this, "score[" + line + "]", line);
    }

    return this.teams[line - 1];
  }

  public String getObjectiveName() {
    return "mScoreboard";
  }

  public Scoreboard getScoreboard() {
    return this.scoreboard;
  }

  public Objective getObjective() {
    return this.objective;
  }
}
