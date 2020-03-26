package tk.slicecollections.maxteer.game;

/**
 * @author Maxter
 */
public enum GameState {
  AGUARDANDO, INICIANDO, EMJOGO, ENCERRADO;
  
  public boolean canJoin() {
    return this == AGUARDANDO;
  }
}
