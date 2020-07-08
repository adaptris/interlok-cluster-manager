package com.adaptris.mgmt.cluster.jgroups;

import java.io.InputStream;
import java.util.Optional;

import org.jgroups.JChannel;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JGroupsChannel {
  
  private static final String BUNDLED_JGROUPS_CONFIG_FILE_NAME = "META-INF/cluster-manager.xml";
  
  private static JGroupsChannel INSTANCE;
  
  @Getter(lombok.AccessLevel.PRIVATE)
  @Setter(lombok.AccessLevel.PRIVATE)
  private JChannel jChannel;
  
  /**
   * The common name known and configured in the bootstrap.properties by each instance in this cluster.
   */
  @Getter
  @Setter
  private String clusterName;
  
  @Getter
  @Setter
  private String configResource;
  
  private JGroupsChannel() {
    // Singleton.
  }
  
  public static JGroupsChannel getInstance() {
    return getInstance(null);
  }
  
  public static JGroupsChannel getInstance(String configResource) {
    if(INSTANCE == null) {
        INSTANCE = new JGroupsChannel();
        if(configResource != null)
          INSTANCE.setConfigResource(configResource);
    }
    
    return INSTANCE;
  }
  
  /**
   * <p>
   * JGroups manager that will handle the broadcasting and receiving of network pings.
   * <p>
   * <p>
   * Typically, you won't need to set or access this member.  It is all handled internally. 
   * The getter and setter only exist for unit test mocking.
   * </p>
   * <p>
   * When JGroups is initialised the configuration file is taken from the META-INF directory of 
   * this jar file.
   * </p>
   */
  public JChannel getJGroupsChannel() throws Exception {
    if(this.getJChannel() == null) {
      this.setJChannel(new JChannel(this.loadJGroupsConfiguration()));
    }
    
    return this.getJChannel();
  }
  
  /**
   * <p>
   * JGroups manager that will handle the broadcasting and receiving of network pings.
   * <p>
   * <p>
   * Typically, you won't need to set or access this member.  It is all handled internally. 
   * The getter and setter only exist for unit test mocking.
   * </p>
   * <p>
   * When JGroups is initialised the configuration file is taken from the META-INF directory of 
   * this jar file, or a configured override..
   * </p>
   */
  public void setJGroupsChannel(JChannel jChannel) {
    this.setJChannel(jChannel);
  }

  private InputStream loadJGroupsConfiguration() {
    return Optional.ofNullable(this.getConfigResource())
        .map(configResource -> this.loadJGroupsConfiguration(configResource))
        .orElse(this.loadJGroupsConfiguration(BUNDLED_JGROUPS_CONFIG_FILE_NAME));
  }
  
  private InputStream loadJGroupsConfiguration(String configName) {
    log.debug("Loading JGroups configuartion file named '{}'", configName);
    return this.getClass().getClassLoader().getResourceAsStream(configName);
  }
  
  public void start() throws Exception {
    if(!this.getJGroupsChannel().isConnected())
      this.getJGroupsChannel().connect(this.getClusterName());
  }
  
  public void stop() {
    try {
      if(this.getJGroupsChannel().isConnected()) {
        this.getJGroupsChannel().disconnect();
        this.getJGroupsChannel().close();
      }
    } catch (Exception ex) {
      log.error("Failed to stop the JChannel, continuing.", ex);
    }
  }

  static void clear() {
    INSTANCE = null;
  }
}
