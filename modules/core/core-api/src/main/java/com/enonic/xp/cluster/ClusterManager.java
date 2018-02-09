package com.enonic.xp.cluster;

public interface ClusterManager
{
    ClusterState getClusterState();

    Clusters getInstances();
}
