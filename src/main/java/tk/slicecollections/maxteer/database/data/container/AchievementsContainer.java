package tk.slicecollections.maxteer.database.data.container;

import org.json.simple.JSONArray;
import tk.slicecollections.maxteer.achievements.Achievement;
import tk.slicecollections.maxteer.database.data.DataContainer;
import tk.slicecollections.maxteer.database.data.interfaces.AbstractContainer;

/**
 * @author Maxter
 */
@SuppressWarnings("unchecked")
public class AchievementsContainer extends AbstractContainer {

  public AchievementsContainer(DataContainer dataContainer) {
    super(dataContainer);
  }

  public void complete(Achievement achievement) {
    JSONArray achievements = this.dataContainer.getAsJsonArray();
    achievements.add(achievement.getId());
    this.dataContainer.set(achievements.toString());
    achievements.clear();
  }

  public boolean isCompleted(Achievement achievement) {
    return this.dataContainer.getAsJsonArray().contains(achievement.getId());
  }
}
