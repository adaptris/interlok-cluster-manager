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
  
  @Getter
  @Setter
  private String uniqueId;
  
  public ClusterInstance() {
  }
  
  public ClusterInstance(UUID uuid, String uniqueId, String jmxAddress) {
    this.setClusterUuid(uuid);
    this.setJmxAddress(jmxAddress);
    this.setUniqueId(uniqueId);
  }
  
  public String toString() {
    StringBuffer buffer = new StringBuffer();
    buffer.append("[");
    buffer.append("UUID: ");
    buffer.append(this.getClusterUuid().toString());
    buffer.append(", UniqueId: ");
    buffer.append(this.getUniqueId());
    buffer.append(", JMXAddress: ");
    buffer.append(this.getJmxAddress());
    buffer.append("]");
    
    return buffer.toString();
  }
}
