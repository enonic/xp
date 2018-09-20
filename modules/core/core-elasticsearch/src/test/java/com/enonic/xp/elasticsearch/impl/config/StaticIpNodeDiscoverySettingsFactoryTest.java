package com.enonic.xp.elasticsearch.impl.config;

import java.util.Map;

import org.junit.Test;

import com.enonic.xp.cluster.DiscoveryConfig;
import com.enonic.xp.cluster.DiscoveryType;

import static com.enonic.xp.cluster.DiscoveryConfig.UNICAST_HOST_KEY;
import static com.enonic.xp.elasticsearch.impl.config.StaticIpNodeDiscoverySettingsFactory.ES_UNICAST_HOST_OPTION;
import static com.enonic.xp.elasticsearch.impl.config.StaticIpNodeDiscoverySettingsFactory.ES_UNICAST_PORT_OPTION;
import static org.junit.Assert.*;

public class StaticIpNodeDiscoverySettingsFactoryTest
{
    @Test
    public void es_settings_should_pass_through_to_settings()
    {
        final DiscoveryConfig discoveryConfig = DiscoveryConfig.create().
            add( "type", DiscoveryType.STATIC_IP.toString() ).
            add( UNICAST_HOST_KEY, "12.23.45.67" ).
            build();

        final ConfigurationTestImpl esConfig = new ConfigurationTestImpl();
        esConfig.put( ES_UNICAST_HOST_OPTION, "10.0.4.5:1234,10.0.4.6:2345" );
        esConfig.put( ES_UNICAST_PORT_OPTION, "[9300-9400]" );

        final Map<String, String> staticIpConfig = StaticIpNodeDiscoverySettingsFactory.create().
            discoveryConfig( discoveryConfig ).
            esConfig( esConfig ).
            build().
            execute();

        assertEquals( "10.0.4.5:1234,10.0.4.6:2345", staticIpConfig.get( ES_UNICAST_HOST_OPTION ) );
    }

    @Test
    public void create_host_string_port_range()
    {
        final DiscoveryConfig discoveryConfig = DiscoveryConfig.create().
            add( "type", DiscoveryType.STATIC_IP.toString() ).
            add( UNICAST_HOST_KEY, "12.23.45.67,23.45.67.89,backups.enonic.io" ).
            build();

        final ConfigurationTestImpl esConfig = new ConfigurationTestImpl();
        esConfig.put( ES_UNICAST_PORT_OPTION, "9300-9400" );

        final Map<String, String> staticIpConfig = StaticIpNodeDiscoverySettingsFactory.create().
            discoveryConfig( discoveryConfig ).
            esConfig( esConfig ).
            build().
            execute();

        assertEquals( "12.23.45.67[9300-9400],23.45.67.89[9300-9400],backups.enonic.io[9300-9400]",
                      staticIpConfig.get( ES_UNICAST_HOST_OPTION ) );
    }

    @Test
    public void create_host_string_single_port()
    {
        final DiscoveryConfig discoveryConfig = DiscoveryConfig.create().
            add( "type", DiscoveryType.STATIC_IP.toString() ).
            add( UNICAST_HOST_KEY, "backups.enonic.io,localhost" ).
            build();

        final ConfigurationTestImpl esConfig = new ConfigurationTestImpl();
        esConfig.put( ES_UNICAST_PORT_OPTION, "9300" );

        final Map<String, String> staticIpConfig = StaticIpNodeDiscoverySettingsFactory.create().
            discoveryConfig( discoveryConfig ).
            esConfig( esConfig ).
            build().
            execute();

        assertEquals( "backups.enonic.io[9300],localhost[9300]", staticIpConfig.get( ES_UNICAST_HOST_OPTION ) );
    }

}


