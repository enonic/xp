package com.enonic.xp.elasticsearch.server.config;

public @interface ElasticsearchServerConfig
{
    String esServerDir();

    boolean embeddedMode() default false;

    String path();

    String http_port() default "9200";

    String path_data();

    String path_repo();

    String path_work();

    String path_conf();

    String path_logs();

    String cluster_name() default "mycluster";

    boolean cluster_routing_allocation_disk_thresholdEnabled() default false;

    String transport_port() default "9300";

    int gateway_expectedNodes() default 1;

    String gateway_recoverAfterTime() default "5m";

    int gateway_recoverAfterNodes() default 1;

    int index_maxResultWindow() default 500_000;

}
