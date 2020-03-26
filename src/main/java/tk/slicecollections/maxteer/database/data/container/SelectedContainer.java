package tk.slicecollections.maxteer.database.data.container;

import org.json.simple.JSONObject;
import tk.slicecollections.maxteer.database.data.DataContainer;
import tk.slicecollections.maxteer.database.data.interfaces.AbstractContainer;
import tk.slicecollections.maxteer.titles.Title;

/**
 * @author Maxter
 */
@SuppressWarnings("unchecked")
public class SelectedContainer extends AbstractContainer {

  public SelectedContainer(DataContainer dataContainer) {
    super(dataContainer);
  }

  public void setTitle(String id) {
    JSONObject selected = this.dataContainer.getAsJsonObject();
    selected.put("title", id);
    this.dataContainer.set(selected.toString());
    selected.clear();
  }

  public void setIcon(String id) {
    JSONObject selected = this.dataContainer.getAsJsonObject();
    selected.put("icon", id);
    this.dataContainer.set(selected.toString());
    selected.clear();
  }

  public Title getTitle() {
    return Title.getById(this.dataContainer.getAsJsonObject().get("title").toString());
  }
}
