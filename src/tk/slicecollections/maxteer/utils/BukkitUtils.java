package tk.slicecollections.maxteer.utils;

import static java.lang.Double.parseDouble;
import static java.lang.Float.parseFloat;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import tk.slicecollections.maxteer.reflection.Accessors;
import tk.slicecollections.maxteer.reflection.MinecraftReflection;
import tk.slicecollections.maxteer.reflection.acessors.FieldAccessor;
import tk.slicecollections.maxteer.reflection.acessors.MethodAccessor;
import tk.slicecollections.maxteer.utils.enums.EnumMaterial;

/**
 * Classe com utilitários relacionado a {@link org.bukkit}.
 * 
 * @author Maxter
 */
public class BukkitUtils {

  private static final List<Field> COLORS;
  private static final MethodAccessor GET_PROFILE;
  private static final FieldAccessor<GameProfile> SKULL_META_PROFILE;

  static {
    List<Field> fields = new ArrayList<>();
    for (Field field : Color.class.getDeclaredFields()) {
      if (Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers()) && Modifier.isPublic(field.getModifiers())) {
        fields.add(field);
      }
    }

    COLORS = fields;
    GET_PROFILE = Accessors.getMethod(MinecraftReflection.getCraftBukkitClass("entity.CraftPlayer"), GameProfile.class, 0);
    SKULL_META_PROFILE = Accessors.getField(MinecraftReflection.getCraftBukkitClass("inventory.CraftMetaSkull"), "profile", GameProfile.class);
  }

  /**
   * Cria um {@link ItemStack} a partir de uma {@code String}.<br/>
   * Formato: {@code MATERIAL:DURABILIDADE : QUANTIDADE : tag>valor}<br/>
   * Referência de Material: {@link EnumMaterial} (prioridade) e {@link Material}<br/>
   * <br/>
   * Propriedades (TAGS) disponíveis:
   * <ul>
   * <li>nome>&aSei lá - Seta o nome do Item.</li>
   * <li>desc>&7Linha 1\n&7Linha2 - Seta a descrição do Item.</li>
   * <li>encantar>DAMAGE_ALL:1\nFIRE_ASPECT:1 - Encanta o Item.</li>
   * <li>pintar>{@link Color} ou pintar>r:g:b - Pinta os Itens: Armadura de Couro e Fogos de
   * Artifício.</li>
   * <li>dono>Notch - Seta o dono de uma cabeça (Recomendado utilizar o
   * {@link BukkitUtils#putProfileOnSkull(Player, ItemStack)}.</li>
   * <li>skin>skinvalue - Seta o valor da Skin através do {@link GameProfile} para cabeças
   * customizadas.</li>
   * <li>paginas>Linha1 pagina1\nLinha2 pagina1{pular}Linha1 pagina2\nLinha2 pagina2 - Seta as páginas
   * do livro. (Utilize {pular} para pular para outra página)</li>
   * <li>autor>&6Maxter - Seta o autor do livro.</li>
   * <li>titulo>&6MCore - Seta o título do livro.</li>
   * <li>efeito>{@link PotionEffectType}:nivel(começa do 0):ticks(20ticks =
   * 1segundo)\nINVISIBILITY:0:600 - Adiciona efeitos em poções.</li>
   * <li>esconder>{@link ItemFlag}\n{@link ItemFlag} ou esconder>tudo - Aplica ItemFlags (Adicionado
   * na 1.8)</li>
   * </ul>
   * 
   * @param item O {@link ItemStack} em uma String.
   * @return O {@link ItemStack} criado.
   */
  public static ItemStack deserializeItemStack(String item) {
    if (item == null || item.isEmpty()) {
      return new ItemStack(Material.AIR);
    }

    item = StringUtils.formatColors(item).replace("\\n", "\n");
    String[] split = item.split(" : ");
    String mat = split[0].split(":")[0];

    ItemStack stack = new ItemStack(EnumMaterial.matchMaterial(mat.toUpperCase()), 1);
    if (split[0].split(":").length > 1) {
      stack.setDurability((short) Integer.parseInt(split[0].split(":")[1]));
    }
    ItemMeta meta = stack.getItemMeta();

    BookMeta book = meta instanceof BookMeta ? ((BookMeta) meta) : null;
    SkullMeta skull = meta instanceof SkullMeta ? ((SkullMeta) meta) : null;
    PotionMeta potion = meta instanceof PotionMeta ? ((PotionMeta) meta) : null;
    FireworkEffectMeta effect = meta instanceof FireworkEffectMeta ? ((FireworkEffectMeta) meta) : null;
    LeatherArmorMeta armor = meta instanceof LeatherArmorMeta ? ((LeatherArmorMeta) meta) : null;

    if (split.length > 1) {
      stack.setAmount(Integer.parseInt(split[1]) > 64 ? 64 : Integer.parseInt(split[1]));
    }

    List<String> lore = new ArrayList<>();
    for (int i = 2; i < split.length; i++) {
      String opt = split[i];

      if (opt.startsWith("nome>")) {
        meta.setDisplayName(StringUtils.formatColors(opt.split(">")[1]));
      }

      if (opt.startsWith("desc>")) {
        for (String lored : opt.split(">")[1].split("\n")) {
          lore.add(StringUtils.formatColors(lored));
        }
      }

      if (opt.startsWith("encantar>")) {
        for (String enchanted : opt.split(">")[1].split("\n")) {
          meta.addEnchant(Enchantment.getByName(enchanted.split(":")[0]), Integer.parseInt(enchanted.split(":")[1]), false);
        }
      }

      if (opt.startsWith("pintar>") && (effect != null || armor != null)) {
        for (String color : opt.split(">")[1].split("\n")) {
          if (color.split(":").length > 2) {
            if (armor != null) {
              armor.setColor(Color.fromRGB(Integer.parseInt(color.split(":")[0]), Integer.parseInt(color.split(":")[1]), Integer.parseInt(color.split(":")[2])));
            } else if (effect != null) {
              effect.setEffect(FireworkEffect.builder()
                  .withColor(Color.fromRGB(Integer.parseInt(color.split(":")[0]), Integer.parseInt(color.split(":")[1]), Integer.parseInt(color.split(":")[2]))).build());
            }
            continue;
          }

          for (Field field : COLORS) {
            if (field.getName().equals(color.toUpperCase())) {
              try {
                if (armor != null) {
                  armor.setColor((Color) field.get(null));
                } else if (effect != null) {
                  effect.setEffect(FireworkEffect.builder().withColor((Color) field.get(null)).build());
                }
              } catch (Exception ex) {
              }
              break;
            }
          }
        }
      }

      if (opt.startsWith("dono>") && skull != null) {
        skull.setOwner(opt.split(">")[1]);
      }

      if (opt.startsWith("skin>") && skull != null) {
        GameProfile gp = new GameProfile(UUID.randomUUID(), null);
        gp.getProperties().put("textures", new Property("textures", opt.split(">")[1]));
        SKULL_META_PROFILE.set(skull, gp);
      }

      if (opt.startsWith("paginas>") && book != null) {
        book.setPages(opt.split(">")[1].split("\\{pular\\}"));
      }

      if (opt.startsWith("autor>") && book != null) {
        book.setAuthor(opt.split(">")[1]);
      }

      if (opt.startsWith("titulo>") && book != null) {
        book.setTitle(opt.split(">")[1]);
      }

      if (opt.startsWith("efeito>") && potion != null) {
        for (String pe : opt.split(">")[1].split("\n")) {
          potion.addCustomEffect(new PotionEffect(PotionEffectType.getByName(pe.split(":")[0]), Integer.parseInt(pe.split(":")[2]), Integer.parseInt(pe.split(":")[1])), false);
        }
      }

      if (opt.startsWith("esconder>")) {
        String[] flags = opt.split(">")[1].split("\n");
        for (String flag : flags) {
          if (flag.equalsIgnoreCase("tudo")) {
            meta.addItemFlags(ItemFlag.values());
            break;
          } else {
            meta.addItemFlags(ItemFlag.valueOf(flag.toUpperCase()));
          }
        }
      }
    }
    if (!lore.isEmpty()) {
      meta.setLore(lore);
    }

    stack.setItemMeta(meta);
    return stack;
  }

  /**
   * Transforma um {@link ItemStack} em uma {@code String}.<br/>
   * 
   * @param item O ItemStack para transforma em String.
   * @return Um ItemStack transformado em {@code String} para ser utilizado no método
   *         {@link BukkitUtils#deserializeItemStack(String)}.
   */
  public static String serializeItemStack(ItemStack item) {
    StringBuilder sb = new StringBuilder(item.getType().name() + (item.getDurability() != 0 ? ":" + item.getDurability() : "") + " : " + item.getAmount());
    ItemMeta meta = item.getItemMeta();

    BookMeta book = meta instanceof BookMeta ? ((BookMeta) meta) : null;
    SkullMeta skull = meta instanceof SkullMeta ? ((SkullMeta) meta) : null;
    PotionMeta potion = meta instanceof PotionMeta ? ((PotionMeta) meta) : null;
    FireworkEffectMeta effect = meta instanceof FireworkEffectMeta ? ((FireworkEffectMeta) meta) : null;
    LeatherArmorMeta armor = meta instanceof LeatherArmorMeta ? ((LeatherArmorMeta) meta) : null;

    if (meta.hasDisplayName()) {
      sb.append(" : nome>" + StringUtils.deformatColors(meta.getDisplayName()));
    }

    if (meta.hasLore()) {
      sb.append(" : desc>");
      for (int i = 0; i < meta.getLore().size(); i++) {
        String line = meta.getLore().get(i);
        sb.append(line + (i + 1 == meta.getLore().size() ? "" : "\n"));
      }
    }

    if (meta.hasEnchants()) {
      sb.append(" : encantar>");
      int size = 0;
      for (Entry<Enchantment, Integer> entry : meta.getEnchants().entrySet()) {
        int level = entry.getValue();
        String name = entry.getKey().getName();
        sb.append(name + ":" + level + (++size == meta.getEnchants().size() ? "" : "\n"));
      }
    }

    if (skull != null && !skull.getOwner().isEmpty()) {
      sb.append(" : dono>" + skull.getOwner());
    }

    if (book != null && book.hasPages()) {
      sb.append(" : paginas>" + StringUtils.join(book.getPages(), "{pular}"));
    }

    if (book != null && book.hasTitle()) {
      sb.append(" : titulo>" + book.getTitle());
    }

    if (book != null && book.hasAuthor()) {
      sb.append(" : autor>" + book.getAuthor());
    }

    if ((effect != null && effect.hasEffect() && !effect.getEffect().getColors().isEmpty()) || (armor != null && armor.getColor() != null)) {
      Color color = effect != null ? effect.getEffect().getColors().get(0) : armor.getColor();
      sb.append(" : pintar>" + color.getRed() + ":" + color.getGreen() + ":" + color.getBlue());
    }

    if (potion != null && potion.hasCustomEffects()) {
	  sb.append(" : efeito>");
	  int size = 0;
      for (PotionEffect pe : potion.getCustomEffects()) {
        sb.append(pe.getType().getName() + ":" + pe.getAmplifier() + ":" + pe.getDuration() + (++size == potion.getCustomEffects().size() ? "" : "\n"));
      }
    }

    for (ItemFlag flag : meta.getItemFlags()) {
      sb.append(" : esconder>" + flag.name());
    }

    return StringUtils.deformatColors(sb.toString());
  }

  /**
   * Seta a partir da {@code Reflection} o perfil de um {@link ItemStack} do tipo Cabeça.
   * 
   * @param player O jogador para requisitar o {@link GameProfile}.
   * @param head O {@link ItemStack} do tipo Cabeça.
   * @return O {@link ItemStack} modificado com o Perfil do jogador.
   */
  public static ItemStack putProfileOnSkull(Player player, ItemStack head) {
    if (head == null || !(head.getItemMeta() instanceof SkullMeta)) {
      return head;
    }

    ItemMeta meta = head.getItemMeta();
    SKULL_META_PROFILE.set(meta, (GameProfile) GET_PROFILE.invoke(player));
    head.setItemMeta(meta);
    return head;
  }

  /**
   * Seta a partir da {@code Reflection} o perfil de um {@link ItemStack} do tipo Cabeça.
   * 
   * @param profile O {@link GameProfile} para modificar.
   * @param head O {@link ItemStack} do tipo Cabeça.
   * @return O {@link ItemStack} modificado com o Perfil.
   */
  public static ItemStack putProfileOnSkull(Object profile, ItemStack head) {
    if (head == null || !(head.getItemMeta() instanceof SkullMeta)) {
      return head;
    }

    ItemMeta meta = head.getItemMeta();
    SKULL_META_PROFILE.set(meta, (GameProfile) profile);
    head.setItemMeta(meta);
    return head;
  }
  
  /**
   * Faz o item "Brilhar" (Encantamento) sem mostrar os encantamentos do item.
   * 
   * @param item O {@link ItemStack}
   */
  public static void putGlowEnchantment(ItemStack item) {
    ItemMeta meta = item.getItemMeta();
    meta.addEnchant(Enchantment.LURE, 1, true);
    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
    item.setItemMeta(meta);
  }

  /**
   * Transforma uma {@link Location} em uma {@code String} utilizando o seguinte formato:<br/>
   * {@code "mundo; x; y; z; yaw; pitch"}
   * 
   * @param unserialized A {@link Location} para transformar em {@code String}.
   * @return A {@link Location} transformada em uma {@code String}.
   */
  public static String serializeLocation(Location unserialized) {
    String serialized = unserialized.getWorld().getName() + "; " + unserialized.getX() + "; " + unserialized.getY() + "; " + unserialized.getZ() + "; " + unserialized.getYaw()
        + "; " + unserialized.getPitch();
    return serialized;
  }

  /**
   * Transforma uma {@code String} em uma {@link Location} utilizando o seguinte formato:<br/>
   * {@code "mundo; x; y; z; yaw; pitch"}
   * 
   * @param serialized A {@code String} para transformar em {@link Location}.
   * @return A {@code String} transformada em uma {@link Location}.
   */
  public static Location deserializeLocation(String serialized) {
    String[] divPoints = serialized.split("; ");
    Location deserialized = new Location(Bukkit.getWorld(divPoints[0]), parseDouble(divPoints[1]), parseDouble(divPoints[2]), parseDouble(divPoints[3]));
    deserialized.setYaw(parseFloat(divPoints[4]));
    deserialized.setPitch(parseFloat(divPoints[5]));
    return deserialized;
  }
}
