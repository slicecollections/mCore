package tk.slicecollections.maxteer.servers.balancer;

import tk.slicecollections.maxteer.servers.balancer.elements.LoadBalancerObject;

public interface LoadBalancer<T extends LoadBalancerObject> {

  public T next();
}
