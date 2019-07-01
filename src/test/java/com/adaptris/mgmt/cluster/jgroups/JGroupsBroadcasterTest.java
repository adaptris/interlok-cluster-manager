package com.adaptris.mgmt.cluster.jgroups;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import java.util.Properties;
import java.util.UUID;

import org.jgroups.JChannel;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.adaptris.core.CoreException;
import com.adaptris.core.management.Constants;
import com.adaptris.core.management.jmx.JmxRemoteComponent;
import com.adaptris.mgmt.cluster.ClusterInstance;

public class JGroupsBroadcasterTest {

  private static final String CLUSTER_NAME = "myClusterName";
  private static final String HOST = null;
  private static final int HOST_PORT = 0;
  
  @Mock
  private NetworkPingSender mockNetworkPingSender;
  @Mock
  private JChannel mockJChannel;
  
  private JGroupsBroadcaster broadcaster;
  private ClusterInstance mockClusterInstance;

  private static JmxRemoteComponent jmxRemoteComponent;
  
  @BeforeAll
  public static void globalSetup() throws Exception {
    Properties properties = new Properties();
    properties.put(Constants.CFG_KEY_JMX_SERVICE_URL_KEY, "service:jmx:jmxmp://localhost:5555");
    
    jmxRemoteComponent = new JmxRemoteComponent();
    jmxRemoteComponent.init(properties);
    jmxRemoteComponent.start();
  }
  
  @AfterAll
  public static void globalTearDown() throws Exception {
    jmxRemoteComponent.stop();
    jmxRemoteComponent.destroy();
  }
  
  @BeforeEach
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    
    JGroupsChannel.getInstance().setJGroupsChannel(mockJChannel);
    
    broadcaster = new JGroupsBroadcaster();
    broadcaster.setNetworkPingSender(mockNetworkPingSender);
    broadcaster.setJGroupsClusterName(CLUSTER_NAME);
    
    mockClusterInstance = new ClusterInstance();
    mockClusterInstance.setClusterUuid(UUID.randomUUID());
    mockClusterInstance.setJmxAddress("myJmxAddress");
  }
  
  @AfterEach
  public void tearDown() throws Exception {
    broadcaster.stop();
  }

  @Test
  public void testSendClusterInstancePing() throws Exception {
    broadcaster.setPingData(mockClusterInstance);
    broadcaster.start();
    
    Thread.sleep(1000); // wait for the first ping
    
    verify(mockNetworkPingSender).sendData(HOST, HOST_PORT, mockClusterInstance);
  }
  
  @Test
  public void testSendGeneratedClusterInstancePing() throws Exception {
    broadcaster.start();
    
    Thread.sleep(11000); // first ping will be sent after 10 seconds
    
    verify(mockNetworkPingSender).sendData(eq(HOST), eq(HOST_PORT), any());
  }
  
  @Test
  public void testSendFailsToJoinCluster() throws Exception {
    doThrow(new CoreException("Expected"))
        .when(mockJChannel).connect(CLUSTER_NAME);
    broadcaster.setPingData(mockClusterInstance);
    try {
      broadcaster.start();
      fail("Startup should throw a core exception.");
    } catch (CoreException ex) {
      //expected
    }
  }
  
}
