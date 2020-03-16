package tk.slicecollections.maxteer.reflection;

import java.util.Arrays;
import tk.slicecollections.maxteer.libraries.MinecraftVersion;

/**
 * Classe utilizada para pegar várias classes e métodos<br/>
 * do Servidor de Minecraft através da Reflection do java.
 * 
 * @author Maxteer
 */
@SuppressWarnings("rawtypes")
public class MinecraftReflection {

  public static final MinecraftVersion VERSION = MinecraftVersion.getCurrentVersion();

  public static String NMU_PREFIX = "";
  public static String OBC_PREFIX = "";
  public static String NMS_PREFIX = "";

  private static Class<?> craftItemStack;

  private static Class<?> block, blocks;
  private static Class<?> entity, entityHuman;
  private static Class<?> enumDirection, enumProtocol, enumGamemode, enumPlayerInfoAction, enumTitleAction;
  private static Class<?> nbtTagCompound;
  private static Class<?> channel, playerInfoData, serverPing, serverData, serverPingPlayerSample;
  private static Class<?> serverConnection;
  private static Class<?> world, worldServer;
  private static Class<?> blockPosition, iBlockData;
  private static Class<?> vector3F;
  private static Class<?> iChatBaseComponent, chatSerializer, itemStack;
  private static Class<?> gameProfile, propertyMap, property;
  private static Class<?> dataWatcher, dataWatcherObject, dataWatcherSerializer, dataWatcherRegistry, watchableObject;

  static {
    try {
      getClass("net.minecraft.util.com.google.common.collect.ImmutableList");
      NMU_PREFIX = "net.minecraft.util.";
    } catch (Exception ex) {
      // nao possui mais a net.minecraft.util
    }

    OBC_PREFIX = "org.bukkit.craftbukkit." + VERSION.getVersion() + ".";
    NMS_PREFIX = OBC_PREFIX.replace("org.bukkit.craftbukkit", "net.minecraft.server");
  }

  // -- Classes em geral para usar.

  public static Class getServerPing() {
    if (serverPing == null) {
      serverPing = getMinecraftClass("ServerPing");
    }

    return serverPing;
  }

  public static Class getServerData() {
    if (serverData == null) {
      serverData = getMinecraftClass("ServerPing$ServerData");
    }

    return serverData;
  }

  public static Class getServerPingPlayerSample() {
    if (serverPingPlayerSample == null) {
      serverPingPlayerSample = getMinecraftClass("ServerPing$ServerPingPlayerSample");
    }

    return serverPingPlayerSample;
  }

  public static Class getEnumDirectionClass() {
    if (enumDirection == null) {
      enumDirection = getMinecraftClass("EnumDirection");
    }

    return enumDirection;
  }

  public static Class getEnumProtocolDirectionClass() {
    return getMinecraftClass(true, "EnumProtocolDirection");
  }

  public static Class getEnumProtocolClass() {
    if (enumProtocol == null) {
      enumProtocol = getMinecraftClass("EnumProtocol");
    }

    return enumProtocol;
  }

  public static Class getEnumGamemodeClass() {
    if (enumGamemode == null) {
      enumGamemode = getMinecraftClass("EnumGamemode", "WorldSettings$EnumGamemode");
    }

    return enumGamemode;
  }

  public static Class getEnumPlayerInfoActionClass() {
    if (enumPlayerInfoAction == null) {
      enumPlayerInfoAction = getMinecraftClass(int.class, "PacketPlayOutPlayerInfo$EnumPlayerInfoAction");
    }

    return enumPlayerInfoAction;
  }

  public static Class getEnumTitleAction() {
    if (enumTitleAction == null) {
      enumTitleAction = getMinecraftClass(int.class, "PacketPlayOutTitle$EnumTitleAction");
    }

    return enumTitleAction;
  }

  public static Class<?> getNBTTagCompoundClass() {
    if (nbtTagCompound == null) {
      nbtTagCompound = getMinecraftClass("NBTTagCompound");
    }

    return nbtTagCompound;
  }

  public static Class<?> getDataWatcherClass() {
    if (dataWatcher == null) {
      dataWatcher = getMinecraftClass("DataWatcher");
    }

    return dataWatcher;
  }

