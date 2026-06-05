package com.enonic.xp.cluster;

public interface ClusterConfig
{
    NodeDiscovery discovery();

    ClusterNodeId name();

    boolean isEnabled();

    /**
     * Publish host, resolved to a single concrete address.
     */
    String networkPublishHost();

    /**
     * Bind host, resolved to a single concrete address.
     */
    String networkHost();
}
