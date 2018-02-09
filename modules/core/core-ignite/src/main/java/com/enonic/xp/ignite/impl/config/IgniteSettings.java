package com.enonic.xp.ignite.impl.config;

public @interface IgniteSettings
{
    String home() default "ignite";

    String localhost();

    int metrics_log_frequency() default 0;

    String discovery_tcp_ipFinder() default "staticIP";

    String discovery_tcp_localAddress() default "localhost";

    int discovery_tcp_port() default 47500;

    int discovery_tcp_port_range() default 0;

    int discovery_tcp_reconnect() default 2;

    long discovery_tcp_network_timeout() default 5000L;

    long discovery_tcp_socket_timeout() default 2000L;

    long discovery_tcp_ack_timeout() default 2000L;

    long discovery_tcp_join_timeout() default 0L;

    int discovery_tcp_stat_printFreq() default 0;
}
