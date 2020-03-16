package tk.slicecollections.maxteer.libraries.npclib.api.metadata;

public interface MetadataStore {
  
  public <T> T get(String key);
  
  public <T> T get(String key, T def);
  
  boolean has(String key);
  
  void remove(String key);
  
  public void set(String key, Object data);
}
