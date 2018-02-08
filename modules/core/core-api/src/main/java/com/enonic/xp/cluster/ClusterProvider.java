package com.enonic.xp.cluster;

public interface ClusterProvider
{
    ClusterProviderId getId();

    ClusterProviderHealth getHealth();

    ClusterNodes getNodes();

    void enable();

    void disable();

    boolean isEnabled();
}
