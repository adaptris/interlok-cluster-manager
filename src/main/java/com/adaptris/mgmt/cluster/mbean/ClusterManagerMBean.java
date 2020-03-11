package com.adaptris.mgmt.cluster.mbean;

import com.adaptris.core.cache.ExpiringMapCache;

public interface ClusterManagerMBean {

  public String getKnownClusterInstancesAsString();
  
  public ExpiringMapCache getClusterInstances();
  
}
