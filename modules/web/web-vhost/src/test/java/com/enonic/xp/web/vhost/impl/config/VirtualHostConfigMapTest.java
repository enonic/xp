package com.enonic.xp.web.vhost.impl.config;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.web.vhost.VirtualHost;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class VirtualHostConfigMapTest
{
    private Map<String, String> map;

    @BeforeEach
    public void setup()
    {
        map = new HashMap<>();
    }

    @Test
    public void testEmptyMap()
    {
        final VirtualHostConfigMap virtualHostConfigMap = new VirtualHostConfigMap( map );

        assertFalse( virtualHostConfigMap.isEnabled() );
        assertEquals( 0, virtualHostConfigMap.buildMappings().size() );
    }

    @Test
    public void testEnabled()
    {
        map.put( "enabled", "true" );
        assertTrue( new VirtualHostConfigMap( map ).isEnabled() );
    }

    @Test
    public void testDisabled()
    {
        map.put( "enabled", "false" );
        assertFalse( new VirtualHostConfigMap( map ).isEnabled() );
    }


    @Test
    public void testDefaultVirtualHostConfig()
    {
        map.put( "mapping.myapp.host", "" );

        final VirtualHostConfigMap virtualHostConfig = new VirtualHostConfigMap( map );
        final VirtualHost virtualHost = virtualHostConfig.buildMappings().get( 0 );

        assertEquals( "myapp", virtualHost.getName() );
        assertEquals( "localhost", virtualHost.getHost() );
        assertEquals( "/", virtualHost.getSource() );
        assertEquals( "/", virtualHost.getTarget() );
        assertNull( virtualHost.getDefaultIdProviderKey() );
        assertEquals( 0, virtualHost.getIdProviderKeys().getSize() );
    }

    @Test
    public void testGetSource()
    {
        map.put( "mapping.myapp.source", "/" );
        VirtualHostConfigMap virtualHostConfig = new VirtualHostConfigMap( map );
        VirtualHost virtualHost = virtualHostConfig.buildMappings().get( 0 );
        assertEquals( "/", virtualHost.getSource() );

        map.put( "mapping.myapp.source", "a/b" );
        virtualHostConfig = new VirtualHostConfigMap( map );
        virtualHost = virtualHostConfig.buildMappings().get( 0 );
        assertEquals( "/a/b", virtualHost.getSource() );

        map.put( "mapping.myapp.source", "a/b/" );
        virtualHostConfig = new VirtualHostConfigMap( map );
        virtualHost = virtualHostConfig.buildMappings().get( 0 );
        assertEquals( "/a/b", virtualHost.getSource() );

        map.put( "mapping.myapp.source", "/a/b/" );
        virtualHostConfig = new VirtualHostConfigMap( map );
        virtualHost = virtualHostConfig.buildMappings().get( 0 );
        assertEquals( "/a/b", virtualHost.getSource() );

        map.put( "mapping.myapp.source", "/a/b//" );
        virtualHostConfig = new VirtualHostConfigMap( map );
        virtualHost = virtualHostConfig.buildMappings().get( 0 );
        assertEquals( "/a/b/", virtualHost.getSource() );

        map.put( "mapping.myapp.source", "//a/b/" );
        virtualHostConfig = new VirtualHostConfigMap( map );
        virtualHost = virtualHostConfig.buildMappings().get( 0 );
        assertEquals( "//a/b", virtualHost.getSource() );

    }

    @Test
    public void testGetTarget()
    {
        map.put( "mapping.myapp.target", "/" );
        VirtualHostConfigMap virtualHostConfig = new VirtualHostConfigMap( map );
        VirtualHost virtualHost = virtualHostConfig.buildMappings().get( 0 );
        assertEquals( "/", virtualHost.getTarget() );

        map.put( "mapping.myapp.target", "a/b" );
        virtualHostConfig = new VirtualHostConfigMap( map );
        virtualHost = virtualHostConfig.buildMappings().get( 0 );
        assertEquals( "/a/b", virtualHost.getTarget() );

        map.put( "mapping.myapp.target", "a/b/" );
        virtualHostConfig = new VirtualHostConfigMap( map );
        virtualHost = virtualHostConfig.buildMappings().get( 0 );
        assertEquals( "/a/b", virtualHost.getTarget() );

        map.put( "mapping.myapp.target", "/a/b/" );
        virtualHostConfig = new VirtualHostConfigMap( map );
        virtualHost = virtualHostConfig.buildMappings().get( 0 );
        assertEquals( "/a/b", virtualHost.getTarget() );

        map.put( "mapping.myapp.target", "/a/b//" );
        virtualHostConfig = new VirtualHostConfigMap( map );
        virtualHost = virtualHostConfig.buildMappings().get( 0 );
        assertEquals( "/a/b/", virtualHost.getTarget() );

        map.put( "mapping.myapp.target", "//a/b/" );
        virtualHostConfig = new VirtualHostConfigMap( map );
        virtualHost = virtualHostConfig.buildMappings().get( 0 );
        assertEquals( "//a/b", virtualHost.getTarget() );
    }

    @Test
    public void testOrder()
    {
        map.put( "mapping.myapp1.source", "/a" );
        map.put( "mapping.myapp2.source", "/a/b" );

        VirtualHostConfigMap virtualHostConfig = new VirtualHostConfigMap( map );

        VirtualHost virtualHost1 = virtualHostConfig.buildMappings().get( 0 );
        VirtualHost virtualHost2 = virtualHostConfig.buildMappings().get( 1 );

        assertEquals( "/a/b", virtualHost1.getSource() );
        assertEquals( "/a", virtualHost2.getSource() );
    }

    @Test
    public void testDefaultIdProvider()
    {
        map.put( "mapping.myapp1.idProvider.system", "default" );
        map.put( "mapping.myapp1.idProvider.myProvider", "enabled" );

        VirtualHostConfigMap virtualHostConfig = new VirtualHostConfigMap( map );

        VirtualHost virtualHost = virtualHostConfig.buildMappings().get( 0 );
        assertEquals( "system", virtualHost.getDefaultIdProviderKey().toString() );
        assertEquals( 2, virtualHost.getIdProviderKeys().getSize() );
        assertTrue( virtualHost.getIdProviderKeys().contains( IdProviderKey.from( "myProvider" ) ) );
    }
}