  public static Class<?> getDataWatcherObjectClass() {
    if (dataWatcherObject == null) {
      dataWatcherObject = getMinecraftClass(int.class, "DataWatcherObject");
    }

    return dataWatcherObject;
  }

  public static Class<?> getDataWatcherSerializerClass() {
    if (dataWatcherSerializer == null) {
      dataWatcherSerializer = getMinecraftClass(null, "DataWatcherSerializer");
    }

    return dataWatcherSerializer;
  }

  public static Class<?> getDataWatcherRegistryClass() {
    if (dataWatcherRegistry == null) {
      dataWatcherRegistry = getMinecraftClass(null, "DataWatcherRegistry");
    }

    return dataWatcherRegistry;
  }

  public static Class<?> getWatchableObjectClass() {
    if (watchableObject == null) {
      watchableObject = getMinecraftClass("DataWatcher$Item", "DataWatcher$WatchableObject", "WatchableObject");
    }

    return watchableObject;
  }

  public static Class<?> getBlock() {
    if (block == null) {
      block = getMinecraftClass("Block");
    }

    return block;
  }

  public static Class getBlocks() {
    if (blocks == null) {
      blocks = getMinecraftClass("Blocks");
    }

    return blocks;
  }

  public static Class<?> getChannelClass() {
    if (channel == null) {
      channel = getMinecraftUtilClass("io.netty.channel.Channel");
    }

    return channel;
  }

  public static Class<?> getEntityClass() {
    if (entity == null) {
      entity = getMinecraftClass("Entity");
    }

    return entity;
  }

  public static Class<?> getEntityHumanClass() {
    if (entityHuman == null) {
      entityHuman = getMinecraftClass("EntityHuman");
    }

    return entityHuman;
  }

  public static Class<?> getPlayerInfoDataClass() {
    if (playerInfoData == null) {
      playerInfoData = getMinecraftClass("PacketPlayOutPlayerInfo$PlayerInfoData");
    }

    return playerInfoData;
  }

  public static Class<?> getServerConnectionClass() {
    if (serverConnection == null) {
      serverConnection = getMinecraftClass("ServerConnection");
    }

    return serverConnection;
  }

  public static Class<?> getWorldClass() {
    if (world == null) {
      world = getMinecraftClass("World");
    }

    return world;
  }

  public static Class<?> getWorldServerClass() {
    if (worldServer == null) {
      worldServer = getMinecraftClass("WorldServer");
    }

    return worldServer;
  }

  public static Class getIChatBaseComponent() {
    if (iChatBaseComponent == null) {
      iChatBaseComponent = getMinecraftClass("IChatBaseComponent");
    }

    return iChatBaseComponent;
  }

  public static Class<?> getChatSerializer() {
    if (chatSerializer == null) {
      chatSerializer = getMinecraftClass("IChatBaseComponent$ChatSerializer", "ChatSerializer");
    }

    return chatSerializer;
  }

  public static Class<?> getCraftItemStackClass() {
    if (craftItemStack == null) {
      craftItemStack = getCraftBukkitClass("inventory.CraftItemStack");
    }

    return craftItemStack;
  }

  public static Class<?> getItemStackClass() {
    if (itemStack == null) {
      itemStack = getMinecraftClass("ItemStack");
    }

    return itemStack;
  }

  public static Class<?> getBlockPositionClass() {
    if (blockPosition == null) {
      blockPosition = getMinecraftClass("BlockPosition");
    }

    return blockPosition;
  }

  public static Class<?> getIBlockDataClass() {
    if (iBlockData == null) {
      iBlockData = getMinecraftClass("IBlockData");
    }

    return iBlockData;
  }

  public static Class<?> getVector3FClass() {
    if (vector3F == null) {
      vector3F = getMinecraftClass("Vector3f");
    }

    return vector3F;
  }

  public static Class<?> getGameProfileClass() {
    if (gameProfile == null) {
      gameProfile = getMinecraftUtilClass("com.mojang.authlib.GameProfile");
    }

    return gameProfile;
  }

  public static Class<?> getPropertyMapClass() {
    if (propertyMap == null) {
      propertyMap = getMinecraftUtilClass("com.mojang.authlib.properties.PropertyMap");
    }

    return propertyMap;
  }

  public static Class<?> getPropertyClass() {
    if (property == null) {
      property = getMinecraftUtilClass("com.mojang.authlib.properties.Property");
    }

    return property;
  }

