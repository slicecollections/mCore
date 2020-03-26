package tk.slicecollections.maxteer.player.scoreboard.scroller;

import java.util.List;

/**
 * @author Maxter
 */
public class ScoreboardScroller {

  private int index;
  private List<String> frames;

  public ScoreboardScroller(List<String> frames) {
    this.index = -1;
    this.frames = frames;
  }

  public String next() {
    if (++index >= frames.size()) {
      this.index = 0;
    }

    return frames.get(index);
  }
}
