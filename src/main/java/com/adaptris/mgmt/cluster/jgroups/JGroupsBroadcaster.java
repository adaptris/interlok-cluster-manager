package com.adaptris.mgmt.cluster.jgroups;

import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import javax.management.ObjectName;

import org.apache.commons.lang3.StringUtils;

import com.adaptris.core.CoreException;
import com.adaptris.core.util.JmxHelper;
import com.adaptris.mgmt.cluster.ClusterInstance;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JGroupsBroadcaster implements Broadcaster {
    
  private static final int DEFAULT_SEND_DELAY_SECONDS = 10;
  
  private static final String CONNECTOR_SERVER = "com.adaptris:type=JmxConnectorServer";
  
  private static final String CONNECTOR_SERVER_ADDRESS = "Address";
  
  private ScheduledExecutorService scheduler;
  private ScheduledFuture<?> schedulerHandle;
  @Getter
  @Setter
  private ClusterInstance pingData;
  @Getter
  @Setter
  private int sendDelaySeconds;
  @Getter
  @Setter
  private NetworkPingSender networkPingSender;
  @Getter
  @Setter
  private String jGroupsClusterName;

  public JGroupsBroadcaster() {
    this.setSendDelaySeconds(DEFAULT_SEND_DELAY_SECONDS);
    this.setNetworkPingSender(new JGroupsNetworkPingSender());
  }
  
  @Override
  public void start() throws CoreException {
    JGroupsChannel jGroupsChannel = JGroupsChannel.getInstance();
    jGroupsChannel.setClusterName(this.getJGroupsClusterName());
    try {
      jGroupsChannel.start();
    } catch (Exception ex) {
      throw new CoreException(ex);
    }
    
    scheduler = Executors.newScheduledThreadPool(1, new ThreadFactory() {
      @Override
      public Thread newThread(Runnable runnable) {
        return new Thread(runnable, "Cluster Broadcast Thread");
      }
    });

    final Runnable broadcastRunnable = new Runnable() {
      @Override
      public void run() {
        try {
          if(getPingData() == null)
            setPingData(generateMyPingData());
          else
            getNetworkPingSender().sendData(null, 0, getPingData());
        } catch (Exception e) {
          log.warn("Error trying to broadcast to others in the cluster; continuing.", e);
        }
      }
    };

    this.schedulerHandle = this.scheduler.scheduleWithFixedDelay(broadcastRunnable, 0, this.getSendDelaySeconds(), TimeUnit.SECONDS);
  }

  @Override
  public void stop() {
    if (schedulerHandle != null) {
      this.schedulerHandle.cancel(true);
      scheduler.shutdownNow();
    }
    JGroupsChannel.getInstance().stop();
  }

  private ClusterInstance generateMyPingData() throws Exception {
    String myJmxAddress = JmxHelper.findMBeanServer().getAttribute(new ObjectName(CONNECTOR_SERVER), CONNECTOR_SERVER_ADDRESS).toString();
    if(StringUtils.isEmpty(myJmxAddress)) {
      log.warn("Attempt to connect to the JMXConnector has failed.  Perhaps it has not yet started.  Continuing and will retry.");
      return null;
    }
    
    ClusterInstance clusterInstance = new ClusterInstance(UUID.randomUUID(), myJmxAddress);
    log.debug("Broadcasting my cluster instance as: {}", clusterInstance);
    return clusterInstance;
  }

  
}
