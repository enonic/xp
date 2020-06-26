package com.enonic.xp.core.impl.hazelcast;

public @interface HazelcastConfig
{
    boolean clusterConfigDefaults() default true;

    boolean system_hazelcast_phone_home_enabled() default true;

    boolean system_hazelcast_socket_bind_any() default true;

    int system_hazelcast_initial_min_cluster_size() default 2;

    boolean system_hazelcast_prefer_ipv4_stack() default true;

    int system_hazelcast_tcp_join_port_try_count() default 1;

    int system_hazelcast_max_no_heartbeat_seconds() default 60;

    int system_hazelcast_heartbeat_interval_seconds() default 5;

    int system_hazelcast_mastership_claim_timeout_seconds() default 120;

    int hazelcast_wait_seconds_before_join() default 5;

    int hazelcast_max_wait_seconds_before_join() default 20;

    boolean lightMember() default false;

    int network_port() default 5701;

    boolean network_portAutoIncrement() default false;

    int network_portCount() default 100;

    String network_publicAddress();

    boolean network_join_multicast_enabled() default false;

    boolean network_join_tcpIp_enabled() default true;

    boolean network_join_kubernetes_enabled() default false;

    String network_join_kubernetes_serviceDns();

    String network_join_tcpIp_members();

    boolean network_interfaces_enabled() default false;

    String network_interfaces();

    boolean network_restApi_enabled() default false;

    String network_restApi_restEndpointGroups() default "HEALTH_CHECK, CLUSTER_READ";
}
