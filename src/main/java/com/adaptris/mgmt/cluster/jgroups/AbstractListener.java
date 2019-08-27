package com.adaptris.mgmt.cluster.jgroups;

import java.util.ArrayList;
import java.util.List;

import com.adaptris.core.ComponentLifecycle;
import com.adaptris.mgmt.cluster.ClusterInstance;

import lombok.Getter;
import lombok.Setter;

public abstract class AbstractListener implements ComponentLifecycle{
  
  @Getter
  @Setter
  private String jGroupsClusterName;
    
  @Getter
  private List<ClusterInstanceEventListener> listeners;
  
  public AbstractListener() {
    listeners = new ArrayList<ClusterInstanceEventListener>();
  }
  
  public void registerListener(ClusterInstanceEventListener eventListener) {
    this.listeners.add(eventListener);
  }

  public void deregisterListener(ClusterInstanceEventListener eventListener) {
    this.listeners.remove(eventListener);
  }
  
  protected void notifyListeners(ClusterInstance pingData) {
    for(ClusterInstanceEventListener listener : this.getListeners())
      listener.clusterInstancePinged(pingData);
  }
}
