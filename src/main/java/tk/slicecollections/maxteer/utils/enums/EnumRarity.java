package tk.slicecollections.maxteer.utils.enums;

import tk.slicecollections.maxteer.utils.StringUtils;

import java.util.concurrent.ThreadLocalRandom;

public enum EnumRarity {
  DIVINO("§bDivino", 10),
  EPICO("§6Épico", 25),
  RARO("§dRaro", 50),
  COMUM("§9Comum", 100);

  private String name;
  private int percentage;

  EnumRarity(String name, int percentage) {
    this.name = name;
    this.percentage = percentage;
  }

  public String getName() {
    return this.name;
  }

  public String getColor() {
    return StringUtils.getFirstColor(this.getName());
  }

  public String getTagged() {
    return this.getColor() + "[" + StringUtils.stripColors(this.getName()) + "]";
  }

  private static final EnumRarity[] VALUES = values();

  public static EnumRarity getRandomRarity() {
    int random = ThreadLocalRandom.current().nextInt(100);
    for (EnumRarity rarity : VALUES) {
      if (random <= rarity.percentage) {
        return rarity;
      }
    }

    return COMUM;
  }

  public static EnumRarity fromName(String name) {
    for (EnumRarity rarity : VALUES) {
      if (rarity.name().equalsIgnoreCase(name)) {
        return rarity;
      }
    }

    return COMUM;
  }
}
