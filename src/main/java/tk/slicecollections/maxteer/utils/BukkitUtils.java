package tk.slicecollections.maxteer.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import tk.slicecollections.maxteer.reflection.Accessors;
import tk.slicecollections.maxteer.reflection.MinecraftReflection;
import tk.slicecollections.maxteer.reflection.acessors.ConstructorAccessor;
import tk.slicecollections.maxteer.reflection.acessors.FieldAccessor;
import tk.slicecollections.maxteer.reflection.acessors.MethodAccessor;
import tk.slicecollections.maxteer.utils.enums.EnumMaterial;

import java.lang.reflect.Field;
import java.util.*;
import java.util.Map.Entry;

import static java.lang.Double.parseDouble;
import static java.lang.Float.parseFloat;

/**
 * Classe com utilitários relacionado a {@link org.bukkit}.
 *
 * @author Maxter
 */
public class BukkitUtils {

  /**
   * Todas as cores prontas da classe {@link Color}
   */
  public static final List<FieldAccessor<Color>> COLORS;
  public static final MethodAccessor GET_PROFILE;
  public static final FieldAccessor<GameProfile> SKULL_META_PROFILE;

  static {
    COLORS = new ArrayList<>();
    for (Field field : Color.class.getDeclaredFields()) {
      if (field.getType().equals(Color.class)) {
        COLORS.add(new FieldAccessor<>(field));
      }
    }
    GET_PROFILE = Accessors.getMethod(MinecraftReflection.getCraftBukkitClass("entity.CraftPlayer"), GameProfile.class, 0);
    SKULL_META_PROFILE = Accessors.getField(MinecraftReflection.getCraftBukkitClass("inventory.CraftMetaSkull"), "profile", GameProfile.class);
  }

  private static Map<Class<?>, MethodAccessor> getHandleCache = new HashMap<>();

  public static Object getHandle(Object target) {
    try {
      Class<?> clazz = target.getClass();
      MethodAccessor accessor = getHandleCache.get(clazz);
      if (accessor == null) {
        accessor = Accessors.getMethod(clazz, "getHandle");
        getHandleCache.put(clazz, accessor);
      }

      return accessor.invoke(target);
    } catch (Exception ex) {
      throw new IllegalArgumentException("Cannot find method getHandle() for " + target + ".");
    }
  }

