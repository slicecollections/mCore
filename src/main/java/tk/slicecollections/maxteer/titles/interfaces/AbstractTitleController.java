package tk.slicecollections.maxteer.titles.interfaces;

import org.bukkit.entity.Player;

/**
 * @author Maxter
 */
public abstract class AbstractTitleController {

  protected Player owner;

  public AbstractTitleController(Player owner) {
    this.owner = owner;
  }

  public Player getOwner() {
    return this.owner;
  }

  public abstract void enable();

  public abstract void disable();

  protected abstract void showToPlayer(Player player);

  protected abstract void hideToPlayer(Player player);
}
