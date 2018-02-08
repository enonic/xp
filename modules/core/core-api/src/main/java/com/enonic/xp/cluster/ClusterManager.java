package com.enonic.xp.cluster;

public interface ClusterManager
{
    ClusterState getHealth();

    Clusters getInstances();
}
