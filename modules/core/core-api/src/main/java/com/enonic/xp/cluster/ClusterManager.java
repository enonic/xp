package com.enonic.xp.cluster;

public interface ClusterManager
{
    ClusterState getHealth();

    ClusterProviders getProviders();
}
