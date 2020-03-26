package tk.slicecollections.maxteer.libraries.npclib.npc.profile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import com.google.common.base.Charsets;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import tk.slicecollections.maxteer.libraries.npclib.npc.profile.api.InvalidMojangException;
import tk.slicecollections.maxteer.libraries.npclib.npc.profile.api.MineToolsAPI;
import tk.slicecollections.maxteer.libraries.npclib.npc.profile.api.MojangAPI;

/**
 * @author Maxter
 */
public abstract class Mojang {

  public abstract String fetchId(String name);

  public abstract String fetchSkinProperty(String id);

  public abstract boolean getResponse();

  private static final List<Mojang> MOJANGAPIS;
  private static final Cache<String, String> CACHED_UUID, CACHED_PROPERTY;

  public static String getUUID(String name) throws InvalidMojangException {
    String id = CACHED_UUID.getIfPresent(name);
    if (id != null) {
      return id;
    }

    for (Mojang api : MOJANGAPIS) {
      id = api.fetchId(name);
      if (api.getResponse()) {
        if (id != null) {
          CACHED_UUID.put(name, id);
        }

        return id;
      }
    }

    throw new InvalidMojangException("Nenhuma api conseguiu pegar o UUID pelo nome: " + name);
  }

  public static String getSkinProperty(String id) throws InvalidMojangException {
    String property = CACHED_PROPERTY.getIfPresent(id);
    if (property != null) {
      return property;
    }

    for (Mojang api : MOJANGAPIS) {
      property = api.fetchSkinProperty(id);
      if (api.getResponse()) {
        if (property != null) {
          CACHED_PROPERTY.put(id, property);
        }

        return property;
      }
    }

    throw new InvalidMojangException("Nenhuma api conseguiu pegar a Property pelo id: " + id);
  }

  public static UUID getOfflineUUID(String name) {
    return UUID.nameUUIDFromBytes(new String("OfflinePlayer:" + name).getBytes(Charsets.UTF_8));
  }

  public static String parseUUID(String withoutDashes) {
    return withoutDashes.substring(0, 8) + '-' + withoutDashes.substring(8, 12) + '-' + withoutDashes.substring(12, 16) + '-' + withoutDashes.substring(16, 20) + '-'
        + withoutDashes.substring(20, 32);
  }

  static {
    MOJANGAPIS = new ArrayList<>();
    MOJANGAPIS.add(new MojangAPI());
    MOJANGAPIS.add(new MineToolsAPI());

    CACHED_UUID = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.HOURS).build();
    CACHED_PROPERTY = CacheBuilder.newBuilder().expireAfterWrite(30, TimeUnit.MINUTES).build();
  }
}
