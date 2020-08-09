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
public class SkyWarsAchievement extends Achievement {

  private SkyWarsReward reward;
  private String icon;
  private String[] stats;
  private int reach;

  public SkyWarsAchievement(SkyWarsReward reward, String id, String name, String desc, int reach, String... stats) {
    super("sw-" + id, name);
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
    return profile.getStats("mCoreSkyWars", this.stats) >= this.reach;
  }

  public ItemStack getIcon(Profile profile) {
    long current = profile.getStats("mCoreSkyWars", this.stats);
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
      new SkyWarsAchievement(new CoinsReward(100), "1k1", "Assassino (Solo)", "&7Abata um total de %reach%\n&7jogadores para receber:\n \n &8• &6100 Coins", 50, "1v1kills"));
    Achievement.addAchievement(
      new SkyWarsAchievement(new CoinsReward(500), "1k2", "Assassino Mestre (Solo)", "&7Abata um total de %reach%\n&7jogadores para receber:\n \n &8• &6500 Coins", 250,
        "1v1kills"));
    Achievement.addAchievement(
      new SkyWarsAchievement(new CoinsReward(250), "1w1", "Vitorioso (Solo)", "&7Vença um total de %reach%\n&7partidas para receber:\n \n &8• &6250 Coins", 50, "1v1wins"));
    Achievement.addAchievement(
      new SkyWarsAchievement(new CoinsReward(1000), "1w2", "Vitorioso Mestre (Solo)", "&7Vença um total de %reach%\n&7partidas para receber:\n \n &8• &61.000 Coins", 200,
        "1v1wins"));
    Achievement.addAchievement(
      new SkyWarsAchievement(new CoinsReward(100), "1p1", "Assistente (Solo)", "&7Consiga um total de %reach%\n&7assistências para receber:\n \n &8• &6100 Coins", 50,
        "1v1assists"));
    Achievement.addAchievement(
      new SkyWarsAchievement(new CoinsReward(500), "1p2", "Assistente Mestre (Solo)", "&7Consiga um total de %reach%\n&7assistências para receber:\n \n &8• &6500 Coins", 250,
        "1v1assists"));
    Achievement.addAchievement(
      new SkyWarsAchievement(new CoinsReward(250), "1g1", "Persistente (Solo)", "&7Jogue um total de %reach%\n&7partidas para receber:\n \n &8• &6250 Coins", 250, "1v1games"));

    Achievement.addAchievement(
      new SkyWarsAchievement(new CoinsReward(100), "2k1", "Assassino (Dupla)", "&7Abata um total de %reach%\n&7jogadores para receber:\n \n &8• &6100 Coins", 50, "2v2kills"));
    Achievement.addAchievement(
      new SkyWarsAchievement(new CoinsReward(500), "2k2", "Assassino Mestre (Dupla)", "&7Abata um total de %reach%\n&7jogadores para receber:\n \n &8• &6500 Coins", 250,
        "2v2kills"));
    Achievement.addAchievement(
      new SkyWarsAchievement(new CoinsReward(250), "2w1", "Vitorioso (Dupla)", "&7Vença um total de %reach%\n&7partidas para receber:\n \n &8• &6250 Coins", 50, "2v2wins"));
    Achievement.addAchievement(
      new SkyWarsAchievement(new CoinsReward(1000), "2w2", "Vitorioso Mestre (Dupla)", "&7Vença um total de %reach%\n&7partidas para receber:\n \n &8• &61.000 Coins", 200,
        "2v2wins"));
    Achievement.addAchievement(
      new SkyWarsAchievement(new CoinsReward(100), "2p1", "Assistente (Dupla)", "&7Consiga um total de %reach%\n&7assistências para receber:\n \n &8• &6100 Coins", 50,
        "2v2assists"));
    Achievement.addAchievement(
      new SkyWarsAchievement(new CoinsReward(500), "2p2", "Assistente Mestre (Dupla)", "&7Consiga um total de %reach%\n&7assistências para receber:\n \n &8• &6500 Coins", 250,
        "2v2assists"));
    Achievement.addAchievement(
      new SkyWarsAchievement(new CoinsReward(250), "2g1", "Persistente (Dupla)", "&7Jogue um total de %reach%\n&7partidas para receber:\n \n &8• &6250 Coins", 250, "2v2games"));

    Achievement.addAchievement(
      new SkyWarsAchievement(new TitleReward("swk"), "tk", "Traidor Celestial", "&7Abata um total de %reach%\n&7jogadores para receber:\n \n &8• &fTítulo: &cAnjo da Morte", 500,
        "1v1kills", "2v2kills"));
    Achievement.addAchievement(
      new SkyWarsAchievement(new TitleReward("sww"), "tw", "Destrono Celestial", "&7Vença um total de %reach%\n&7partidas para receber:\n \n &8• &fTítulo: &bRei Celestial", 400,
        "1v1wins", "2v2wins"));
    Achievement.addAchievement(
      new SkyWarsAchievement(new TitleReward("swa"), "tp", "Anjo Guardião", "&7Consiga um total de %reach%\n&7assistências para receber:\n \n &8• &fTítulo: &6Companheiro de Asas",
        500, "1v1assists", "2v2assists"));
  }

  interface SkyWarsReward {
    void give(Profile profile);
  }

  static class CoinsReward implements SkyWarsReward {
    private double amount;

    public CoinsReward(double amount) {
      this.amount = amount;
    }

    @Override
    public void give(Profile profile) {
      profile.getDataContainer("mCoreSkyWars", "coins").addDouble(this.amount);
    }
  }

  static class TitleReward implements SkyWarsReward {
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
