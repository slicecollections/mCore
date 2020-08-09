package tk.slicecollections.maxteer.achievements.types;

import org.bukkit.inventory.ItemStack;
import tk.slicecollections.maxteer.achievements.Achievement;
import tk.slicecollections.maxteer.player.Profile;
import tk.slicecollections.maxteer.titles.Title;
import tk.slicecollections.maxteer.utils.BukkitUtils;
import tk.slicecollections.maxteer.utils.StringUtils;

/**
 * @author Maxter
 */
public class MurderAchievement extends Achievement {

  private MurderReward reward;
  private String icon;
  private String[] stats;
  private int reach;

  public MurderAchievement(MurderReward reward, String id, String name, String desc, int reach, String... stats) {
    super("mm-" + id, name);
    this.reward = reward;
    this.icon = "%material% : 1 : nome>%name% : desc>" + desc + "\n \n&fProgresso: %progress%";
    this.stats = stats;
    this.reach = reach;
  }

  @Override
  protected void give(Profile profile) {
    this.reward.give(profile);
  }

  @Override
  protected boolean check(Profile profile) {
    return profile.getStats("mCoreMurder", this.stats) >= this.reach;
  }

  public ItemStack getIcon(Profile profile) {
    long current = profile.getStats("mCoreMurder", this.stats);
    if (current > this.reach) {
      current = this.reach;
    }

    return BukkitUtils.deserializeItemStack(
      this.icon.replace("%material%", current == this.reach ? "ENCHANTED_BOOK" : "BOOK").replace("%name%", (current == this.reach ? "&a" : "&c") + this.getName())
        .replace("%current%", StringUtils.formatNumber(current)).replace("%reach%", StringUtils.formatNumber(this.reach))
        .replace("%progress%", (current == this.reach ? "&a" : current > this.reach / 2 ? "&7" : "&c") + current + "/" + this.reach));
  }

  public static void setupAchievements() {
    Achievement.addAchievement(
      new MurderAchievement(new CoinsReward(500), "d1", "Investigador", "&7Vença um total de %reach% partidas\n&7como Detetive para receber:\n \n &8• &6500 Coins", 100,
        "cldetectivewins"));
    Achievement.addAchievement(
      new MurderAchievement(new CoinsReward(500), "k2", "Trapper", "&7Vença um total de %reach% partidas\n&7como Assassino para receber:\n \n &8• &6500 Coins", 100,
        "clkillerwins"));
    Achievement.addAchievement(
      new MurderAchievement(new CoinsReward(1500), "d2", "Perito Criminal", "&7Vença um total de %reach% partidas\n&7como Detetive para receber:\n \n &8• &61.500 Coins", 200,
        "cldetectivewins"));
    Achievement.addAchievement(
      new MurderAchievement(new CoinsReward(1500), "k2", "Traidor", "&7Vença um total de %reach% partidas\n&7como Assassino para receber:\n \n &8• &61.500 Coins",
        200, "clkillerwins"));

    Achievement.addAchievement(
      new MurderAchievement(new TitleReward("mmd"), "td", "Detetive", "&7Vença um total de %reach% partidas\n&7como Detetive para receber:\n \n &8• &fTítulo: &6Sherlock Holmes",
        400, "cldetectivewins"));
    Achievement.addAchievement(new MurderAchievement(new TitleReward("mmk"), "tk", "Serial Killer",
      "&7Vença um total de %reach% partidas\n&7como Assassino para receber:\n \n &8• &fTítulo: &4Jeff the Killer", 400, "clkillerwins"));
  }

  interface MurderReward {
    void give(Profile profile);
  }

  static class CoinsReward implements MurderReward {
    private double amount;

    public CoinsReward(double amount) {
      this.amount = amount;
    }

    @Override
    public void give(Profile profile) {
      profile.getDataContainer("mCoreMurder", "coins").addDouble(this.amount);
    }
  }

  static class TitleReward implements MurderReward {
    private String titleId;

    public TitleReward(String titleId) {
      this.titleId = titleId;
    }

    @Override
    public void give(Profile profile) {
      profile.getTitlesContainer().add(Title.getById(this.titleId));
    }
  }
}
