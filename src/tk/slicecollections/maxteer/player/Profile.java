package tk.slicecollections.maxteer.player;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import tk.slicecollections.maxteer.Core;
import tk.slicecollections.maxteer.booster.Booster;
import tk.slicecollections.maxteer.booster.NetworkBooster;
import tk.slicecollections.maxteer.database.Database;
import tk.slicecollections.maxteer.database.data.DataContainer;
import tk.slicecollections.maxteer.database.data.container.AchievementsContainer;
import tk.slicecollections.maxteer.database.data.container.BoostersContainer;
import tk.slicecollections.maxteer.database.data.container.DeliveriesContainer;
import tk.slicecollections.maxteer.database.data.container.PreferencesContainer;
import tk.slicecollections.maxteer.database.data.container.SelectedContainer;
import tk.slicecollections.maxteer.database.data.container.TitlesContainer;
import tk.slicecollections.maxteer.database.data.interfaces.AbstractContainer;
import tk.slicecollections.maxteer.game.Game;
import tk.slicecollections.maxteer.game.GameTeam;
import tk.slicecollections.maxteer.player.enums.PlayerVisibility;
import tk.slicecollections.maxteer.player.hotbar.Hotbar;
import tk.slicecollections.maxteer.player.role.Role;
import tk.slicecollections.maxteer.player.scoreboard.MScoreboard;
import tk.slicecollections.maxteer.titles.TitleManager;
import tk.slicecollections.maxteer.utils.StringUtils;

public class Profile {

  private String name;

  private Game<? extends GameTeam> game;
  private Hotbar hotbar;
  private MScoreboard scoreboard;

  private Map<String, Long> lastHit = new HashMap<>();
  private Map<String, Map<String, DataContainer>> tableMap;

  public Profile(String name) {
    this.name = name;
    this.tableMap = Database.getInstance().load(name);

    this.getDataContainer("mCoreProfile", "lastlogin").set(System.currentTimeMillis());
  }

  public void setGame(Game<? extends GameTeam> game) {
    this.game = game;
    this.lastHit.clear();
    if (this.game != null) {
      TitleManager.leaveLobby(this);
    } else {
      TitleManager.joinLobby(this);
    }
  }

  public void setHit(String name) {
    this.lastHit.put(name, System.currentTimeMillis() + 8000);
  }

  public void setHotbar(Hotbar hotbar) {
    this.hotbar = hotbar;
  }

  public void setScoreboard(MScoreboard scoreboard) {
    this.scoreboard = scoreboard;
  }

  public void update() {
    this.scoreboard.update();
  }

  public void refresh() {
    Player player = getPlayer();
    if (player == null) {
      return;
    }

    player.setMaxHealth(20.0);
    player.setHealth(20.0);
    player.setFoodLevel(20);
    player.setExhaustion(0.0f);
    player.setExp(0.0f);
    player.setLevel(0);
    player.setAllowFlight(false);
    player.closeInventory();
    player.spigot().setCollidesWithEntities(true);
    for (PotionEffect pe : player.getActivePotionEffects()) {
      player.removePotionEffect(pe.getType());
    }

    if (!playingGame()) {
      player.setGameMode(GameMode.ADVENTURE);
      player.teleport(Core.getLobby());

      player.setAllowFlight(player.hasPermission("mcore.fly"));
    }
    this.refreshPlayers();
  }