  public static void openBook(Player player, ItemStack book) {
    Object entityPlayer = BukkitUtils.getHandle(player);

    ItemStack old = player.getInventory().getItemInHand();
    try {
      player.getInventory().setItemInHand(book);
      Accessors.getMethod(entityPlayer.getClass(), "openBook").invoke(entityPlayer, BukkitUtils.asNMSCopy(book));
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    player.getInventory().setItemInHand(old);
    player.updateInventory();
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
    EnchantmentStorageMeta enchantment = meta instanceof EnchantmentStorageMeta ? ((EnchantmentStorageMeta) meta) : null;

    if (split.length > 1) {
      stack.setAmount(Math.min(Integer.parseInt(split[1]), 64));
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
          if (enchantment != null) {
            enchantment.addStoredEnchant(Enchantment.getByName(enchanted.split(":")[0]), Integer.parseInt(enchanted.split(":")[1]), true);
            continue;
          }

          meta.addEnchant(Enchantment.getByName(enchanted.split(":")[0]), Integer.parseInt(enchanted.split(":")[1]), true);
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

          for (FieldAccessor<Color> field : COLORS) {
            if (field.getHandle().getName().equals(color.toUpperCase())) {
              if (armor != null) {
                armor.setColor(field.get(null));
              } else if (effect != null) {
                effect.setEffect(FireworkEffect.builder().withColor(field.get(null)).build());
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
        book.setPages(opt.split(">")[1].split("\\{pular}"));
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
   * {@link BukkitUtils#deserializeItemStack(String)}.
   */
  public static String serializeItemStack(ItemStack item) {
    StringBuilder sb = new StringBuilder(item.getType().name() + (item.getDurability() != 0 ? ":" + item.getDurability() : "") + " : " + item.getAmount());
    ItemMeta meta = item.getItemMeta();

    BookMeta book = meta instanceof BookMeta ? ((BookMeta) meta) : null;
    SkullMeta skull = meta instanceof SkullMeta ? ((SkullMeta) meta) : null;
    PotionMeta potion = meta instanceof PotionMeta ? ((PotionMeta) meta) : null;
    FireworkEffectMeta effect = meta instanceof FireworkEffectMeta ? ((FireworkEffectMeta) meta) : null;
    LeatherArmorMeta armor = meta instanceof LeatherArmorMeta ? ((LeatherArmorMeta) meta) : null;
    EnchantmentStorageMeta enchantment = meta instanceof EnchantmentStorageMeta ? ((EnchantmentStorageMeta) meta) : null;

    if (meta.hasDisplayName()) {
      sb.append(" : nome>").append(StringUtils.deformatColors(meta.getDisplayName()));
    }

    if (meta.hasLore()) {
      sb.append(" : desc>");
      for (int i = 0; i < meta.getLore().size(); i++) {
        String line = meta.getLore().get(i);
        sb.append(line).append(i + 1 == meta.getLore().size() ? "" : "\n");
      }
    }

    if (meta.hasEnchants() || (enchantment != null && enchantment.hasStoredEnchants())) {
      sb.append(" : encantar>");
      int size = 0;
      for (Entry<Enchantment, Integer> entry : (enchantment != null ? enchantment.getStoredEnchants() : meta.getEnchants()).entrySet()) {
        int level = entry.getValue();
        String name = entry.getKey().getName();
        sb.append(name).append(":").append(level).append(++size == (enchantment != null ? enchantment.getStoredEnchants() : meta.getEnchants()).size() ? "" : "\n");
      }
    }

    if (skull != null && !skull.getOwner().isEmpty()) {
      sb.append(" : dono>").append(skull.getOwner());
    }

    if (book != null && book.hasPages()) {
      sb.append(" : paginas>").append(StringUtils.join(book.getPages(), "{pular}"));
    }

    if (book != null && book.hasTitle()) {
      sb.append(" : titulo>").append(book.getTitle());
    }

    if (book != null && book.hasAuthor()) {
      sb.append(" : autor>").append(book.getAuthor());
    }

    if ((effect != null && effect.hasEffect() && !effect.getEffect().getColors().isEmpty()) || (armor != null && armor.getColor() != null)) {
      Color color = effect != null ? effect.getEffect().getColors().get(0) : armor.getColor();
      sb.append(" : pintar>").append(color.getRed()).append(":").append(color.getGreen()).append(":").append(color.getBlue());
    }

    if (potion != null && potion.hasCustomEffects()) {
      sb.append(" : efeito>");
      int size = 0;
      for (PotionEffect pe : potion.getCustomEffects()) {
        sb.append(pe.getType().getName()).append(":").append(pe.getAmplifier()).append(":").append(pe.getDuration()).append(++size == potion.getCustomEffects().size() ? "" : "\n");
      }
    }

    for (ItemFlag flag : meta.getItemFlags()) {
      sb.append(" : esconder>").append(flag.name());
    }

    return StringUtils.deformatColors(sb.toString()).replace("\n", "\\n");
  }

  /**
   * Seta a partir da {@code Reflection} o perfil de um {@link ItemStack} do tipo Cabeça.
   *
   * @param player O jogador para requisitar o {@link GameProfile}.
   * @param head   O {@link ItemStack} do tipo Cabeça.
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
   * @param head    O {@link ItemStack} do tipo Cabeça.
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

  private static Class<?> NBTagList = MinecraftReflection.getMinecraftClass("NBTTagList");
  private static Class<?> NBTagString = MinecraftReflection.getMinecraftClass("NBTTagString");
  private static ConstructorAccessor<?> constructorTagList = new ConstructorAccessor<>(NBTagList.getConstructors()[0]);
  private static ConstructorAccessor<?> constructorTagString = new ConstructorAccessor<>(NBTagString.getConstructors()[1]);
  private static MethodAccessor getTag = Accessors.getMethod(MinecraftReflection.getItemStackClass(), "getTag");
  private static MethodAccessor setCompound = Accessors.getMethod(MinecraftReflection.getNBTTagCompoundClass(), "set", String.class, NBTagList.getSuperclass());
  private static MethodAccessor addList = Accessors.getMethod(NBTagList, "add");
  private static MethodAccessor asNMSCopy = Accessors.getMethod(MinecraftReflection.getCraftItemStackClass(), "asNMSCopy");
  private static MethodAccessor asCraftMirror = Accessors.getMethod(MinecraftReflection.getCraftItemStackClass(), "asCraftMirror");

  /**
   * Transforma um {@link ItemStack} em ItemStack do NMS
   *
   * @param item Item para transformar.
   * @return NMS ItemStack
   */
  public static Object asNMSCopy(ItemStack item) {
    return asNMSCopy.invoke(null, item);
  }

  /**
   * Transforma um ItemStack do NMS em {@link ItemStack}
   *
   * @param nmsItem Item NMS para transformar.
   * @return ItemStack
   */
  public static ItemStack asCraftMirror(Object nmsItem) {
    return (ItemStack) asCraftMirror.invoke(null, nmsItem);
  }

  public static ItemStack setNBTList(ItemStack item, String key, List<String> strings) {
    Object nmsStack = asNMSCopy(item);
    Object compound = getTag.invoke(nmsStack);
    Object compoundList = constructorTagList.newInstance();
    for (String string : strings) {
      addList.invoke(compoundList, constructorTagString.newInstance(string));
    }
    setCompound.invoke(compound, key, compoundList);
    return asCraftMirror(nmsStack);
  }

  /**
   * Transforma uma {@link Location} em uma {@code String} utilizando o seguinte formato:<br/>
   * {@code "mundo; x; y; z; yaw; pitch"}
   *
   * @param unserialized A {@link Location} para transformar em {@code String}.
   * @return A {@link Location} transformada em uma {@code String}.
   */
  public static String serializeLocation(Location unserialized) {
    return unserialized.getWorld().getName() + "; " + unserialized.getX() + "; " + unserialized.getY() + "; " + unserialized.getZ() + "; " + unserialized
      .getYaw() + "; " + unserialized.getPitch();
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
