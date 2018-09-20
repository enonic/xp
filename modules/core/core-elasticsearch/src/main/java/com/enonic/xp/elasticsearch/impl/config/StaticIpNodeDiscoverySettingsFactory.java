package com.enonic.xp.elasticsearch.impl.config;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

import com.enonic.xp.cluster.DiscoveryConfig;
import com.enonic.xp.config.Configuration;

import static com.enonic.xp.cluster.DiscoveryConfig.UNICAST_HOST_KEY;

class StaticIpNodeDiscoverySettingsFactory
{
    public static final String ES_UNICAST_HOST_OPTION = "discovery.zen.ping.unicast.hosts";

    public static final String ES_UNICAST_PORT_OPTION = "discovery.unicast.port";

    private final Configuration esConfig;

    private final DiscoveryConfig discoveryConfig;

    private final Logger LOG = LoggerFactory.getLogger( StaticIpNodeDiscoverySettingsFactory.class );

    private StaticIpNodeDiscoverySettingsFactory( final Builder builder )
    {
        esConfig = builder.esConfig;
        discoveryConfig = builder.discoveryConfig;
    }

    Map<String, String> execute()
    {
        final Map<String, String> config = Maps.newHashMap();

        final boolean esConfigHasSpecifiedHosts = esConfig.exists( ES_UNICAST_HOST_OPTION );

        if ( esConfigHasSpecifiedHosts )
        {
            final String hostString = esConfig.get( ES_UNICAST_HOST_OPTION );
            doSetHostString( config, hostString );
        }
        else
        {
            getFromClusterConfig( config );
        }

        return config;
    }

    private void doSetHostString( final Map<String, String> config, final String hostString )
    {
        config.put( ES_UNICAST_HOST_OPTION, hostString );
        LOG.info( "Setting the [{}] discovery option to value [{}]", ES_UNICAST_HOST_OPTION, hostString );
    }

    private void getFromClusterConfig( final Map<String, String> config )
    {
        if ( !this.discoveryConfig.exists( UNICAST_HOST_KEY ) )
        {
            // Use default ES-settings
            LOG.info( "No discovery host values set, using default" );
            return;
        }

        final String hostString = createHostString();
        doSetHostString( config, hostString );
    }

    private String createHostString()
    {
        final String port = esConfig.get( ES_UNICAST_PORT_OPTION );

        return Stream.of( discoveryConfig.get( UNICAST_HOST_KEY ).split( "," ) ).
            map( e -> e + getPortPrefix( port ) ).
            collect( Collectors.joining( "," ) );
    }

    private String getPortPrefix( final String port )
    {
        return "[" + port + "]";
    }

    static Builder create()
    {
        return new Builder();
    }

    static final class Builder
    {
        private Configuration esConfig;

        private DiscoveryConfig discoveryConfig;

        private Builder()
        {
        }

        Builder esConfig( final Configuration val )
        {
            esConfig = val;
            return this;
        }

        public Builder discoveryConfig( final DiscoveryConfig val )
        {
            this.discoveryConfig = val;
            return this;
        }

        StaticIpNodeDiscoverySettingsFactory build()
        {
            return new StaticIpNodeDiscoverySettingsFactory( this );
        }
    }
}
