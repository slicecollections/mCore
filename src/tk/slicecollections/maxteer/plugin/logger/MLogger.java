package tk.slicecollections.maxteer.plugin.logger;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginLogger;

public class MLogger extends PluginLogger {

  private Plugin plugin;
  private String prefix;
  private CommandSender sender;

  public MLogger(Plugin plugin) {
    super(plugin);
    this.plugin = plugin;
    this.prefix = "[" + plugin.getName() + "] ";
    this.sender = Bukkit.getConsoleSender();
  }

  public MLogger(MLogger parent, String prefix) {
    super(parent.plugin);
    this.plugin = parent.plugin;
    this.prefix = parent.prefix + prefix;
    this.sender = Bukkit.getConsoleSender();
  }

  public void run(Level level, String method, Runnable runnable) {
    try {
      runnable.run();
    } catch (Exception ex) {
      this.log(level, method.replace("${n}", plugin.getName()).replace("${v}", plugin.getDescription().getVersion()), ex);
    }
  }

  @Override
  public void log(LogRecord logRecord) {
    MLevel level = MLevel.fromName(logRecord.getLevel().getName());
    if (level == null) {
      return;
    }
    
    String message = logRecord.getMessage();
    if (message.equals("Default system encoding may have misread config.yml from plugin jar")) {
      return;
    }
    StringBuilder result = new StringBuilder(this.prefix + message);
    if (logRecord.getThrown() != null) {
      result.append("\n" + logRecord.getThrown().getLocalizedMessage());
      for (StackTraceElement ste : logRecord.getThrown().getStackTrace()) {
        if (ste.toString().contains("tk.slicecollections")) {
          result.append("\n" + ste.toString());
          continue;
        }
      }
    }

    this.sender.sendMessage(level.format(result.toString()));
  }

  public MLogger getModule(String module) {
    return new MLogger(this, module + ": ");
  }

  public static enum MLevel {
      INFO("§a"),
      WARNING("§e"),
      SEVERE("§c");

    private String color;

    MLevel(String color) {
      this.color = color;
    }

    public String format(String message) {
      return this.color + message;
    }

    public static MLevel fromName(String name) {
      for (MLevel level : values()) {
        if (level.name().equalsIgnoreCase(name)) {
          return level;
        }
      }

      return null;
    }
  }
}
