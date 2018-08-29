package com.enonic.xp.ignite.impl.config;

public @interface IgniteSettings
{
    String home();

    String localhost();

    int metrics_log_frequency() default 0;

    boolean connector_enabled() default false;

    boolean odbc_enabled() default false;

    int discovery_tcp_port() default 47500;

    int discovery_tcp_port_range() default 0;

    int discovery_tcp_reconnect() default 10;

    long discovery_tcp_network_timeout() default 5000L;

    long discovery_tcp_socket_timeout() default 2000L;

    long discovery_tcp_ack_timeout() default 2000L;

    long discovery_tcp_join_timeout() default 0L;

    int discovery_tcp_stat_printFreq() default 0;

    String off_heap_max_size() default "512MB";

    int communication_message_queue_limit() default 1024;

}
