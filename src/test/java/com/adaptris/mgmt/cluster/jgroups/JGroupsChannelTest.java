package com.adaptris.mgmt.cluster.jgroups;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.jgroups.JChannel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class JGroupsChannelTest {
    
  @BeforeEach
  public void setUp() throws Exception {
  }
  
  @AfterEach
  public void tearDown() throws Exception {
    JGroupsChannel.clear();
  }

  @Test
  public void testLoadsConfiguration() throws Exception {
    assertNotNull(JGroupsChannel.getInstance().getJGroupsChannel());
  }
  
  @Test
  public void testNoReloadOfConfiguration() throws Exception {
    JChannel jGroupsChannel = JGroupsChannel.getInstance().getJGroupsChannel();
    
    assertEquals(jGroupsChannel, JGroupsChannel.getInstance().getJGroupsChannel());
  }
  
  @Test
  public void testNoReloadOfOverrideConfiguration() throws Exception {
    JChannel jGroupsChannel = JGroupsChannel.getInstance("cluster-manager.xml").getJGroupsChannel();
    
    assertEquals(jGroupsChannel, JGroupsChannel.getInstance().getJGroupsChannel());
  }
  
}
