package com.adaptris.mgmt.cluster.jgroups;

import com.adaptris.core.ComponentLifecycle;
import com.adaptris.mgmt.cluster.ClusterInstance;

public interface Broadcaster extends ComponentLifecycle {
   
   public void setPingData(ClusterInstance data);

   public void setSendDelaySeconds(int parseInt);
   
   public void setJGroupsClusterName(String clusterName);
   
   public void setDebug(boolean debug);
   
   public void setJGroupsConfiguration(String resource);
  
}
