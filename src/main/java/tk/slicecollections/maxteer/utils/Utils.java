package tk.slicecollections.maxteer.utils;

import org.bukkit.Location;

/**
 * Classe com utilitários diversos sem nicho predefinido.
 *
 * @author Maxter
 */
public class Utils {

  public static float clampYaw(float yaw) {
    while (yaw < -180.0F) {
      yaw += 360.0F;
    }
    while (yaw >= 180.0F) {
      yaw -= 360.0F;
    }

    return yaw;
  }

  /**
   * Verifica se uma localização tem a chunk carregada no mundo.
   *
   * @param location localização para verificar
   * @return TRUE caso a localização tenha a chunk carregada, FALSE caso não tenha.
   */
  public static boolean isLoaded(Location location) {
    if (location == null || location.getWorld() == null) {
      return false;
    }

    int chunkX = location.getBlockX() >> 4;
    int chunkZ = location.getBlockZ() >> 4;
    return location.getWorld().isChunkLoaded(chunkX, chunkZ);
  }
}
