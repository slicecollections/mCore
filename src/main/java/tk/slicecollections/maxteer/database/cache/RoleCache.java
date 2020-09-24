package tk.slicecollections.maxteer.database.cache;

import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author Maxteer
 */
public class RoleCache {

  private static final Map<String, Object[]> CACHE = new ConcurrentHashMap<>();

  public static void setCache(String playerName, String role, String realName) {
    Object[] array = new Object[3];
    array[0] = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(30);
    array[1] = role;
    array[2] = realName;
    CACHE.put(playerName.toLowerCase(), array);
  }

  public static String get(String playerName) {
    Object[] array = CACHE.get(playerName.toLowerCase());
    if (array == null) {
      return null;
    }

    return array[1] + " : " + array[2];
  }

  public static boolean isPresent(String playerName) {
    return CACHE.containsKey(playerName.toLowerCase());
  }

  public static TimerTask clearCache() {
    return new TimerTask() {
      @Override
      public void run() {
        CACHE.entrySet().removeIf(entry -> (long) entry.getValue()[0] < System.currentTimeMillis());
      }
    };
  }
}
