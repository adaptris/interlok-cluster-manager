package com.adaptris.mgmt.cluster.mbean;

import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.adaptris.core.CoreException;
import com.adaptris.core.cache.ExpiringMapCache;
import com.adaptris.mgmt.cluster.ClusterInstance;
import com.adaptris.mgmt.cluster.jgroups.ClusterInstanceEventListener;
import com.adaptris.util.TimeInterval;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.jodah.expiringmap.ExpirationPolicy;

@Slf4j
public class ClusterManager implements ClusterManagerMBean, ClusterInstanceEventListener {
  
  private static final long DEFAULT_EXPIRATION_TIME = 60l; // 60 seconds

  @Getter
  @Setter
  private ExpiringMapCache clusterInstances;
  @Getter
  @Setter
  private boolean debug;
  
  // use a PAssiveExpiryMap
  public ClusterManager() throws CoreException {
    this.setClusterInstances(
        new ExpiringMapCache()
        .withExpiration(new TimeInterval(DEFAULT_EXPIRATION_TIME, TimeUnit.SECONDS))
        .withExpirationPolicy(ExpirationPolicy.CREATED)
        );
    
    this.getClusterInstances().init();
    this.getClusterInstances().start();
  }
  
  @Override
  public String getKnownClusterInstances() {
    try {
      synchronized(this.getClusterInstances()) {
        return this.getClusterInstances().getKeys()
        .stream()
        .map(a -> {
          try {
            return ((ClusterInstance) getClusterInstances().get(a)).getJmxAddress();
          } catch (CoreException e) {
            log.warn("Error accessing our cache of cluster instances.", e);
            return "";
          }
        })
        .collect(Collectors.joining(","));
      }
    } catch (CoreException ex) {
      log.warn("Error accessing our cache of cluster instances.", ex);
      return null;
    }
  }
  
  public void addClusterInstance(ClusterInstance clusterInstance) throws CoreException {
    synchronized(this.getClusterInstances()) {
      if(this.getClusterInstances().get(clusterInstance.getClusterUuid().toString()) == null) {
        this.getClusterInstances().put(clusterInstance.getClusterUuid().toString(), clusterInstance);
        if(this.isDebug())
          log.debug("Found new Cluster Instance: {}", clusterInstance);
      } else {
        // refresh the instance on the cache, so it does not expire
        this.getClusterInstances().remove(clusterInstance.getClusterUuid().toString());
        this.getClusterInstances().put(clusterInstance.getClusterUuid().toString(), clusterInstance);
        if(this.isDebug())
          log.trace("Refreshed Cluster Instance {}", clusterInstance);
      }
    }
  }

  @Override
  public void clusterInstancePinged(ClusterInstance clusterInstance) {
    if(this.isDebug())
      log.trace("Cluster instance pinged: {}", clusterInstance);
    try {
      this.addClusterInstance(clusterInstance);
    } catch (CoreException ex) {
      log.warn("Unable to add cluster instance to our cache {}", clusterInstance, ex);
    }
      
  }

}
