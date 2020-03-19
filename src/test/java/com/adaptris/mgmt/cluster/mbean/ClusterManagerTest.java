package com.adaptris.mgmt.cluster.mbean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.adaptris.core.CoreException;
import com.adaptris.core.cache.ExpiringMapCache;
import com.adaptris.mgmt.cluster.ClusterInstance;

public class ClusterManagerTest {
  
  private ClusterManager clusterManager;
  
  @Mock private ExpiringMapCache mockClusterInstances;
  
  private List<String> mockKeySet;
  
  @BeforeEach
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    clusterManager = new ClusterManager();
    
    mockKeySet = new ArrayList<>();
    mockKeySet.add("key");
  }
  
  @Test
  public void testClusterManagerNewInstance() throws Exception {
    clusterManager.clusterInstancePinged(new ClusterInstance(UUID.randomUUID(), "MyAdapterId", "myJmxAddress"));
    
    assertTrue(clusterManager.getKnownClusterInstancesAsString().contains("myJmxAddress"));
  }
  
  @Test
  public void testClusterManagerNewInstanceNotReAdded() throws Exception {
    UUID randomUUID = UUID.randomUUID();
    clusterManager.clusterInstancePinged(new ClusterInstance(randomUUID, "MyAdapterId", "myJmxAddress"));
    clusterManager.clusterInstancePinged(new ClusterInstance(randomUUID, "MyAdapterId", "myJmxAddress"));
    
    System.out.println(clusterManager.getKnownClusterInstancesAsString());
    
    assertTrue(clusterManager.getKnownClusterInstancesAsString().indexOf("myJmxAddress") >= 0);
    assertTrue(clusterManager.getKnownClusterInstancesAsString().indexOf(",") == -1);
  }
  
  @Test
  public void testClusterManagerMultipleNewInstanceNotReAdded() throws Exception {
    clusterManager.setDebug(true);
    UUID randomUUID = UUID.randomUUID();
    
    ClusterInstance clusterInstance1 = new ClusterInstance(randomUUID, "MyAdapterId", "myJmxAddress");
    ClusterInstance clusterInstance2 = new ClusterInstance(randomUUID, "MyAdapterId2", "myJmxAddress2");
    
    clusterManager.clusterInstancePinged(clusterInstance1);
    clusterManager.clusterInstancePinged(clusterInstance2);
    clusterManager.clusterInstancePinged(clusterInstance1);
    clusterManager.clusterInstancePinged(clusterInstance2);
    
    System.out.println(clusterManager.getKnownClusterInstancesAsString());
    
    assertTrue(clusterManager.getKnownClusterInstancesAsString().indexOf("myJmxAddress") >= 0);
    assertTrue(clusterManager.getKnownClusterInstancesAsString().indexOf("myJmxAddress2") >= 0);
  }
  
  @Test
  public void testClusterManagerMultipleNewInstance() throws Exception {
    clusterManager.setDebug(true);
    clusterManager.clusterInstancePinged(new ClusterInstance(UUID.randomUUID(), "MyAdapterId", "myJmxAddress"));
    clusterManager.clusterInstancePinged(new ClusterInstance(UUID.randomUUID(), "MyAdapterId", "myJmxAddress2"));
    
    System.out.println(clusterManager.getKnownClusterInstancesAsString());
    
    assertTrue(clusterManager.getKnownClusterInstancesAsString().indexOf("myJmxAddress") >= 0);
    assertTrue(clusterManager.getKnownClusterInstancesAsString().indexOf(",") > 0);
    assertTrue(clusterManager.getKnownClusterInstancesAsString().indexOf("myJmxAddress2") >= 0);
  }
  
  @Test
  public void testClusterManagerNoMap() throws Exception {
    doThrow(new CoreException("expected"))
        .when(mockClusterInstances).getKeys();
        
    clusterManager.setClusterInstances(mockClusterInstances);
    
    try {
      assertNull(clusterManager.getKnownClusterInstancesAsString());
    } catch (Exception ex) {
      fail("Should return null and fail silently");
    }
  }
  
  @Test
  public void testClusterManagerKeyFails() throws Exception {
    // Should be impossible, but expiring map cache apparently can throw an exception here...
    when(mockClusterInstances.getKeys())
        .thenReturn(mockKeySet);
    doThrow(new CoreException("expected"))
        .when(mockClusterInstances).get(anyString());
    
    clusterManager.setClusterInstances(mockClusterInstances);
    
    try {
      assertEquals("", clusterManager.getKnownClusterInstancesAsString());
    } catch (Exception ex) {
      fail("Should return null and fail silently");
    }
  }

}
