package com.enonic.xp.elasticsearch7.impl;

public @interface ElasticsearchServerConfig
{
    String PATH = "${xp.home}/repo/index";

    String esServerDir() default "${xp.home}/../elasticsearch";

    boolean embeddedMode() default false;

    String http_port() default "9200";

    String path_data() default PATH + "/data";

    String path_repo() default "${xp.home}/snapshots";

    String path_work() default PATH + "/work";

    String path_conf() default PATH + "/conf";

    String path_logs() default PATH + "/logs";

    String cluster_name() default "mycluster";

    boolean cluster_routing_allocation_disk_thresholdEnabled() default false;

    String transport_port() default "9300";

    int gateway_expectedNodes() default 1;

    String gateway_recoverAfterTime() default "5m";

    int gateway_recoverAfterNodes() default 1;

    int index_maxResultWindow() default 500_000;

}
