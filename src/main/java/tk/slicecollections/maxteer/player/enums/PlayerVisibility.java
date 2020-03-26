package tk.slicecollections.maxteer.player.enums;

/**
 * @author Maxter
 */
public enum PlayerVisibility {
  TODOS,
  NENHUM;

  public String getInkSack() {
    if (this == TODOS) {
      return "10";
    }

    return "8";
  }

  public String getName() {
    if (this == TODOS) {
      return "§aAtivado";
    }

    return "§cDesativado";
  }

  public PlayerVisibility next() {
    if (this == NENHUM) {
      return TODOS;
    }

    return NENHUM;
  }

  private static final PlayerVisibility[] VALUES = values();

  public static PlayerVisibility getByOrdinal(long ordinal) {
    if (ordinal < 2 && ordinal > -1) {
      return VALUES[(int) ordinal];
    }

    return null;
  }
}
