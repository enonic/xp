package com.enonic.xp.cluster;

public interface ClusterConfig
{
    NodeDiscovery discovery();

    ClusterNodeId name();

    boolean isEnabled();
}
