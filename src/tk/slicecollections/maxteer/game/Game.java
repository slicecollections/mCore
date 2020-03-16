package tk.slicecollections.maxteer.game;

import java.util.List;
import org.bukkit.entity.Player;
import tk.slicecollections.maxteer.player.Profile;

public interface Game<T extends GameTeam> {
  
  public void broadcastMessage(String message);
  
  public void broadcastMessage(String message, boolean spectators);
  
  public void join(Profile profile);
  
  public void leave(Profile profile, Game<?> game);
  
  public void kill(Profile profile, Profile killer);
  
  public void killLeave(Profile profile, Profile killer);
  
  public void start();
  
  public void stop(T winners);
  
  public void reset();
  
  public String getGameName();
  
  public GameState getState();
  
  public boolean isSpectator(Player player);
  
  public int getOnline();
  
  public int getMaxPlayers();
  
  public T getTeam(Player player);
  
  public List<T> listTeams();
  
  public List<Player> listPlayers();
  
  public List<Player> listPlayers(boolean spectators);
}
