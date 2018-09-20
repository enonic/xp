package com.enonic.xp.cluster;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.Maps;

import com.enonic.xp.config.Configuration;

public class DiscoveryConfig
{
    public static final String UNICAST_HOST_KEY = "unicast.hosts";

    public static final String DISCOVERY_TYPE_KEY = "type";

    private final Map<String, String> config;

    private final DiscoveryType type;

    private DiscoveryConfig( final Builder builder )
    {
        config = builder.config;
        this.type = resolveType();
    }

    public DiscoveryConfig( final Map<String, String> config )
    {
        this.config = config;
        this.type = resolveType();
    }

    public static DiscoveryConfig empty()
    {
        return new DiscoveryConfig( new HashMap<>() );
    }

    public static DiscoveryConfig from( final Configuration config )
    {
        return new DiscoveryConfig( config.asMap() );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public DiscoveryType getType()
    {
        return type;
    }

    private DiscoveryType resolveType()
    {
        if ( this.config != null && this.config.containsKey( DISCOVERY_TYPE_KEY ) )
        {
            try
            {
                return DiscoveryType.fromString( this.config.get( DISCOVERY_TYPE_KEY ) );
            }
            catch ( IllegalArgumentException e )
            {
                throw new IllegalArgumentException(
                    "Invalid type of cluster discovery configured: " + this.config.get( DISCOVERY_TYPE_KEY ) );
            }
        }

        return DiscoveryType.STATIC_IP;
    }

    public String get( final String key )
    {
        return this.config != null ? this.config.get( key ) : null;
    }


    public boolean exists( final String key )
    {
        return this.config != null && this.config.containsKey( key );
    }


    public static final class Builder
    {
        private Map<String, String> config = Maps.newHashMap();

        private Builder()
        {
        }

        public Builder add( final String key, final String value )
        {
            config.put( key, value );
            return this;
        }

        public DiscoveryConfig build()
        {
            return new DiscoveryConfig( this );
        }
    }
}
