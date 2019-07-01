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
    clusterManager.clusterInstancePinged(new ClusterInstance(UUID.randomUUID(), "myJmxAddress"));
    
    assertTrue(clusterManager.getKnownClusterInstances().contains("myJmxAddress"));
  }
  
  @Test
  public void testClusterManagerNewInstanceNotReAdded() throws Exception {
    UUID randomUUID = UUID.randomUUID();
    clusterManager.clusterInstancePinged(new ClusterInstance(randomUUID, "myJmxAddress"));
    clusterManager.clusterInstancePinged(new ClusterInstance(randomUUID, "myJmxAddress"));
    
    System.out.println(clusterManager.getKnownClusterInstances());
    
    assertTrue(clusterManager.getKnownClusterInstances().indexOf("myJmxAddress") >= 0);
    assertTrue(clusterManager.getKnownClusterInstances().indexOf(",") == -1);
  }
  
  @Test
  public void testClusterManagerMultipleNewInstance() throws Exception {
    clusterManager.clusterInstancePinged(new ClusterInstance(UUID.randomUUID(), "myJmxAddress"));
    clusterManager.clusterInstancePinged(new ClusterInstance(UUID.randomUUID(), "myJmxAddress2"));
    
    System.out.println(clusterManager.getKnownClusterInstances());
    
    assertTrue(clusterManager.getKnownClusterInstances().indexOf("myJmxAddress") >= 0);
    assertTrue(clusterManager.getKnownClusterInstances().indexOf(",") > 0);
    assertTrue(clusterManager.getKnownClusterInstances().indexOf("myJmxAddress2") >= 0);
  }

}
