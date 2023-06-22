package com.adaptris.mgmt.cluster.jgroups;

import org.jgroups.BytesMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adaptris.mgmt.cluster.ClusterInstance;

public class JGroupsNetworkPingSender implements NetworkPingSender {

  protected transient Logger log = LoggerFactory.getLogger(this.getClass().getName());

  @Override
  public void sendData(String host, int port, ClusterInstance data) throws Exception {
    JGroupsChannel jGroupsChannel = JGroupsChannel.getInstance();
    if (jGroupsChannel.getJGroupsChannel().isConnected()) {
      jGroupsChannel.getJGroupsChannel().send(new BytesMessage(null, PacketHelper.createDataPacket(data)));
    } else {
      log.warn("JGroupsChannel not connected, therefore skipping the send.");
    }
  }

}
