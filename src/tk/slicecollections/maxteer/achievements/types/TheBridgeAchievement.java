package tk.slicecollections.maxteer.achievements.types;

import org.bukkit.inventory.ItemStack;
import tk.slicecollections.maxteer.achievements.Achievement;
import tk.slicecollections.maxteer.player.Profile;
import tk.slicecollections.maxteer.titles.Title;
import tk.slicecollections.maxteer.utils.BukkitUtils;
import tk.slicecollections.maxteer.utils.StringUtils;

public class TheBridgeAchievement extends Achievement {

  private TheBridgeReward reward;
  private String icon;
  private String[] stats;
  private int reach;

  public TheBridgeAchievement(TheBridgeReward reward, String id, String name, String desc, int reach, String... stats) {
    super("tb-" + id, name);
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
    return profile.getStats("mCoreTheBridge", this.stats) >= this.reach;
  }

  public ItemStack getIcon(Profile profile) {
    long current = profile.getStats("mCoreTheBridge", this.stats);
    if (current > this.reach) {
      current = this.reach;
    }

    return BukkitUtils.deserializeItemStack(this.icon
        .replace("%material%", current == this.reach ? "ENCHANTED_BOOK" : "BOOK")
        .replace("%name%", (current == this.reach ? "&a" : "&c") + this.getName())
        .replace("%current%", StringUtils.formatNumber(current))
        .replace("%reach%", StringUtils.formatNumber(this.reach))
        .replace("%progress%", (current == this.reach ? "&a" : current > this.reach / 2 ? "&7" : "&c") + current + "/" + this.reach));
  }

  public static void setupAchievements() {
    Achievement.addAchievement(new TheBridgeAchievement(new CoinsReward(100),"1k1", "Assassino (Solo)", "&7Abata um total de %reach%\n&7jogadores para receber:\n \n &8• &6100 Coins", 50, "1v1kills"));
    Achievement.addAchievement(new TheBridgeAchievement(new CoinsReward(500), "1k2", "Assassino Mestre (Solo)", "&7Abata um total de %reach%\n&7jogadores para receber:\n \n &8• &6500 Coins", 250, "1v1kills"));
    Achievement.addAchievement(new TheBridgeAchievement(new CoinsReward(250), "1w1", "Vitorioso (Solo)", "&7Vença um total de %reach%\n&7partidas para receber:\n \n &8• &6250 Coins", 50, "1v1wins"));
    Achievement.addAchievement(new TheBridgeAchievement(new CoinsReward(1000), "1w2", "Vitorioso Mestre (Solo)", "&7Vença um total de %reach%\n&7partidas para receber:\n \n &8• &61.000 Coins", 200, "1v1wins"));
    Achievement.addAchievement(new TheBridgeAchievement(new CoinsReward(250), "1p1", "Pontuador (Solo)", "&7Consiga um total de %reach%\n&7pontos para receber:\n \n &8• &6250 Coins", 250, "1v1points"));
    Achievement.addAchievement(new TheBridgeAchievement(new CoinsReward(1000), "1p2", "Pontuador Mestre (Solo)", "&7Consiga um total de %reach%\n&7pontos para receber:\n \n &8• &61.000 Coins", 1000, "1v1points"));
    Achievement.addAchievement(new TheBridgeAchievement(new CoinsReward(250), "1g1", "Persistente (Solo)", "&7Jogue um total de %reach%\n&7partidas para receber:\n \n &8• &6250 Coins", 250, "1v1games"));

    Achievement.addAchievement(new TheBridgeAchievement(new CoinsReward(100), "2k1", "Assassino (Dupla)", "&7Abata um total de %reach%\n&7jogadores para receber:\n \n &8• &6100 Coins", 50, "2v2kills"));
    Achievement.addAchievement(new TheBridgeAchievement(new CoinsReward(500), "2k2", "Assassino Mestre (Dupla)", "&7Abata um total de %reach%\n&7jogadores para receber:\n \n &8• &6500 Coins", 250, "2v2kills"));
    Achievement.addAchievement(new TheBridgeAchievement(new CoinsReward(250), "2w1", "Vitorioso (Dupla)", "&7Vença um total de %reach%\n&7partidas para receber:\n \n &8• &6250 Coins", 50, "2v2wins"));
    Achievement.addAchievement(new TheBridgeAchievement(new CoinsReward(1000), "2w2", "Vitorioso Mestre (Dupla)", "&7Vença um total de %reach%\n&7partidas para receber:\n \n &8• &61.000 Coins", 200, "2v2wins"));
    Achievement.addAchievement(new TheBridgeAchievement(new CoinsReward(250), "2p1", "Pontuador (Dupla)", "&7Consiga um total de %reach%\n&7pontos para receber:\n \n &8• &6250 Coins", 250, "2v2points"));
    Achievement.addAchievement(new TheBridgeAchievement(new CoinsReward(1000), "2p2", "Pontuador Mestre (Dupla)", "&7Consiga um total de %reach%\n&7pontos para receber:\n \n &8• &61.000 Coins", 1000, "2v2points"));
    Achievement.addAchievement(new TheBridgeAchievement(new CoinsReward(250), "2g1", "Persistente (Dupla)", "&7Jogue um total de %reach%\n&7partidas para receber:\n \n &8• &6250 Coins", 250, "2v2games"));
    
    Achievement.addAchievement(new TheBridgeAchievement(new TitleReward("tbk"), "tk", "Assassino das Pontes", "&7Abata um total de %reach%\n&7jogadores para receber:\n \n &8• &fTítulo: &cSentinela da Ponte", 500, "1v1kills", "2v2kills"));
    Achievement.addAchievement(new TheBridgeAchievement(new TitleReward("tbw"), "tw", "Glorioso sobre Pontes", "&7Vença um total de %reach%\n&7partidas para receber:\n \n &8• &fTítulo: &6Líder da Ponte", 400, "1v1wins", "2v2wins"));
    Achievement.addAchievement(new TheBridgeAchievement(new TitleReward("tbp"), "tp", "Maestria em Pontuação", "&7Consiga um total de %reach%\n&7pontos para receber:\n \n &8• &fTítulo: &ePontuador Mestre", 2000, "1v1points", "2v2points"));
  }
  
  interface TheBridgeReward {
    public void give(Profile profile);
  }
  
  static class CoinsReward implements TheBridgeReward {
    private double amount;
    
    public CoinsReward(double amount) {
      this.amount = amount;
    }
    
    @Override
    public void give(Profile profile) {
      profile.getDataContainer("mCoreTheBridge", "coins").addDouble(this.amount);
    }
  }
  
  static class TitleReward implements TheBridgeReward {
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
