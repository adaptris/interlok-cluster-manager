package com.adaptris.mgmt.cluster;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Properties;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.adaptris.core.CoreException;
import com.adaptris.mgmt.cluster.jgroups.AbstractListener;
import com.adaptris.mgmt.cluster.jgroups.Broadcaster;
import com.adaptris.mgmt.cluster.mbean.ClusterManager;

public class ClusterManagerComponentTest {
  
  private static final String CLUSTER_NAME = "MyClusterName";
  private static final String CLUSTER_NAME_KEY = "clusterName";
  
  private ClusterManagerComponent clusterManagerComponent;
  
  @Mock private Broadcaster mockBroadcaster;
  @Mock private AbstractListener mockListener;
  @Mock private ClusterManager mockClusterManager;
  
  private Properties bootstrapProperties;
  
  @BeforeEach
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    clusterManagerComponent = new ClusterManagerComponent();
    
    bootstrapProperties = new Properties();
    bootstrapProperties.put(CLUSTER_NAME_KEY, CLUSTER_NAME);
  }
  
  @AfterEach
  public void tearDown() throws Exception {    
  }
  
  /************************************************************************
   *******************   TESTS   ****************************************** 
   ************************************************************************/
  
  @Test
  public void testPickupClusterName() throws Exception {
    clusterManagerComponent.init(bootstrapProperties);
    clusterManagerComponent.start();
    
    assertEquals(CLUSTER_NAME, clusterManagerComponent.getClusterName());
    
    clusterManagerComponent.stop();
    clusterManagerComponent.destroy();
  }
  
  @Test
  public void testNoClusterName() throws Exception {
    try {
      clusterManagerComponent.init(new Properties());
      fail("Should fail with cluster name specified in the properties.");
    } catch (CoreException ex) {
      // expected
    }
  }
  
  @Test
  public void testPreconfiguredNoRecreat() throws Exception {
    clusterManagerComponent.setBroadcaster(mockBroadcaster);
    clusterManagerComponent.setListener(mockListener);
    clusterManagerComponent.setClusterManager(mockClusterManager);
    
    clusterManagerComponent.init(bootstrapProperties);

    assertEquals(mockBroadcaster, clusterManagerComponent.getBroadcaster());
    assertEquals(mockListener, clusterManagerComponent.getListener());
    assertEquals(mockClusterManager, clusterManagerComponent.getClusterManager());
  }
  
  @Test
  public void testNullProperties() throws Exception {
    try {
      clusterManagerComponent.init(null);
      fail("Should fail with cluster name specified in the properties.");
    } catch (NullPointerException ex) {
      // expected
    }
  }
  
  

}
