package com.enonic.xp.cluster;

public interface ClusterConfig
{
    NodeDiscovery discovery();

    ClusterNodeId name();

    boolean isEnabled();

    String networkPublishHost();

    String networkHost();

    boolean isSessionReplicationEnabled();
}
