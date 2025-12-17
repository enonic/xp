package com.enonic.xp.elasticsearch.impl;

import java.util.Map;
import java.util.stream.Collectors;

import org.elasticsearch.common.settings.Settings;
import org.osgi.framework.BundleContext;

import com.enonic.xp.cluster.ClusterConfig;
import com.enonic.xp.config.ConfigBuilder;
import com.enonic.xp.config.ConfigInterpolator;
import com.enonic.xp.config.Configuration;

import static com.google.common.base.Strings.isNullOrEmpty;

final class NodeSettingsBuilder
{
    private static final String COMMON_NODE_NAME_OPTION = "node.name";

    private static final String AUTO_CREATE_INDEX_OPTION = "action.auto_create_index";

    private static final String COMMON_NETWORK_HOST_OPTION = "network.host";

    private static final String ES_NODE_LOCAL = "node.local";

    private static final String COMMON_NETWORK_PUBLISH_HOST_OPTION = "network.publish_host";

    private static final String ES_UNICAST_SOCKETS_OPTION = "discovery.unicast.sockets";

    private static final String ES_ZEN_UNICAST_HOST_OPTION = "discovery.zen.ping.unicast.hosts";

    private static final String ES_UNICAST_PORT_OPTION = "discovery.unicast.port";

    private final BundleContext context;

    private final Configuration defaultConfig;

    private final ClusterConfig clusterConfig;

    NodeSettingsBuilder( final BundleContext context, final ClusterConfig clusterConfig )
    {
        this.context = context;
        this.defaultConfig = ConfigBuilder.create().
            load( getClass(), "default.properties" ).
            build();
        this.clusterConfig = clusterConfig;
    }

    Settings buildSettings( final Map<String, String> map )
    {
        final Configuration config = buildConfig( map );
        return buildSettings( config );
    }

    private Configuration buildConfig( final Map<String, String> map )
    {
        final Configuration config = ConfigBuilder.create().
            addAll( this.defaultConfig ).
            addAll( map ).
            build();

        return new ConfigInterpolator().
            addLookup( this.context::getProperty ).
            interpolate( config );
    }

    private Settings buildSettings( final Configuration config )
    {
        final Settings.Builder builder = Settings.settingsBuilder()
            .put( config.asMap() )
            .put( AUTO_CREATE_INDEX_OPTION, false )
            .put( COMMON_NODE_NAME_OPTION, this.clusterConfig.name().toString() )
            .put( ES_ZEN_UNICAST_HOST_OPTION, createZenUnicastHostString( config ) )
            .put( ES_NODE_LOCAL, !this.clusterConfig.isEnabled() );

        final String networkHost = this.clusterConfig.networkHost();
        if ( !isNullOrEmpty( networkHost ) )
        {
            builder.put( COMMON_NETWORK_HOST_OPTION, networkHost );
        }
        final String networkPublishHost = this.clusterConfig.networkPublishHost();
        if ( !isNullOrEmpty( networkPublishHost ) )
        {
            builder.put( COMMON_NETWORK_PUBLISH_HOST_OPTION, networkPublishHost );
        }
        return builder.build();
    }

    private String createZenUnicastHostString( final Configuration source )
    {
        final String sockets = source.get( ES_UNICAST_SOCKETS_OPTION );
        if ( !isNullOrEmpty( sockets ) )
        {
            return sockets;
        }

        final String port = source.get( ES_UNICAST_PORT_OPTION );

        return this.clusterConfig.discovery().get().stream().
            map( e -> e.getCanonicalHostName() + getPortPrefix( port ) ).
            collect( Collectors.joining( "," ) );
    }

    private String getPortPrefix( final String port )
    {
        return ":" + port;
    }
}
