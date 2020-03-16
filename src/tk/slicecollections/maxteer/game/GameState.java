package tk.slicecollections.maxteer.game;

public enum GameState {
  AGUARDANDO, INICIANDO, EMJOGO, ENCERRADO;
  
  public boolean canJoin() {
    if (this == AGUARDANDO) {
      return true;
    }
    
    return false;
  }
}