  public void refreshPlayers() {
    if (this.hotbar != null) {
      this.hotbar.apply(this);
    }

    if (!this.playingGame()) {
      Player player = this.getPlayer();
      Profile.listProfiles().forEach(profile -> {
        Player players = profile.getPlayer();

        if (!playingGame() && !profile.playingGame()) {
          if (this.getPreferencesContainer().getPlayerVisibility() == PlayerVisibility.TODOS) {
            if (!player.canSee(players)) {
              TitleManager.show(this, profile);
            }
            player.showPlayer(players);
          } else {
            if (player.canSee(players)) {
              TitleManager.hide(this, profile);
            }
            player.hidePlayer(players);
          }

          if (profile.getPreferencesContainer().getPlayerVisibility() == PlayerVisibility.TODOS) {
            if (!players.canSee(player)) {
              TitleManager.show(profile, this);
            }
            players.showPlayer(player);
          } else {
            if (players.canSee(player)) {
              TitleManager.hide(profile, this);
            }
            players.hidePlayer(player);
          }
        } else {
          player.hidePlayer(players);
          players.hidePlayer(player);
        }
      });
    }
  }

  public void save() {
    // Salvar o último rank para utilização em funções offline como "LeaderBoards"
    this.getDataContainer("mCoreProfile", "role").set(StringUtils.stripColors(Role.getPlayerRole(this.getPlayer()).getName()));
    Database.getInstance().save(this.name, this.tableMap);
  }

  public void saveSync() {
    // Salvar o último rank para utilização em funções offline como "LeaderBoards"
    this.getDataContainer("mCoreProfile", "role").set(StringUtils.stripColors(Role.getPlayerRole(this.getPlayer()).getName()));
    Database.getInstance().saveSync(this.name, this.tableMap);
  }

  public void destroy() {
    this.name = null;
    this.game = null;
    this.hotbar = null;
    this.scoreboard = null;
    this.lastHit.clear();
    this.lastHit = null;
    this.tableMap.values().stream().forEach(containerMap -> {
      containerMap.values().forEach(DataContainer::gc);
      containerMap.clear();
    });
    this.tableMap.clear();
    this.tableMap = null;
  }

  public String getName() {
    return this.name;
  }

  public boolean isOnline() {
    return isOnline(this.name);
  }

  public Player getPlayer() {
    return Bukkit.getPlayerExact(this.name);
  }

  public Game<?> getGame() {
    return this.getGame(Game.class);
  }

  @SuppressWarnings("unchecked")
  public <T extends Game<?>> T getGame(Class<T> gameClass) {
    return (T) this.game;
  }

  public Hotbar getHotbar() {
    return this.hotbar;
  }

  public boolean playingGame() {
    return this.game != null;
  }

