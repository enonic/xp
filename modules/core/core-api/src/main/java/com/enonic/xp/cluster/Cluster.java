package com.enonic.xp.cluster;

public interface Cluster
{
    ClusterId getId();

    ClusterHealth getHealth();

    ClusterNodes getNodes();

    void enable();

    void disable();

    boolean isEnabled();
}
