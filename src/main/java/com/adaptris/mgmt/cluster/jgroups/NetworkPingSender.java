package com.adaptris.mgmt.cluster.jgroups;

import com.adaptris.mgmt.cluster.ClusterInstance;

public interface NetworkPingSender {
    
  public void sendData(String host, int port, ClusterInstance data) throws Exception;
    
}
