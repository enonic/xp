package com.enonic.xp.elasticsearch7.impl;

public @interface ElasticsearchServerConfig
{
    String esServerDir();

    boolean embeddedMode() default false;

    String http_port();

    String path();

    String path_home();

    String path_data();

    String path_repo();

    String path_work();

    String path_conf();

    String path_logs();

    String path_plugins();

    String cluster_name();

    boolean cluster_routing_allocation_disk_thresholdEnabled() default false;

    String transport_port();

    int gateway_expectedNodes() default 1;

    String gateway_recoverAfterTime();

    int gateway_recoverAfterNodes() default 1;

    int index_maxResultWindow() default 500_000;

}
