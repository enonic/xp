package com.enonic.xp.cluster;

public interface Cluster
{
    ClusterId getId();

    ClusterHealth getHealth();

    ClusterNodes getNodes();

    @Deprecated
    void enable();

    @Deprecated
    void disable();

    @Deprecated
    boolean isEnabled();
}