  // -- Utilitarios

  /**
   * Verifica se o object é da classe escolhida.
   * 
   * @param clazz - A classe para verificar.
   * @param object - O objeto que é da classe.
   * @return TRUE caso o objeto seja relacionado a classe, FALSE caso não.
   */
  public static boolean is(Class<?> clazz, Object object) {
    if (clazz == null || object == null) {
      return false;
    }

    return clazz.isAssignableFrom(object.getClass());
  }

  // -- Metodos para buscar classes.

  /**
   * Método utilizado para procurar por uma classe e carrega-la.
   * 
   * @param name O nome da classe.
   * @return A classe com o nome desejado.
   * @throws IllegalArgumentException Caso não encontre a classe.
   */
  public static Class<?> getClass(String name) {
    try {
      Class<?> clazz = MinecraftReflection.class.getClassLoader().loadClass(name);
      return clazz;
    } catch (Exception ex) {
      throw new IllegalArgumentException("Cannot find class " + name);
    }
  }

  /**
   * Um encurtador para {@link MinecraftReflection#getCraftBukkitClass(Object, String...)}<br>
   * com o Object <code>false</code> para sempre dar throw na exception.
   */
  public static Class<?> getCraftBukkitClass(String... names) {
    return getCraftBukkitClass(false, names);
  }

  /**
   * Método utilizado para procurar uma classe da package <code>org.bukkit.craftbukkit</code> a partir
   * dos nomes desejados.
   * 
   * @param canNull FALSE caso queira que ocorra a Exception, ou uma Classe para retornar caso nao
   *        encontre.
   * @param names Os possíveis nomes da classe.
   * @return A classe encontrada.
   * @throws IllegalArgumentException Caso não ache nenhuma classe.
   */
  public static Class<?> getCraftBukkitClass(Object canNull, String... names) {
    for (String name : names) {
      try {
        Class<?> clazz = getClass(OBC_PREFIX + name);
        return clazz;
      } catch (Exception ex) {
      }
    }

    if (canNull != null && canNull instanceof Boolean && (Boolean) canNull == false) {
      throw new IllegalArgumentException("Cannot find CraftBukkit Class from names " + Arrays.asList(names) + ".");
    }

    return canNull != null && canNull.getClass().equals(Class.class) ? (Class) canNull : null;
  }

  /**
   * Um encurtador para {@link MinecraftReflection#getMinecraftClass(Object, String...)}<br>
   * com o Object <code>false</code> para sempre dar throw na exception.
   */
  public static Class<?> getMinecraftClass(String... names) {
    return getMinecraftClass(false, names);
  }

  /**
   * Método utilizado para procurar uma classe da package <code>net.minecraft.server</code> a partir dos
   * nomes desejados.
   * 
   * @param canNull FALSE caso queira que ocorra a Exception, ou uma Classe para retornar caso não
   *        ache.
   * @param names Os possiveis nomes da classe.
   * @return A classe encontrada.
   * @throws IllegalArgumentException Caso não ache nenhuma classe.
   */
  public static Class<?> getMinecraftClass(Object canNull, String... names) {
    for (String name : names) {
      try {
        Class clazz = getClass(NMS_PREFIX + name);
        return clazz;
      } catch (Exception ex) {
      }
    }

    if (canNull != null && canNull instanceof Boolean && (Boolean) canNull == false) {
      throw new IllegalArgumentException("Cannot find MinecraftServer Class from names " + Arrays.asList(names) + ".");
    }

    return canNull != null && canNull.getClass().equals(Class.class) ? (Class) canNull : null;
  }

  /**
   * Metodo utilizado para procurar uma classe de utilidade do minecraft<br>
   * Caso ainda exista a package net.minecraft.util irá usa-la como prefixo. Caso contrario irá
   * procurar pelo nome sem nenhuma modificação.
   * 
   * @param name O nome da classe.
   * @return A classe encontrada.
   * @throws IllegalArgumentException Caso não ache nenhuma classe.
   */
  public static Class<?> getMinecraftUtilClass(String name) {
    try {
      Class<?> clazz = getClass(NMU_PREFIX + name);
      return clazz;
    } catch (Exception ex) {
      throw new IllegalArgumentException("Cannot find MinecraftUtil Class from name " + name + ".");
    }
  }
}
