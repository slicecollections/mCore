package tk.slicecollections.maxteer.servers.balancer;

import tk.slicecollections.maxteer.servers.ServerItem;
import tk.slicecollections.maxteer.servers.balancer.elements.LoadBalancerObject;
import tk.slicecollections.maxteer.servers.balancer.elements.NumberConnection;

/**
 * @author Maxter
 */
public class Server implements LoadBalancerObject, NumberConnection {

  private String name;
  private int max;

  public Server(String name, int max) {
    this.name = name;
    this.max = max;
  }

  public String getName() {
    return this.name;
  }

  @Override
  public int getActualNumber() {
    return ServerItem.getServerCount(this.name);
  }

  @Override
  public int getMaxNumber() {
    return this.max;
  }

  @Override
  public boolean canBeSelected() {
    return this.getActualNumber() < this.max;
  }
}
