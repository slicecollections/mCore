package tk.slicecollections.maxteer.player.hotbar;

public class HotbarButton {
  
  private int slot;
  private HotbarAction action;
  private String icon;
  
  public HotbarButton(int slot, HotbarAction action, String icon) {
    this.slot = slot;
    this.action = action;
    this.icon = icon;
  }
  
  public int getSlot() {
    return this.slot - 1;
  }
  
  public HotbarAction getAction() {
    return this.action;
  }
  
  public String getIcon() {
    return this.icon;
  }
}