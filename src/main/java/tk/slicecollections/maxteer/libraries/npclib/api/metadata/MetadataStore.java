package tk.slicecollections.maxteer.libraries.npclib.api.metadata;

/**
 * @author Maxter
 */
public interface MetadataStore {

  <T> T get(String key);

  <T> T get(String key, T def);

  boolean has(String key);

  void remove(String key);

  void set(String key, Object data);
}
