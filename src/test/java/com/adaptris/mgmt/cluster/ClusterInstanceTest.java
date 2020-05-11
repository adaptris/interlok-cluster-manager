package com.adaptris.mgmt.cluster;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ClusterInstanceTest {
  
  private static final UUID UNIQUE_ID = UUID.randomUUID();

  private static final String ADAPTER_ID = "MyAdapterId";
  
  private static final String JMX_ADDRESS = "JMX_ADDRESS";
  
  private ClusterInstance clusterInstance;
  
  @BeforeEach
  public void setUp() throws Exception {
    clusterInstance = new ClusterInstance();
  }
  
  /************************************************************************
   *******************   TESTS   ****************************************** 
   ************************************************************************/
  
  @Test
  public void testSetAllConstructor() throws Exception {
    clusterInstance = new ClusterInstance(UNIQUE_ID, ADAPTER_ID, JMX_ADDRESS);
    
    assertEquals(UNIQUE_ID, clusterInstance.getClusterUuid());
    assertEquals(ADAPTER_ID, clusterInstance.getUniqueId());
    assertEquals(JMX_ADDRESS, clusterInstance.getJmxAddress());
  }
  
  @Test
  public void testEquals() throws Exception {
    clusterInstance = new ClusterInstance(UNIQUE_ID, ADAPTER_ID, JMX_ADDRESS);
    
    ClusterInstance clusterInstance2 = new ClusterInstance(UNIQUE_ID, ADAPTER_ID, JMX_ADDRESS);
    
    assertEquals(clusterInstance.toString(), clusterInstance2.toString());
  }
  
  @Test
  public void testNotEquals() throws Exception {
    clusterInstance = new ClusterInstance(UNIQUE_ID, ADAPTER_ID, JMX_ADDRESS);
    
    ClusterInstance clusterInstance2 = new ClusterInstance(UUID.randomUUID(), ADAPTER_ID, JMX_ADDRESS);
    
    assertNotEquals(clusterInstance.toString(), clusterInstance2.toString());
  }
}
