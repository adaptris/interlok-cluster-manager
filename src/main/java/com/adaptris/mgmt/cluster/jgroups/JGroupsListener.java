package com.adaptris.mgmt.cluster.jgroups;

import org.jgroups.Message;
import org.jgroups.Receiver;

import com.adaptris.core.CoreException;
import com.adaptris.mgmt.cluster.ClusterInstance;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JGroupsListener extends AbstractListener implements Receiver {
  
  public JGroupsListener() {
    super();
  }

  @Override
  public void start() throws CoreException {
    JGroupsChannel jGroupsChannel = JGroupsChannel.getInstance();
    jGroupsChannel.setClusterName(this.getJGroupsClusterName());
    
    try {
      jGroupsChannel.getJGroupsChannel().setReceiver(this);
      jGroupsChannel.start();
    } catch (Exception ex) {
      throw new CoreException(ex);
    }
  }
  
  @Override
  public void receive(Message msg) {    
    int byteCount = msg.getLength();
    if(byteCount != PacketHelper.STANDARD_PACKET_SIZE) {
      log.warn("Incorrect packet size ({}) received on the TCP socket, ignoring this data stream.", byteCount);
    } else {
      try {
        sendPingEvent(msg.getBuffer());
      } catch (Exception ex) {
        log.error("Unable to handle cluster instance ping.", ex);
      }
    }
  }


  private void sendPingEvent(byte[] data) throws Exception {
    ClusterInstance pingRecord = PacketHelper.createPingRecord(data);
    this.notifyListeners(pingRecord);
  }
  
  @Override
  public void stop() {
    JGroupsChannel.getInstance().stop();
  }

}
