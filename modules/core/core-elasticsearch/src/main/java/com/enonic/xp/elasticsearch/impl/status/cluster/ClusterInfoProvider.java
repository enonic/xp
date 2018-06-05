package com.enonic.xp.elasticsearch.impl.status.cluster;

interface ClusterInfoProvider<INFO>
{
    String CLUSTER_HEALTH_TIMEOUT = "3s";

    INFO getInfo();
}
