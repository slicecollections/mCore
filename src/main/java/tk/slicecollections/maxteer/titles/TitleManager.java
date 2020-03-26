package tk.slicecollections.maxteer.titles;

import tk.slicecollections.maxteer.player.Profile;
import tk.slicecollections.maxteer.titles.interfaces.AbstractTitleController;

/**
 * @author Maxter
 */
public abstract class TitleManager<T extends AbstractTitleController> {

  public abstract void onJoinLobby(Profile profile);

  public abstract void onLeaveLobby(Profile profile);

  public abstract void onLeaveServer(Profile profile);

  public abstract void onLobbyShow(Profile profile, Profile target);

  public abstract void onLobbyHide(Profile profile, Profile target);

  public abstract void onSelectTitle(Profile profile, Title title);

  public abstract void onDeselectTitle(Profile profile);

  public abstract T getTitleController(Profile profile);

  private static TitleManager<? extends AbstractTitleController> titleManager;

  public static void setTitleManager(TitleManager<? extends AbstractTitleController> tm) {
    titleManager = tm;
  }

  public static void joinLobby(Profile profile) {
    if (titleManager != null) {
      titleManager.onJoinLobby(profile);
    }
  }

  public static void leaveLobby(Profile profile) {
    if (titleManager != null) {
      titleManager.onLeaveLobby(profile);
    }
  }

  public static void leaveServer(Profile profile) {
    if (titleManager != null) {
      titleManager.onLeaveServer(profile);
    }
  }

  public static void show(Profile profile, Profile target) {
    if (titleManager != null) {
      titleManager.onLobbyShow(profile, target);
    }
  }

  public static void hide(Profile profile, Profile target) {
    if (titleManager != null) {
      titleManager.onLobbyHide(profile, target);
    }
  }

  public static void select(Profile profile, Title title) {
    if (titleManager != null) {
      titleManager.onSelectTitle(profile, title);
    }
  }

  public static void deselect(Profile profile) {
    if (titleManager != null) {
      titleManager.onDeselectTitle(profile);
    }
  }
}
