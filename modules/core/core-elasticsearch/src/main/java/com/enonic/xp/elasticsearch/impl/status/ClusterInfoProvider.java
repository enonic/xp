package com.enonic.xp.elasticsearch.impl.status;

public interface ClusterInfoProvider<INFO>
{
    String CLUSTER_HEALTH_TIMEOUT = "3s";

    INFO getInfo();
}
