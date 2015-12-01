package com.enonic.xp.elasticsearch.impl.status.cluster;

public interface ClusterInfoProvider<INFO>
{
    String CLUSTER_HEALTH_TIMEOUT = "3s";

    INFO getInfo();
}
