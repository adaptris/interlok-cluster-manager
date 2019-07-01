package com.adaptris.mgmt.cluster.jgroups;

import com.adaptris.mgmt.cluster.ClusterInstance;

public interface ClusterInstanceEventListener {
  
  public void clusterInstancePinged(ClusterInstance clusterInstance);
  
}
