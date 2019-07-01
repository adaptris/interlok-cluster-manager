package com.adaptris.mgmt.cluster.mbean;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.adaptris.mgmt.cluster.ClusterInstance;
import com.adaptris.mgmt.cluster.jgroups.ClusterInstanceEventListener;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClusterManager implements ClusterManagerMBean, ClusterInstanceEventListener {

  @Getter
  @Setter
  private List<ClusterInstance> clusterInstances;
  
  public ClusterManager() {
    this.setClusterInstances(new ArrayList<ClusterInstance>());
  }
  
  @Override
  public String getKnownClusterInstances() {
    return this.getClusterInstances()
        .stream()
        .map(a -> a.getJmxAddress())
        .collect(Collectors.joining(","));
  }
  
  public void addClusterInstance(ClusterInstance clusterInstance) {
    if(!this.getClusterInstances().contains(clusterInstance)) {
      this.getClusterInstances().add(clusterInstance);
      log.debug("Found new Cluster Instance: {}", clusterInstance);
    }
  }

  @Override
  public void clusterInstancePinged(ClusterInstance clusterInstance) {
    log.trace("Cluster instance pinged: {}", clusterInstance);
    this.addClusterInstance(clusterInstance);
  }

}
