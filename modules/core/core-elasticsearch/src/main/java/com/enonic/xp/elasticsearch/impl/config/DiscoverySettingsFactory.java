package com.enonic.xp.elasticsearch.impl.config;

import java.util.Map;

import com.google.common.collect.Maps;

import com.enonic.xp.cluster.DiscoveryConfig;
import com.enonic.xp.cluster.DiscoveryType;
import com.enonic.xp.config.Configuration;

class DiscoverySettingsFactory
{
    private final Configuration esConfig;

    private final DiscoveryConfig discoveryConfig;

    private DiscoverySettingsFactory( final Builder builder )
    {
        esConfig = builder.esConfig;
        discoveryConfig = builder.clusterConfig;
    }

    Map<String, String> execute()
    {
        final Map<String, String> config = Maps.newHashMap();

        if ( discoveryConfig.getType().equals( DiscoveryType.STATIC_IP ) )
        {
            config.putAll( StaticIpNodeDiscoverySettingsFactory.create().
                esConfig( esConfig ).
                discoveryConfig( discoveryConfig ).
                build().
                execute() );
        }
        else
        {
            throw new RuntimeException( "Unsupported discovery type [" + discoveryConfig.getType() + "]" );
        }

        return config;
    }


    static Builder create()
    {
        return new Builder();
    }

    static final class Builder
    {
        private Configuration esConfig;

        private DiscoveryConfig clusterConfig;

        private Builder()
        {
        }

        Builder esConfig( final Configuration val )
        {
            esConfig = val;
            return this;
        }

        Builder discoveryConfig( final DiscoveryConfig val )
        {
            clusterConfig = val;
            return this;
        }

        DiscoverySettingsFactory build()
        {
            return new DiscoverySettingsFactory( this );
        }
    }
}
