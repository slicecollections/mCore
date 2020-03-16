package tk.slicecollections.maxteer.player.enums;

public enum ProtectionLobby {
  ATIVADO,
  DESATIVADO;
  
  public String getInkSack() {
    if (this == ATIVADO) {
      return "10";
    }
    
    return "8";
  }
  
  public String getName() {
    if (this == ATIVADO) {
      return "§aAtivado";
    }

    return "§cDesativado";
  }
  
  public ProtectionLobby next() {
    if (this == DESATIVADO) {
      return ATIVADO;
    }
    
    return DESATIVADO;
  }
  
  private static final ProtectionLobby[] VALUES = values();
  
  public static ProtectionLobby getByOrdinal(long ordinal) {
    if (ordinal < 2 && ordinal > -1) {
      return VALUES[(int) ordinal];
    }
    
    return null;
  }
}
