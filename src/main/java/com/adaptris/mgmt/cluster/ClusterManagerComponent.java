package com.adaptris.mgmt.cluster;

import static org.apache.commons.lang3.StringUtils.isEmpty;

import java.util.Properties;

import javax.management.ObjectName;

import org.apache.commons.lang3.StringUtils;

import com.adaptris.core.CoreException;
import com.adaptris.core.management.ManagementComponent;
import com.adaptris.core.util.JmxHelper;
import com.adaptris.core.util.LifecycleHelper;
import com.adaptris.mgmt.cluster.jgroups.AbstractListener;
import com.adaptris.mgmt.cluster.jgroups.Broadcaster;
import com.adaptris.mgmt.cluster.jgroups.JGroupsBroadcaster;
import com.adaptris.mgmt.cluster.jgroups.JGroupsListener;
import com.adaptris.mgmt.cluster.mbean.ClusterManager;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClusterManagerComponent implements ManagementComponent {
  
  private static final String CLUSTER_NAME_KEY = "clusterName";
  
  private static final String CLUSTER_DEBUG_KEY = "clusterDebug";
  
  private static final String CLUSTER_MANAGER_OBJECT_NAME = "com.adaptris:type=ClusterManager,id=ClusterManager";
  
  @Getter
  @Setter
  private ClusterManager clusterManager;
  
  @Getter
  @Setter
  private Broadcaster broadcaster;
  
  @Getter
  @Setter
  private AbstractListener listener;
  
  /**
   * The common name known and configured in the bootstrap.properties by each instance in this cluster.
   */
  @Getter
  @Setter
  private String clusterName;

  public ClusterManagerComponent() {
  }
  
  @Override
  public void init(@NonNull Properties config) throws Exception {
    String configuredClusterName = config.getProperty(CLUSTER_NAME_KEY);
    if(isEmpty(configuredClusterName))
      throw new CoreException("The cluster name has not been set in the bootstrap.properties.\nExample; clusterName = MyCluster");
    
    this.setClusterName(configuredClusterName);
    
    if(this.getBroadcaster() == null) {
      this.setBroadcaster(new JGroupsBroadcaster());
      this.getBroadcaster().setJGroupsClusterName(this.getClusterName());
      this.getBroadcaster().setDebug(new Boolean(StringUtils.defaultIfBlank(config.getProperty(CLUSTER_DEBUG_KEY), "false")));
    }
      
    if(this.getListener() == null) {
      this.setListener(new JGroupsListener());
      this.getListener().setJGroupsClusterName(this.getClusterName());
    }
      
    if(this.getClusterManager() == null) {
      this.setClusterManager(new ClusterManager());
      this.getClusterManager().setDebug(new Boolean(StringUtils.defaultIfBlank(config.getProperty(CLUSTER_DEBUG_KEY), "false"))); 
    }
  }

  @Override
  public void start() throws Exception {
    this.getListener().registerListener(this.getClusterManager());
    JmxHelper.register(new ObjectName(CLUSTER_MANAGER_OBJECT_NAME), this.getClusterManager());
    
    LifecycleHelper.initAndStart(this.getListener());
    LifecycleHelper.initAndStart(this.getBroadcaster());
    
    log.debug(this.getClass().getSimpleName() + " Started");
  }

  @Override
  public void stop() throws Exception {
    LifecycleHelper.stopAndClose(this.getListener());
    LifecycleHelper.stopAndClose(this.getBroadcaster());
    
    log.debug(this.getClass().getSimpleName() + " Stopped");
  }

  @Override
  public void destroy() throws Exception {
    log.debug(this.getClass().getSimpleName() + " Destroyed");
  }
  
}
