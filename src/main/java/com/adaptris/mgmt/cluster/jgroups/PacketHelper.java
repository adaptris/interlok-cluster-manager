package com.adaptris.mgmt.cluster.jgroups;

import java.nio.ByteBuffer;
import java.util.UUID;

import com.adaptris.mgmt.cluster.ClusterInstance;

public class PacketHelper {
    
  public static final int STANDARD_PACKET_SIZE = 280;
  
  private static final int MAX_JMX_LENGTH = 128;
  
  private static final int MAX_ADAPTER_ID_LENGTH = 128;
  
  private static final String UTF8_ENCODING = "UTF-8";
    
  public static ClusterInstance createPingRecord(byte[] data) throws Exception {
    ByteBuffer byteBuffer = ByteBuffer.wrap(data);
    ClusterInstance ping = new ClusterInstance();
    
    long bigBits = byteBuffer.getLong();
    long littleBits = byteBuffer.getLong();
    byte[] adapterId = new byte[MAX_ADAPTER_ID_LENGTH];
    byte[] hostArray = new byte[MAX_JMX_LENGTH];
    byteBuffer.get(adapterId);
    byteBuffer.get(hostArray);
    ping.setUniqueId(new String(adapterId, UTF8_ENCODING).trim());
    ping.setJmxAddress(new String(hostArray, UTF8_ENCODING).trim());
    ping.setClusterUuid(new UUID(bigBits, littleBits));
    
    return ping;
  }
  
  public static byte[] createDataPacket(ClusterInstance ping) throws Exception {
    ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[STANDARD_PACKET_SIZE]);
    
    byteBuffer.putLong(ping.getClusterUuid().getMostSignificantBits());
    byteBuffer.putLong(ping.getClusterUuid().getLeastSignificantBits());
    byteBuffer.put(padStringByteArray(ping.getUniqueId(), MAX_ADAPTER_ID_LENGTH));
    byteBuffer.put(padStringByteArray(ping.getJmxAddress(), MAX_JMX_LENGTH));
    
    return byteBuffer.array();
  }

  private static byte[] padStringByteArray(String sourceString, int maxLength) throws Exception {
    byte[] paddedArray = new byte[maxLength];
    System.arraycopy(sourceString.getBytes(UTF8_ENCODING), 0, paddedArray, 0, sourceString.getBytes(UTF8_ENCODING).length);
    
    return paddedArray;
  }

}
