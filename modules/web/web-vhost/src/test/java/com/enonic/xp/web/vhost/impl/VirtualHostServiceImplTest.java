package com.enonic.xp.web.vhost.impl;

import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.junit.jupiter.api.Test;

import com.google.common.collect.Maps;

import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.IdProviderKeys;
import com.enonic.xp.web.vhost.VirtualHost;
import com.enonic.xp.web.vhost.impl.config.VirtualHostServiceImpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VirtualHostServiceImplTest
{
    @Test
    void testNoConfig()
    {
        final VirtualHostServiceImpl virtualHostService = new VirtualHostServiceImpl( Map.of() );

        assertFalse( virtualHostService.isEnabled() );
        assertNotNull( virtualHostService.getVirtualHosts() );

        final List<VirtualHost> mappings = virtualHostService.getVirtualHosts();
        assertEquals( 0, mappings.size() );
    }

    @Test
    void testLoadedConfig_none()
        throws Exception
    {
        final VirtualHostServiceImpl virtualHostService = new VirtualHostServiceImpl( loadConfig( "none" ) );

        assertFalse( virtualHostService.isEnabled() );
        assertNotNull( virtualHostService.getVirtualHosts() );

        final List<VirtualHost> mappings = virtualHostService.getVirtualHosts();
        assertEquals( 0, mappings.size() );
    }

    @Test
    void testLoadedConfig_simple()
        throws Exception
    {
        final VirtualHostServiceImpl virtualHostService = new VirtualHostServiceImpl( loadConfig( "simple" ) );

        assertTrue( virtualHostService.isEnabled() );

        final List<VirtualHost> mappings = virtualHostService.getVirtualHosts();

        assertNotNull( mappings );
        assertEquals( 1, mappings.size() );

        assertMapping( mappings.get( 0 ), "a", "localhost", "/status", "/full/path/status", null, null );
    }

    @Test
    void testLoadedConfig_disable()
        throws Exception
    {
        final VirtualHostServiceImpl virtualHostService = new VirtualHostServiceImpl( loadConfig( "disable" ) );

        assertFalse( virtualHostService.isEnabled() );
    }

    @Test
    void testLoadedConfig_complete()
        throws Exception
    {
        final VirtualHostServiceImpl virtualHostService = new VirtualHostServiceImpl( loadConfig( "complete" ) );

        assertTrue( virtualHostService.isEnabled() );

        final List<VirtualHost> mappings = virtualHostService.getVirtualHosts();

        assertNotNull( mappings );
        assertEquals( 4, mappings.size() );

        assertMapping( mappings.get( 0 ), "b", "enonic.com", "/status/b", "/full/path/status/b", IdProviderKeys.from( "enonic" ), null );

        assertMapping( mappings.get( 1 ), "a", "localhost", "/status/a", "/full/path/status/a",
                       IdProviderKeys.from( IdProviderKey.system() ), null );

        assertMapping( mappings.get( 2 ), "c", "localhost", "/status/c", "/full/path/status/c", null, null );

        assertMapping( mappings.get( 3 ), "d", "localhost", "/status/d", "/full/path/status/d", IdProviderKeys.from( "enonic.with.a.dot" ),
                       IdProviderKeys.from( "enonic_disabled", "notSpecifiedProvider" ) );
    }

    @Test
    void testEnabled()
    {
        final VirtualHostServiceImpl virtualHostService = new VirtualHostServiceImpl( Collections.singletonMap( "enabled", "true" ) );

        assertTrue( virtualHostService.isEnabled() );
    }

    @Test
    void testGetVirtualHosts()
    {
        final Map<String, String> configurationMap = new HashMap<>();

        configurationMap.put( "enabled", "true" );

        configurationMap.put( "mapping.a.host", "localhost" );
        configurationMap.put( "mapping.a.source", "/a" );
        configurationMap.put( "mapping.a.target", "/other/a" );

        final VirtualHostServiceImpl virtualHostService = new VirtualHostServiceImpl( configurationMap );

        final List<VirtualHost> virtualHosts = virtualHostService.getVirtualHosts();

        assertNotNull( virtualHosts );
        assertFalse( virtualHosts.isEmpty() );
        assertEquals( "a", virtualHosts.get( 0 ).getName() );
    }

    @Test
    void testOrder()
    {
        final Map<String, String> configurationMap = new HashMap<>();

        configurationMap.put( "enabled", "true" );

        configurationMap.put( "mapping.a.host", "localhost" );
        configurationMap.put( "mapping.a.source", "/a" );
        configurationMap.put( "mapping.a.target", "/other/a" );

        configurationMap.put( "mapping.b.host", "localhost" );
        configurationMap.put( "mapping.b.source", "/b" );
        configurationMap.put( "mapping.b.target", "/other/b" );

        configurationMap.put( "mapping.c.host", "~localhost" );
        configurationMap.put( "mapping.c.source", "/a/c" );
        configurationMap.put( "mapping.c.target", "/other/a/c" );

        configurationMap.put( "mapping.d.host", "enonic.com" );
        configurationMap.put( "mapping.d.source", "/d" );
        configurationMap.put( "mapping.d.target", "/other/d" );
        configurationMap.put( "mapping.d.order", "1" );

        final VirtualHostServiceImpl virtualHostService = new VirtualHostServiceImpl( configurationMap );

        final List<VirtualHost> virtualHosts = virtualHostService.getVirtualHosts();
        assertThat( virtualHosts.stream().map( VirtualHost::getName ) ).containsExactly( "d", "c", "a", "b" );
    }

    private Map<String, String> loadConfig( final String name )
        throws Exception
    {
        final String path = "vhost-" + name + ".properties";
        final InputStream in = getClass().getResourceAsStream( path );

        assertNotNull( in, "Properties file [" + path + "] not found" );

        final Properties props = new Properties();
        props.load( in );

        return Maps.fromProperties( props );
    }

    private void assertMapping( final VirtualHost virtualHost, final String name, final String host, final String source,
                                final String target, final IdProviderKeys enabledProviderKeys,
                                final IdProviderKeys notEnabledIdProviderKeys )
    {
        assertEquals( name, virtualHost.getName() );
        assertEquals( host, virtualHost.getHost() );
        assertEquals( source, virtualHost.getSource() );
        assertEquals( target, virtualHost.getTarget() );
        if ( enabledProviderKeys != null )
        {

            enabledProviderKeys.forEach( idProviderKey -> assertTrue( virtualHost.getIdProviderKeys().contains( idProviderKey ) ) );
        }
        if ( notEnabledIdProviderKeys != null )
        {

            notEnabledIdProviderKeys.forEach( idProviderKey -> assertFalse( virtualHost.getIdProviderKeys().contains( idProviderKey ) ) );
        }
    }

}
