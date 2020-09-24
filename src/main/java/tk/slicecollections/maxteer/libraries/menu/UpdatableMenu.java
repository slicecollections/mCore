package tk.slicecollections.maxteer.libraries.menu;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import tk.slicecollections.maxteer.plugin.MPlugin;

/**
 * @author Maxter
 */
public abstract class UpdatableMenu extends Menu implements Listener {

  private BukkitTask task;

  public UpdatableMenu(String name) {
    this(name, 3);
  }

  public UpdatableMenu(String name, int rows) {
    super(name, rows);
  }

  public void register(MPlugin plugin, long updateEveryTicks) {
    Bukkit.getPluginManager().registerEvents(this, plugin);
    this.task = new BukkitRunnable() {
      @Override
      public void run() {
        update();
      }
    }.runTaskTimer(plugin, 0, updateEveryTicks);
  }

  public void cancel() {
    this.task.cancel();
    this.task = null;
  }

  public abstract void update();
}
