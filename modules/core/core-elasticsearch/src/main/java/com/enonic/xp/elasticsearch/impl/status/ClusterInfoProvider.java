package com.enonic.xp.elasticsearch.impl.status;

import org.elasticsearch.client.Client;


public abstract class ClusterInfoProvider<INFO>
{
    protected static final String CLUSTER_HEALTH_TIMEOUT = "3s";

    protected Client client;

    public ClusterInfoProvider( final Client client )
    {
        this.client = client;
    }

    public abstract INFO getInfo();

}
