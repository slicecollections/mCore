package tk.slicecollections.maxteer.plugin.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import tk.slicecollections.maxteer.plugin.logger.MLogger;
import tk.slicecollections.maxteer.utils.StringUtils;

public class MWriter {

  private MLogger logger;
  private File file;
  private String header;
  private Map<String, Object> keys = new LinkedHashMap<>();

  public MWriter(MLogger logger, File file) {
    this(logger, file, "");
  }

  public MWriter(MLogger logger, File file, String header) {
    this.logger = logger;
    this.file = file;
    this.header = header;
  }

  public void write() {
    try {
      Writer fw = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
      fw.append(this.toSaveString());
      fw.close();
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  @SuppressWarnings("unchecked")
  public void set(String path, YamlEntry entry) {
    String[] splitter = path.split("\\.");

    Map<String, Object> currentMap = this.keys;
    for (int slot = 0; slot < splitter.length; slot++) {
      String p = splitter[slot];
      if (slot + 1 == splitter.length) {
        currentMap.put(p, entry);
        continue;
      } else {
        if (currentMap.containsKey(p)) {
          currentMap = (Map<String, Object>) currentMap.get(p);
        } else {
          currentMap.put(p, new LinkedHashMap<String, Object>());
          currentMap = (Map<String, Object>) currentMap.get(p);
        }
      }
    }
  }

  public String toSaveString() {
    StringBuilder join = new StringBuilder();
    if (!this.header.isEmpty()) {
      for (String split : this.header.split("\n")) {
        for (String annotation : StringUtils.split(split, 100)) {
          join.append("# " + annotation + "\n");
        }
      }
    }

    for (Entry<String, Object> entry : this.keys.entrySet()) {
      join.append(toSaveString(entry.getKey(), entry.getValue(), 0));
    }

    return join.toString();
  }

  @SuppressWarnings("unchecked")
  private String toSaveString(String key, Object object, int spaces) {
    StringBuilder join = new StringBuilder();
    if (object instanceof YamlEntry) {
      YamlEntry ye = (YamlEntry) object;
      if (!ye.getAnnotation().isEmpty()) {
        for (String split : ye.getAnnotation().split("\n")) {
          for (String annotation : StringUtils.split(split, 100)) {
            join.append(repeat(spaces) + "# " + annotation + "\n");
          }
        }
      }

      object = ye.getValue();
    }
    
    join.append(repeat(spaces) + key + ":");
    if (object instanceof String) {
      join.append(" '" + object.toString().replace("'", "''") + "'\n");
    } else if (object instanceof Integer) {
      join.append(" " + object + "\n");
    } else if (object instanceof Double) {
      join.append(" " + object + "\n");
    } else if (object instanceof Long) {
      join.append(" " + object + "\n");
    } else if (object instanceof Boolean) {
      join.append(" " + object + "\n");
    } else if (object instanceof List) {
      join.append("\n");
      for (Object obj : (List<?>) object) {
        if (obj instanceof Integer) {
          join.append(repeat(spaces) + "- " + obj.toString() + "\n");
        } else {
          join.append(repeat(spaces) + "- '" + obj.toString().replace("'", "''") + "'\n");
        }
      }
    } else if (object instanceof Map) {
      join.append("\n");
      for (Entry<String, Object> entry : ((Map<String, Object>) object).entrySet()) {
        join.append(toSaveString(entry.getKey(), entry.getValue(), spaces + 1));
      }
    } else if (object instanceof InputStream) {
      join.append("\n");
      try {
        BufferedReader reader = new BufferedReader(new InputStreamReader((InputStream) object, "UTF-8"));
        String line = null;
        while ((line = reader.readLine()) != null) {
          join.append(repeat(spaces + 1) + line + "\n");
        }
      } catch (IOException ex) {
        this.logger.log(Level.SEVERE, "Erro ao ler a InputStream \"" + key + "\":", ex);
      }
    }

    return join.toString();
  }

  private String repeat(int spaces) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < spaces; i++) {
      sb.append(" ");
    }

    return sb.toString();
  }

  public static class YamlEntry {

    private String annotation;
    private Object value;

    public YamlEntry(Object[] array) {
      this.annotation = (String) array[0];
      this.value = array[1];
    }

    public String getAnnotation() {
      return annotation;
    }

    public Object getValue() {
      return value;
    }
  }

  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.FIELD)
  public static @interface YamlEntryInfo {
    public String annotation() default "";
  }
}
