package com.enonic.xp.cluster;

public interface ClusterConfig
{
    DiscoveryConfig discoveryConfig();

    ClusterNodeId name();

    boolean isEnabled();

    String networkPublishHost();

    String networkHost();

    boolean isSessionReplicationEnabled();
}
