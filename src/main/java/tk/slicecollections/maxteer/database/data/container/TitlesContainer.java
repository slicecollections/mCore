package tk.slicecollections.maxteer.database.data.container;

import org.json.simple.JSONArray;
import tk.slicecollections.maxteer.database.data.DataContainer;
import tk.slicecollections.maxteer.database.data.interfaces.AbstractContainer;
import tk.slicecollections.maxteer.titles.Title;

/**
 * @author Maxter
 */
@SuppressWarnings("unchecked")
public class TitlesContainer extends AbstractContainer {

  public TitlesContainer(DataContainer dataContainer) {
    super(dataContainer);
  }

  public void add(Title title) {
    JSONArray titles = this.dataContainer.getAsJsonArray();
    titles.add(title.getId());
    this.dataContainer.set(titles.toString());
    titles.clear();
  }

  public boolean has(Title title) {
    return this.dataContainer.getAsJsonArray().contains(title.getId());
  }
}
