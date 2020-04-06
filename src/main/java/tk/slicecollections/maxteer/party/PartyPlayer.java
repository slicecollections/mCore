package tk.slicecollections.maxteer.party;

import tk.slicecollections.maxteer.Manager;

/**
 * @author Maxter
 */
public class PartyPlayer {

  private String name;
  private PartyRole role;

  public PartyPlayer(String name, PartyRole role) {
    this.name = name;
    this.role = role;
  }

  public void sendMessage(String message) {
    Object player = Manager.getPlayer(name);
    if (player != null) {
      Manager.sendMessage(player, message);
    }
  }

  public void setRole(PartyRole role) {
    this.role = role;
  }

  public String getName() {
    return this.name;
  }

  public PartyRole getRole() {
    return this.role;
  }

  public boolean isOnline() {
    return Manager.getPlayer(this.name) != null;
  }
}
