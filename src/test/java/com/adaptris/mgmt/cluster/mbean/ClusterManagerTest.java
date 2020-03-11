package com.adaptris.mgmt.cluster.mbean;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.adaptris.mgmt.cluster.ClusterInstance;

public class ClusterManagerTest {
  
  private ClusterManager clusterManager;
  
  @BeforeEach
  public void setUp() throws Exception {
    clusterManager = new ClusterManager();
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
  public void testClusterManagerMultipleNewInstance() throws Exception {
    clusterManager.setDebug(true);
    clusterManager.clusterInstancePinged(new ClusterInstance(UUID.randomUUID(), "MyAdapterId", "myJmxAddress"));
    clusterManager.clusterInstancePinged(new ClusterInstance(UUID.randomUUID(), "MyAdapterId", "myJmxAddress2"));
    
    System.out.println(clusterManager.getKnownClusterInstancesAsString());
    
    assertTrue(clusterManager.getKnownClusterInstancesAsString().indexOf("myJmxAddress") >= 0);
    assertTrue(clusterManager.getKnownClusterInstancesAsString().indexOf(",") > 0);
    assertTrue(clusterManager.getKnownClusterInstancesAsString().indexOf("myJmxAddress2") >= 0);
  }

}
