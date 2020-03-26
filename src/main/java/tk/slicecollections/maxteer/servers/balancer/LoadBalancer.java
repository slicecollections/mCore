package tk.slicecollections.maxteer.servers.balancer;

import tk.slicecollections.maxteer.servers.balancer.elements.LoadBalancerObject;

/**
 * @author Maxter
 */
public interface LoadBalancer<T extends LoadBalancerObject> {

  public T next();
}
