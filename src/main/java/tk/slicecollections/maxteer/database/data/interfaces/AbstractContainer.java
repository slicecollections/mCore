package tk.slicecollections.maxteer.database.data.interfaces;

import tk.slicecollections.maxteer.database.data.DataContainer;

/**
 * @author Maxter
 */
public abstract class AbstractContainer {

  protected DataContainer dataContainer;

  public AbstractContainer(DataContainer dataContainer) {
    this.dataContainer = dataContainer;
  }

  public void gc() {
    this.dataContainer = null;
  }
}
