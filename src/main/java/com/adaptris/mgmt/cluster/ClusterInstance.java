package com.adaptris.mgmt.cluster;

import java.util.UUID;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode
public class ClusterInstance {

  @Getter
  @Setter
  private UUID clusterUuid;
  
  @Getter
  @Setter
  private String jmxAddress;
  
  public ClusterInstance() {
  }
  
  public ClusterInstance(UUID uuid, String jmxAddress) {
    this.setClusterUuid(uuid);
    this.setJmxAddress(jmxAddress);
  }
  
  public String toString() {
    return "[UUID: " + this.getClusterUuid().toString() + ", JMXAddress: " + this.getJmxAddress() + "]";
  }
}
