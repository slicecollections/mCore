package tk.slicecollections.maxteer.plugin;

import java.io.File;
import org.bukkit.plugin.PluginLogger;
import org.bukkit.plugin.java.JavaPlugin;
import tk.slicecollections.maxteer.plugin.config.FileUtils;
import tk.slicecollections.maxteer.plugin.config.MConfig;
import tk.slicecollections.maxteer.plugin.config.MWriter;
import tk.slicecollections.maxteer.plugin.logger.MLogger;
import tk.slicecollections.maxteer.reflection.Accessors;
import tk.slicecollections.maxteer.reflection.acessors.FieldAccessor;

/**
 * @author Maxter
 */
public abstract class MPlugin extends JavaPlugin {

  private static final FieldAccessor<PluginLogger> LOGGER_ACCESSOR = Accessors.getField(JavaPlugin.class, "logger", PluginLogger.class);
  private final FileUtils fileUtils;

  public MPlugin() {
    this.fileUtils = new FileUtils(this);
    LOGGER_ACCESSOR.set(this, new MLogger(this));
    
    this.start();
  }

  public abstract void start();

  public abstract void load();

  public abstract void enable();

  public abstract void disable();

  @Override
  public void onLoad() {
    this.load();
  }

  @Override
  public void onEnable() {
    this.enable();
  }

  @Override
  public void onDisable() {
    this.disable();
  }

  public MConfig getConfig(String name) {
    return this.getConfig("", name);
  }

  public MConfig getConfig(String path, String name) {
    return MConfig.getConfig(this, "plugins/" + this.getName() + "/" + path, name);
  }
  
  public MWriter getWriter(File file) {
    return this.getWriter(file, "");
  }
  
  public MWriter getWriter(File file, String header) {
    return new MWriter((MLogger) this.getLogger(), file, header);
  }

  public FileUtils getFileUtils() {
    return this.fileUtils;
  }
}
