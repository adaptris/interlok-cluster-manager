package com.adaptris.mgmt.cluster.jgroups;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.jgroups.JChannel;
import org.jgroups.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.adaptris.mgmt.cluster.ClusterInstance;

public class JGroupsNetworkPingSenderTest {
  
  private JGroupsNetworkPingSender pingSender;
  
  @Mock
  private JChannel mockJChannel;
  
  @BeforeEach
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    
    JGroupsChannel.getInstance().setJGroupsChannel(mockJChannel);
    pingSender = new JGroupsNetworkPingSender();
  }
  
  @Test
  public void testNotConnected() throws Exception {
    pingSender.sendData("someHost", 999, new ClusterInstance(UUID.randomUUID(), "MyAdapterId", "myJmxAddress"));
    
    verify(mockJChannel, times(0)).send(any(Message.class));
  }
  
  @Test
  public void testIsConnected() throws Exception {
    when(mockJChannel.isConnected())
        .thenReturn(true);
    pingSender.sendData("someHost", 999, new ClusterInstance(UUID.randomUUID(), "MyAdapterId", "myJmxAddress"));
    
    verify(mockJChannel, times(1)).send(any(Message.class));
  }

}
