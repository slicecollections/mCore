package tk.slicecollections.maxteer.titles;

import org.bukkit.inventory.ItemStack;
import tk.slicecollections.maxteer.player.Profile;
import tk.slicecollections.maxteer.utils.BukkitUtils;
import tk.slicecollections.maxteer.utils.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Maxter
 */
public class Title {

  private String id;
  private String icon;
  private String title;

  public Title(String id, String title, String desc) {
    this.id = id;
    this.icon = "%material%:%durability% : 1 : esconder>tudo : nome>%name% : desc>&fTítulo: " + title + "\n \n" + desc + "\n \n%action%";
    this.title = title;
  }

  public String getId() {
    return this.id;
  }

  public String getTitle() {
    return this.title;
  }

  public void give(Profile profile) {
    if (!this.has(profile)) {
      profile.getTitlesContainer().add(this);
    }
  }

  public boolean has(Profile profile) {
    return profile.getTitlesContainer().has(this);
  }

  public ItemStack getIcon(Profile profile) {
    boolean has = this.has(profile);
    Title selected = profile.getSelectedContainer().getTitle();

    return BukkitUtils.deserializeItemStack(
      this.icon.replace("%material%", has ? (selected != null && selected.equals(this)) ? "MAP" : "EMPTY_MAP" : "STAINED_GLASS_PANE").replace("%durability%", has ? "0" : "14")
        .replace("%name%", (has ? "&a" : "&c") + StringUtils.stripColors(this.title))
        .replace("%action%", (has ? (selected != null && selected.equals(this)) ? "&eClique para deselecionar!" : "&eClique para selecionar!" : "&cVocê não possui este título.")));
  }

  private static final List<Title> TITLES = new ArrayList<>();

  public static void setupTitles() {
    TITLES.add(new Title("tbk", "§cSentinela da Ponte", "&8Pode ser desbloqueado através do\n&8Desafio \"Assassino das Pontes\"&8."));
    TITLES.add(new Title("tbw", "§6Líder da Ponte", "&8Pode ser desbloqueado através do\n&8Desafio \"Glorioso sobre Pontes\"&8."));
    TITLES.add(new Title("tbp", "§ePontuador Mestre", "&8Pode ser desbloqueado através do\n&8Desafio \"Maestria em Pontuação\"&8."));

    TITLES.add(new Title("swk", "§cAnjo da Morte", "&8Pode ser desbloqueado através do\n&8Desafio \"Traidor Celestial\"&8."));
    TITLES.add(new Title("sww", "§bRei Celestial", "&8Pode ser desbloqueado através do\n&8Desafio \"Destrono Celestial\"&8."));
    TITLES.add(new Title("swa", "§6Companheiro de Asas", "&8Pode ser desbloqueado através do\n&8Desafio \"Anjo Guardião\"&8."));
  }

  public static Title getById(String id) {
    return TITLES.stream().filter(title -> title.getId().equals(id)).findFirst().orElse(null);
  }

  public static Collection<Title> listTitles() {
    return TITLES;
  }
}
