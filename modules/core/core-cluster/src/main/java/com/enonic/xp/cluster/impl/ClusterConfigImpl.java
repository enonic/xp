package com.enonic.xp.cluster.impl;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import com.enonic.xp.cluster.ClusterConfig;
import com.enonic.xp.cluster.ClusterNodeId;
import com.enonic.xp.cluster.NodeDiscovery;
import com.enonic.xp.cluster.impl.discovery.ClusterNodeNameProvider;
import com.enonic.xp.cluster.impl.discovery.StaticIpNodeDiscovery;
import com.enonic.xp.config.ConfigBuilder;
import com.enonic.xp.config.ConfigInterpolator;
import com.enonic.xp.config.Configuration;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.base.Strings.nullToEmpty;

@Component(configurationPid = "com.enonic.xp.cluster")
public class ClusterConfigImpl
    implements ClusterConfig
{
    private Configuration config;

    private NetworkInterfaceResolver networkInterfaceResolver;

    @Activate
    public void activate( final Map<String, String> map )
    {
        this.networkInterfaceResolver = new NetworkInterfaceResolver();
        this.config = ConfigBuilder.create().
            load( getClass(), "default.properties" ).
            addAll( map ).
            build();

        this.config = new ConfigInterpolator().interpolate( this.config );
    }

    @Override
    public NodeDiscovery discovery()
    {
        final String unicastHosts = this.config.get( "discovery.unicast.hosts" );

        final StaticIpNodeDiscovery.Builder builder = StaticIpNodeDiscovery.create();

        for ( final String entry : unicastHosts.split( "," ) )
        {
            try
            {
                builder.add( InetAddress.getByName( normalizeHostEntry( entry ) ) );
            }
            catch ( UnknownHostException e )
            {
                throw new IllegalArgumentException( "Cannot create host entry for value [" + entry + "]", e );
            }
        }

        return builder.build();
    }

    private String normalizeHostEntry( final String entry )
    {
        return isNullOrEmpty( entry ) ? "" : entry.trim();
    }

    @Override
    public ClusterNodeId name()
    {
        final String nodeName = this.config.get( "node.name" );

        if ( isNullOrEmpty( nodeName ) )
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
    public String networkPublishHost()
    {
        final String host = nullToEmpty( this.config.get( "network.publish.host" ) );
        return host.isBlank() ? null : networkInterfaceResolver.resolveAddress( host );
    }

    @Override
    public String networkHost()
    {
        final String host = nullToEmpty( this.config.get( "network.host" ) );
        return host.isBlank() ? null : networkInterfaceResolver.resolveAddress( host );
    }
}
