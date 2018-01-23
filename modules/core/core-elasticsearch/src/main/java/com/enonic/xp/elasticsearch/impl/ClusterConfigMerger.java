package com.enonic.xp.elasticsearch.impl;

import java.util.stream.Collectors;

import com.enonic.xp.cluster.ClusterConfig;
import com.enonic.xp.config.ConfigBuilder;
import com.enonic.xp.config.Configuration;

class ClusterConfigMerger
{
    private static final String ES_UNICAST_HOST_OPTION = "discovery.zen.ping.unicast.hosts";

    private static final String CLUSTER_CONFIG_UNITCAST_OPTION = "discovery.unicast.port";

    private static final String ES_NODE_NAME_OPTION = "node.name";

    static Configuration merge( final ClusterConfig clusterConfig, final Configuration source )
    {
        final ConfigBuilder builder = ConfigBuilder.create().
            addAll( source ).
            add( ES_NODE_NAME_OPTION, clusterConfig.name().toString() );

        if ( !source.exists( ES_UNICAST_HOST_OPTION ) )
        {
            builder.add( ES_UNICAST_HOST_OPTION, createHostString( clusterConfig, source ) );
        }

        return builder.build();
    }

    private static String createHostString( final ClusterConfig clusterConfig, final Configuration source )
    {
        final String port = source.get( CLUSTER_CONFIG_UNITCAST_OPTION );

        return clusterConfig.discovery().get().stream().map( e -> e.getCanonicalHostName() + ":" + port ).collect(
            Collectors.joining( "," ) );
    }
}
