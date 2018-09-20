package com.enonic.xp.cluster.impl;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import com.google.common.base.Strings;

import com.enonic.xp.cluster.ClusterConfig;
import com.enonic.xp.cluster.ClusterNodeId;
import com.enonic.xp.cluster.DiscoveryConfig;
import com.enonic.xp.config.ConfigBuilder;
import com.enonic.xp.config.ConfigInterpolator;
import com.enonic.xp.config.Configuration;

@Component(configurationPid = "com.enonic.xp.cluster")
public class ClusterConfigImpl
    implements ClusterConfig
{
    private Configuration config;

    private NetworkInterfaceResolver networkInterfaceResolver = new NetworkInterfaceResolver();

    private DiscoveryConfig discoveryConfig;

    @Activate
    public void activate( final Map<String, String> map )
    {
        this.config = ConfigBuilder.create().
            load( getClass(), "default.properties" ).
            addAll( map ).
            build();

        this.config = new ConfigInterpolator().interpolate( this.config );

        this.discoveryConfig = new DiscoveryConfig( this.config.subConfig( "discovery." ).asMap() );
    }

    @Override
    public DiscoveryConfig discoveryConfig()
    {
        return this.discoveryConfig;
    }

    @Override
    public ClusterNodeId name()
    {
        final String nodeName = this.config.get( "node.name" );

        if ( Strings.isNullOrEmpty( nodeName ) )
        {
            return ClusterNodeNameProvider.getID();
        }

        return ClusterNodeId.from( nodeName );
    }

    @Override
    public boolean isEnabled()
    {
        return Boolean.parseBoolean( this.config.get( "cluster.enabled" ) );
    }

    @Override
    public boolean isSessionReplicationEnabled()
    {
        return Boolean.parseBoolean( this.config.getOrDefault( "session.replication.enabled", "false" ) );
    }

    @Override
    public String networkPublishHost()
    {
        final String host = this.config.get( "network.publish.host" );
        return host == null ? null : networkInterfaceResolver.resolveAddress( host );
    }

    @Override
    public String networkHost()
    {
        final String host = this.config.get( "network.host" );
        return host == null ? null : networkInterfaceResolver.resolveAddress( host );
    }

    public void setNetworkInterfaceResolver( final NetworkInterfaceResolver networkInterfaceResolver )
    {
        this.networkInterfaceResolver = networkInterfaceResolver;
    }
}
