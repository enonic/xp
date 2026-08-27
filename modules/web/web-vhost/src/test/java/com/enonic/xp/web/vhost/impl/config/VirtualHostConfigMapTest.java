package com.enonic.xp.web.vhost.impl.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.web.vhost.IdProviderFlow;
import com.enonic.xp.web.vhost.VirtualHost;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VirtualHostConfigMapTest
{
    private Map<String, String> map;

    @BeforeEach
    void setup()
    {
        map = new HashMap<>();
    }

    @Test
    void testEmptyMap()
    {
        final VirtualHostConfigMap virtualHostConfigMap = new VirtualHostConfigMap( map );

        assertFalse( virtualHostConfigMap.isEnabled() );
        assertEquals( 0, virtualHostConfigMap.buildMappings().size() );
    }

    @Test
    void testEnabled()
    {
        map.put( "enabled", "true" );
        assertTrue( new VirtualHostConfigMap( map ).isEnabled() );
    }

    @Test
    void testDisabled()
    {
        map.put( "enabled", "false" );
        assertFalse( new VirtualHostConfigMap( map ).isEnabled() );
    }


    @Test
    void testDefaultVirtualHostConfig()
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
    void testGetSource()
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
    void testGetTarget()
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
    void testHostOrder()
    {
        map.put( "mapping.myapp1.order", "3" );
        map.put( "mapping.myapp2.order", "1" );
        map.put( "mapping.myapp3.order", "2" );

        VirtualHostConfigMap virtualHostConfig = new VirtualHostConfigMap( map );

        final List<VirtualHost> virtualHosts = virtualHostConfig.buildMappings();
        assertThat( virtualHosts.stream().map( VirtualHost::getName ) ).containsExactly( "myapp2", "myapp3", "myapp1" );
    }

    @Test
    void testSourceOrder()
    {
        map.put( "mapping.myapp1.source", "/a" );
        map.put( "mapping.myapp2.source", "/a/b/c" );
        map.put( "mapping.myapp3.source", "/a/b" );

        VirtualHostConfigMap virtualHostConfig = new VirtualHostConfigMap( map );

        final List<VirtualHost> virtualHosts = virtualHostConfig.buildMappings();
        assertThat( virtualHosts.stream().map( VirtualHost::getName ) ).containsExactly( "myapp2", "myapp3", "myapp1" );
    }

    @Test
    void testDefaultIdProvider()
    {
        map.put( "mapping.myapp1.idProvider.system", "default" );
        map.put( "mapping.myapp1.idProvider.myProvider", "enabled" );

        VirtualHostConfigMap virtualHostConfig = new VirtualHostConfigMap( map );

        VirtualHost virtualHost = virtualHostConfig.buildMappings().get( 0 );
        assertEquals( "system", virtualHost.getDefaultIdProviderKey().toString() );
        assertEquals( 2, virtualHost.getIdProviderKeys().getSize() );
        assertTrue( virtualHost.getIdProviderKeys().contains( IdProviderKey.from( "myProvider" ) ) );
    }

    @Test
    void testIdProviderFlows_default()
    {
        map.put( "mapping.myapp1.idProvider.system", "default" );

        final VirtualHost virtualHost = new VirtualHostConfigMap( map ).buildMappings().get( 0 );

        assertEquals( IdProviderFlow.DEFAULT, virtualHost.getIdProviderFlows( IdProviderKey.system() ) );
        assertEquals( Set.of(), virtualHost.getIdProviderFlows( IdProviderKey.from( "other" ) ) );
    }

    @Test
    void testIdProviderFlows_explicit()
    {
        map.put( "mapping.myapp1.idProvider.system", "default&enabled=autologin,forced" );
        map.put( "mapping.myapp1.idProvider.myProvider", "enabled=login" );

        final VirtualHost virtualHost = new VirtualHostConfigMap( map ).buildMappings().get( 0 );

        assertEquals( "system", virtualHost.getDefaultIdProviderKey().toString() );
        assertEquals( Set.of( IdProviderFlow.AUTOLOGIN, IdProviderFlow.FORCED ), virtualHost.getIdProviderFlows( IdProviderKey.system() ) );
        assertEquals( Set.of( IdProviderFlow.LOGIN ), virtualHost.getIdProviderFlows( IdProviderKey.from( "myProvider" ) ) );
    }

    @Test
    void testIdProviderFlows_unknownFlowIgnored()
    {
        map.put( "mapping.myapp1.idProvider.system", "enabled=autologin,unknown" );

        final VirtualHost virtualHost = new VirtualHostConfigMap( map ).buildMappings().get( 0 );

        assertEquals( Set.of( IdProviderFlow.AUTOLOGIN ), virtualHost.getIdProviderFlows( IdProviderKey.system() ) );
    }

    @Test
    void testIdProviderFlows_emptyListMeansDefault()
    {
        map.put( "mapping.myapp1.idProvider.system", "enabled=" );

        final VirtualHost virtualHost = new VirtualHostConfigMap( map ).buildMappings().get( 0 );

        assertEquals( IdProviderFlow.DEFAULT, virtualHost.getIdProviderFlows( IdProviderKey.system() ) );
    }

    @Test
    void testConnector_default()
    {
        map.put( "mapping.myapp1.host", "example.com" );

        final VirtualHost virtualHost = new VirtualHostConfigMap( map ).buildMappings().get( 0 );

        assertEquals( Set.of( "xp" ), virtualHost.getConnectors() );
    }

    @Test
    void testConnector_explicit()
    {
        map.put( "mapping.myapp1.host", "example.com" );
        map.put( "mapping.myapp1.connector", "api, status" );

        final VirtualHost virtualHost = new VirtualHostConfigMap( map ).buildMappings().get( 0 );

        assertEquals( Set.of( "api", "status" ), virtualHost.getConnectors() );
    }

    @Test
    void testConnector_unknownIgnored()
    {
        map.put( "mapping.myapp1.host", "example.com" );
        map.put( "mapping.myapp1.connector", "bogus, api" );

        final VirtualHost virtualHost = new VirtualHostConfigMap( map ).buildMappings().get( 0 );

        assertEquals( Set.of( "api" ), virtualHost.getConnectors() );
    }

    @Test
    void testConnector_noValidConnectors_matchesNothing()
    {
        map.put( "mapping.myapp1.host", "example.com" );
        map.put( "mapping.myapp1.connector", "bogus" );

        final VirtualHost virtualHost = new VirtualHostConfigMap( map ).buildMappings().get( 0 );

        // an explicit but invalid connector never falls back to the web connector
        assertEquals( Set.of(), virtualHost.getConnectors() );
    }

    @Test
    void test()
    {
        map.put( "mapping.myapp1.host", "example.com" );
        map.put( "mapping.myapp1.source", "/" );
        map.put( "mapping.myapp1.target", "/" );
        map.put( "mapping.myapp1.order", "1" );

        map.put( "mapping.myapp2.host", "example.com" );
        map.put( "mapping.myapp2.source", "/source" );
        map.put( "mapping.myapp2.target", "/target" );
        map.put( "mapping.myapp2.order", "1" );

        map.put( "mapping.myapp3.host", "example.com" );
        map.put( "mapping.myapp3.source", "/source" );
        map.put( "mapping.myapp3.target", "/target/path" );
        map.put( "mapping.myapp3.order", "5" );

        VirtualHostConfigMap virtualHostConfig = new VirtualHostConfigMap( map );

        final List<VirtualHost> virtualHosts = virtualHostConfig.buildMappings();

        assertEquals( "/source", virtualHosts.get( 0 ).getSource() );
        assertEquals( "/", virtualHosts.get( 1 ).getSource() );
    }

    @Test
    void testContextConfig()
    {
        map.put( "mapping.name.context.propertyName", "propertyValue" );

        VirtualHostConfigMap virtualHostConfig = new VirtualHostConfigMap( map );

        final List<VirtualHost> virtualHosts = virtualHostConfig.buildMappings();

        final Map<String, String> context = virtualHosts.get( 0 ).getContext();
        assertEquals( "propertyValue", context.get( "propertyName" ) );
    }
}