  public List<Profile> getLastHitters() {
    List<Profile> hitters = this.lastHit.entrySet().stream()
        .filter(entry -> isOnline(entry.getKey()))
        .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue()))
        .map(entry -> getProfile(entry.getKey())).collect(Collectors.toList());
    //limpar após uso
    this.lastHit.clear();
    return hitters;
  }

  public MScoreboard getScoreboard() {
    return this.scoreboard;
  }

  public void addStats(String table, String... keys) {
    this.addStats(table, 1, keys);
  }

  public void addStats(String table, long amount, String... keys) {
    for (String key : keys) {
      this.getDataContainer(table, key).addLong(amount);
    }
  }

  public void setStats(String table, long amount, String... keys) {
    for (String key : keys) {
      this.getDataContainer(table, key).set(amount);
    }
  }

  public void updateDailyStats(String table, String date, long amount, String key) {
    long currentExpire = this.getStats(table, date);
    this.setStats(table, System.currentTimeMillis(), date);
    if ((amount == 0 || this.getStats(table, key) > 0) && (!COMPARE_SDF.format(System.currentTimeMillis()).equals(COMPARE_SDF.format(currentExpire)))) {
      this.setStats(table, 0, key);
      return;
    }

    this.addStats(table, amount, key);
  }

  public int addCoins(String table, double amount) {
    this.getDataContainer(table, "coins").addDouble(amount);
    return (int) amount;
  }

  // Com multiplicador
  public int addCoinsWM(String table, double amount) {
    amount = this.calculateWM(amount);
    this.addCoins(table, amount);
    return (int) amount;
  }

  public double calculateWM(double amount) {
    double add = 0.0D;
    String booster = this.getBoostersContainer().getEnabled();
    if (booster != null) {
      add = amount * Double.parseDouble(booster.split(":")[0]);
    }

    NetworkBooster nb = Booster.getNetworkBooster(Core.minigame);
    if (nb != null) {
      add += amount * nb.getMultiplier();
    }

    return add;
  }

  public void removeCoins(String table, double amount) {
    this.getDataContainer(table, "coins").removeDouble(amount);
  }

  public long getStats(String table, String... keys) {
    long stat = 0;
    for (String key : keys) {
      stat += this.getDataContainer(table, key).getAsLong();
    }

    return stat;
  }

  // Resetar diariamente baseado em um Timemillis.
  public long getDailyStats(String table, String date, String key) {
    long currentExpire = this.getStats(table, date);
    if (!COMPARE_SDF.format(System.currentTimeMillis()).equals(COMPARE_SDF.format(currentExpire))) {
      this.setStats(table, 0, key);
    }

    this.setStats(table, System.currentTimeMillis(), date);
    return this.getStats(table, key);
  }

  public double getCoins(String table) {
    return this.getDataContainer(table, "coins").getAsDouble();
  }

  public String getFormatedStats(String table, String... keys) {
    return StringUtils.formatNumber(this.getStats(table, keys));
  }

  public String getFormatedStatsDouble(String table, String key) {
    return StringUtils.formatNumber(this.getDataContainer(table, key).getAsDouble());
  }

  public DeliveriesContainer getDeliveriesContainer() {
    return this.getAbstractContainer("mCoreProfile", "deliveries", DeliveriesContainer.class);
  }
  
  public PreferencesContainer getPreferencesContainer() {
    return this.getAbstractContainer("mCoreProfile", "preferences", PreferencesContainer.class);
  }

  public TitlesContainer getTitlesContainer() {
    return this.getAbstractContainer("mCoreProfile", "titles", TitlesContainer.class);
  }

  public BoostersContainer getBoostersContainer() {
    return this.getAbstractContainer("mCoreProfile", "boosters", BoostersContainer.class);
  }

  public AchievementsContainer getAchievementsContainer() {
    return this.getAbstractContainer("mCoreProfile", "achievements", AchievementsContainer.class);
  }

  public SelectedContainer getSelectedContainer() {
    return this.getAbstractContainer("mCoreProfile", "selected", SelectedContainer.class);
  }

  public DataContainer getDataContainer(String table, String key) {
    return this.tableMap.get(table).get(key);
  }

  public <T extends AbstractContainer> T getAbstractContainer(String table, String key, Class<T> containerClass) {
    return this.getDataContainer(table, key).getContainer(containerClass);
  }

  private static final Map<String, Profile> PROFILES = new HashMap<>();
  private static final SimpleDateFormat COMPARE_SDF = new SimpleDateFormat("yyyy/MM/dd");

  public static Profile createOrLoadProfile(String playerName) {
    Profile profile = PROFILES.get(playerName.toLowerCase());
    if (profile == null) {
      profile = new Profile(playerName);
      PROFILES.put(playerName.toLowerCase(), profile);
    }

    return profile;
  }

  public static Profile loadIfExists(String playerName) {
    Profile profile = PROFILES.get(playerName.toLowerCase());
    if (profile == null) {
      playerName = Database.getInstance().exists(playerName);
      if (playerName != null) {
        profile = new Profile(playerName);
      }
    }

    return profile;
  }

  public static Profile getProfile(String playerName) {
    return PROFILES.get(playerName.toLowerCase());
  }

  public static Profile unloadProfile(String playerName) {
    return PROFILES.remove(playerName.toLowerCase());
  }

  public static boolean isOnline(String playerName) {
    return PROFILES.containsKey(playerName.toLowerCase());
  }

  public static Collection<Profile> listProfiles() {
    return PROFILES.values();
  }
}
