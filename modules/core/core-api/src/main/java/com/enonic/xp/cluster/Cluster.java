package com.enonic.xp.cluster;

public interface Cluster
{
    ClusterId getId();

    ClusterHealth getHealth();

    ClusterNodes getNodes();
}
