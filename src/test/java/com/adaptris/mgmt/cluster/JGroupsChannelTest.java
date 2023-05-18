package com.adaptris.mgmt.cluster;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.jgroups.JChannel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.adaptris.mgmt.cluster.jgroups.JGroupsChannel;

public class JGroupsChannelTest {

  private static final String CLUSTER_NAME = "MyCluster";

  private JGroupsChannel jGroupsChannel;

  @Mock
  private JChannel mockJChannel;

  private AutoCloseable mock;

  @BeforeEach
  public void setUp() throws Exception {
    mock = MockitoAnnotations.openMocks(this);
  }

  @AfterEach
  public void tearDown() throws Exception {
    mock.close();
  }

  /************************************************************************
   ******************* TESTS ******************************************
   ************************************************************************/

  @Test
  public void testSingleton() throws Exception {
    jGroupsChannel = JGroupsChannel.getInstance();

    assertNotNull(jGroupsChannel.getJGroupsChannel());
  }

  @Test
  public void testSingletonJChannelNotRecreated() throws Exception {
    jGroupsChannel = JGroupsChannel.getInstance();
    JChannel secondCallChannel = JGroupsChannel.getInstance().getJGroupsChannel();

    assertTrue(jGroupsChannel.getJGroupsChannel() == secondCallChannel);
  }

  @Test
  public void testStartUp() throws Exception {
    jGroupsChannel = JGroupsChannel.getInstance();
    jGroupsChannel.setJGroupsChannel(mockJChannel);
    jGroupsChannel.setClusterName(CLUSTER_NAME);

    jGroupsChannel.start();

    verify(mockJChannel).connect(CLUSTER_NAME);
  }

  @Test
  public void testShutDown() throws Exception {
    when(mockJChannel.isConnected())
        .thenReturn(true);

    jGroupsChannel = JGroupsChannel.getInstance();
    jGroupsChannel.setJGroupsChannel(mockJChannel);

    jGroupsChannel.stop();

    verify(mockJChannel).disconnect();
    verify(mockJChannel).close();
  }

  @Test
  public void testShutdownSilently() throws Exception {
    when(mockJChannel.isConnected())
        .thenReturn(false);

    jGroupsChannel = JGroupsChannel.getInstance();
    jGroupsChannel.setJGroupsChannel(mockJChannel);

    jGroupsChannel.stop();

    verify(mockJChannel, times(0)).disconnect();
    verify(mockJChannel, times(0)).close();
  }

}
