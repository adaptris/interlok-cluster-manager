package com.adaptris.mgmt.cluster.jgroups;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.UUID;

import org.jgroups.JChannel;
import org.jgroups.Message;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.adaptris.core.CoreException;
import com.adaptris.mgmt.cluster.ClusterInstance;

public class JGroupsListenerTest {
    
  private static final String CLUSTER_NAME = "myClusterName";
  
  private ClusterInstance clusterInstance;
    
  private JGroupsListener jGroupsListener;
  @Mock
  private ClusterInstanceEventListener mockPingEventListener;
  @Mock
  private JChannel mockJChannel;
  
  @BeforeEach
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    
    JGroupsChannel.getInstance().setJGroupsChannel(mockJChannel);
    
    jGroupsListener = new JGroupsListener();
    jGroupsListener.setJGroupsClusterName(CLUSTER_NAME);
        
    clusterInstance = new ClusterInstance();
    clusterInstance.setClusterUuid(UUID.randomUUID());
    clusterInstance.setUniqueId("MyUniqueId");
    clusterInstance.setJmxAddress("myJmxAddress");
    
  }
  
  @AfterEach
  public void tearDown() throws Exception {
    jGroupsListener.deregisterListener(mockPingEventListener);
    jGroupsListener.stop();
  }
  
  @Test
  public void testReceiveMasterPing() throws Exception {
    jGroupsListener.registerListener(mockPingEventListener);
    jGroupsListener.start();
    
    jGroupsListener.receive(new Message(null, PacketHelper.createDataPacket(clusterInstance)));
    
    Thread.sleep(3000);
    
    verify(mockPingEventListener).clusterInstancePinged(any(ClusterInstance.class));
  }
  
  @Test
  public void testStartupFails() throws Exception {
    doThrow(new CoreException("Expected"))
        .when(mockJChannel).connect(CLUSTER_NAME);
    try {
      jGroupsListener.start();
      fail("Startup should throw a core exception.");
    } catch (CoreException ex) {
      //expected
    }
  }
  
  @Test
  public void testIncorrectPacketReceived() throws Exception {
    jGroupsListener.registerListener(mockPingEventListener);
    jGroupsListener.start();
    
    jGroupsListener.receive(new Message(null, new byte[1024]));
    
    Thread.sleep(1000);
    
    verify(mockPingEventListener, times(0)).clusterInstancePinged(any(ClusterInstance.class));
  }
}
