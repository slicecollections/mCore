package tk.slicecollections.maxteer.database.data;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import tk.slicecollections.maxteer.database.data.interfaces.AbstractContainer;
import tk.slicecollections.maxteer.reflection.Accessors;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Maxter
 */
public class DataContainer {

  private Object value;

  public DataContainer(Object value) {
    this.value = value;
  }

  public void gc() {
    this.value = null;
    this.containerMap.values().forEach(AbstractContainer::gc);
    this.containerMap.clear();
    this.containerMap = null;
  }

  public void set(Object value) {
    this.value = value;
  }

  public void addInt(int amount) {
    this.value = getAsInt() + amount;
  }

  public void addLong(long amount) {
    this.value = getAsLong() + amount;
  }

  public void addDouble(double amount) {
    this.value = getAsDouble() + amount;
  }

  public void removeInt(int amount) {
    this.value = getAsInt() - amount;
  }

  public void removeLong(long amount) {
    this.value = getAsLong() - amount;
  }

  public void removeDouble(double amount) {
    this.value = getAsDouble() - amount;
  }

  public Object get() {
    return value;
  }

  public int getAsInt() {
    return Integer.parseInt(this.getAsString());
  }

  public long getAsLong() {
    return Long.parseLong(this.getAsString());
  }

  public double getAsDouble() {
    return Double.parseDouble(this.getAsString());
  }

  public String getAsString() {
    return value.toString();
  }

  public boolean getAsBoolean() {
    return Boolean.parseBoolean(this.getAsString());
  }

  public JSONObject getAsJsonObject() {
    try {
      return (JSONObject) new JSONParser().parse(this.getAsString());
    } catch (Exception ex) {
      throw new IllegalArgumentException("\"" + value + "\" is not a JsonObject: ", ex);
    }
  }

  public JSONArray getAsJsonArray() {
    try {
      return (JSONArray) new JSONParser().parse(this.getAsString());
    } catch (Exception ex) {
      throw new IllegalArgumentException("\"" + value + "\" is not a JsonArray: ", ex);
    }
  }

  private Map<Class<? extends AbstractContainer>, AbstractContainer> containerMap = new HashMap<>();

  @SuppressWarnings("unchecked")
  public <T extends AbstractContainer> T getContainer(Class<T> containerClass) {
    if (!this.containerMap.containsKey(containerClass)) {
      this.containerMap.put(containerClass, Accessors.getConstructor(containerClass, DataContainer.class).newInstance(this));
    }

    return (T) this.containerMap.get(containerClass);
  }
}
