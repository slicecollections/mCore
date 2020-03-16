package tk.slicecollections.maxteer.nms.v1_8_R3.utils;

import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_8_R3.metadata.PlayerMetadataStore;

public class UUIDMetadataStore extends PlayerMetadataStore {
  
  @Override
  protected String disambiguate(OfflinePlayer player, String metadataKey) {
    return player.getUniqueId().toString() + ":" + metadataKey;
  }
}
