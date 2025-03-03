package com.enonic.xp.web.vhost.impl;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.servlet.http.HttpServletRequest;

import com.enonic.xp.web.vhost.VirtualHost;
import com.enonic.xp.web.vhost.VirtualHostResolver;
import com.enonic.xp.web.vhost.VirtualHostService;
import com.enonic.xp.web.vhost.impl.mapping.VirtualHostIdProvidersMapping;
import com.enonic.xp.web.vhost.impl.mapping.VirtualHostMapping;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class VirtualHostResolverImplTest
{
    private VirtualHostMapping virtualHostMapping;

    private VirtualHostService virtualHostService;

    @BeforeEach
    public void setUp()
    {
        this.virtualHostService = mock( VirtualHostService.class );
    }

    @Test
    public void testMatches_wrongHost()
    {
        this.virtualHostMapping =
            new VirtualHostMapping( "mymapping", "foo.no", "/", "/a", VirtualHostIdProvidersMapping.create().build(), 0 );
        when( virtualHostService.getVirtualHosts() ).thenReturn( List.of( this.virtualHostMapping ) );

        VirtualHostResolver virtualHostResolver = new VirtualHostResolverImpl( virtualHostService );

        HttpServletRequest req = mock( HttpServletRequest.class );
        when( req.getServerName() ).thenReturn( "localhost" );
        when( req.getRequestURI() ).thenReturn( "/a/b" );

        assertNull( virtualHostResolver.resolveVirtualHost( req ) );
    }

    @Test
    public void testMatches_wrongSource()
    {
        this.virtualHostMapping =
            new VirtualHostMapping( "mymapping", "foo.no", "/b", "/a", VirtualHostIdProvidersMapping.create().build(), 0 );
        when( virtualHostService.getVirtualHosts() ).thenReturn( List.of( this.virtualHostMapping ) );

        VirtualHostResolver virtualHostResolver = new VirtualHostResolverImpl( virtualHostService );

        HttpServletRequest req = mock( HttpServletRequest.class );
        when( req.getServerName() ).thenReturn( "foo.no" );
        when( req.getRequestURI() ).thenReturn( "/a" );

        assertNull( virtualHostResolver.resolveVirtualHost( req ) );
    }

    @Test
    public void testMatches_host()
    {
        this.virtualHostMapping =
            new VirtualHostMapping( "mymapping", "foo.no", "/", "/a", VirtualHostIdProvidersMapping.create().build(), 0 );
        when( virtualHostService.getVirtualHosts() ).thenReturn( List.of( this.virtualHostMapping ) );

        VirtualHostResolver virtualHostResolver = new VirtualHostResolverImpl( virtualHostService );

        HttpServletRequest req = mock( HttpServletRequest.class );
        when( req.getServerName() ).thenReturn( "foo.no" );
        when( req.getRequestURI() ).thenReturn( "/a/b" );

        assertNotNull( virtualHostResolver.resolveVirtualHost( req ) );
    }

    @Test
    public void testResolve()
    {
        final List<VirtualHost> virtualHosts = new ArrayList<>();

        virtualHosts.add( createVirtualHostMapping( "a", "localhost", "/", "/other/a", 0 ) );
        virtualHosts.add( createVirtualHostMapping( "b", "enonic.com ~example\\.(?<cc>.+)", "/", "/other/d", 0 ) );

        when( virtualHostService.getVirtualHosts() ).thenReturn( virtualHosts );

        HttpServletRequest req = mock( HttpServletRequest.class );
        when( req.getServerName() ).thenReturn( "eXampLe.com" );

        final VirtualHostResolver virtualHostResolver = new VirtualHostResolverImpl( virtualHostService );

        final VirtualHost mapping = virtualHostResolver.resolveVirtualHost( req );

        assertNotNull( mapping );
        assertEquals( "b", mapping.getName() );
    }

    @Test
    public void testResolve_notFound()
    {
        final List<VirtualHost> virtualHosts = new ArrayList<>();

        virtualHosts.add( createVirtualHostMapping( "a", "localhost", "/", "/other/a", 0 ) );
        virtualHosts.add( createVirtualHostMapping( "b", "domain.com", "/", "/other/b", 0 ) );

        when( virtualHostService.getVirtualHosts() ).thenReturn( virtualHosts );

        HttpServletRequest req = mock( HttpServletRequest.class );
        when( req.getServerName() ).thenReturn( "foo.no" );

        final VirtualHostResolver virtualHostResolver = new VirtualHostResolverImpl( virtualHostService );

        final VirtualHost mapping = virtualHostResolver.resolveVirtualHost( req );

        assertNull( mapping );
    }

    @Test
    public void testResolve_multipleHosts()
    {
        final List<VirtualHost> virtualHosts = new ArrayList<>();
        virtualHosts.add( createVirtualHostMapping( "a", "subdomain.com", "/source", "/other/a", 1 ) );
        virtualHosts.add( createVirtualHostMapping( "c", "domain.com ~(?<sub>.+)\\.domain\\.com", "/", "/other/c/${sub}", 1 ) );
        virtualHosts.add( createVirtualHostMapping( "b", "domain.com", "/", "/other/b", 1 ) );
        virtualHosts.add( createVirtualHostMapping( "d", "no.domain.com", "/", "/other/d", 1 ) );
        virtualHosts.add( createVirtualHostMapping( "e", "~.+", "/", "/other/e", 2 ) );

        when( virtualHostService.getVirtualHosts() ).thenReturn( virtualHosts );

        HttpServletRequest req = mock( HttpServletRequest.class );
        when( req.getServerName() ).thenReturn( "no.domain.com" );
        when( req.getRequestURI() ).thenReturn( URI.create( "https://no.domain.com" ).getPath() );

        final VirtualHostResolver virtualHostResolver = new VirtualHostResolverImpl( virtualHostService );
        VirtualHost mapping = virtualHostResolver.resolveVirtualHost( req );

        assertNotNull( mapping );
        assertEquals( "c", mapping.getName() );
        assertEquals( "/other/c/no", mapping.getTarget() );
        assertEquals( "no.domain.com", mapping.getHost() );

        when( req.getServerName() ).thenReturn( "foo.com" );
        when( req.getRequestURI() ).thenReturn( URI.create( "https://foo.com" ).getPath() );

        mapping = virtualHostResolver.resolveVirtualHost( req );

        assertNotNull( mapping );
        assertEquals( "e", mapping.getName() );
    }

    @Test
    public void test_matchesVHostWithLongestSourceAndEqualHosts()
    {
        final List<VirtualHost> virtualHosts = new ArrayList<>();
        virtualHosts.add( createVirtualHostMapping( "a", "no.domain.com", "/source", "/other/a", 0 ) );
        virtualHosts.add( createVirtualHostMapping( "b", "~(?<sub>.+)\\.domain\\.com", "/source/path", "/other/b/${sub}", 0 ) );

        when( virtualHostService.getVirtualHosts() ).thenReturn( virtualHosts );

        HttpServletRequest req = mock( HttpServletRequest.class );
        when( req.getServerName() ).thenReturn( "no.domain.com" );
        when( req.getRequestURI() ).thenReturn( URI.create( "https://no.domain.com/source/path/123" ).getPath() );

        final VirtualHostResolver virtualHostResolver = new VirtualHostResolverImpl( virtualHostService );
        VirtualHost mapping = virtualHostResolver.resolveVirtualHost( req );

        assertNotNull( mapping );
        assertEquals( "b", mapping.getName() );
        assertEquals( "/other/b/no", mapping.getTarget() );
        assertEquals( "no.domain.com", mapping.getHost() );
    }

    @Test
    public void testResolve_multipleHosts_sortedBySource_reversed()
    {
        final List<VirtualHost> virtualHosts = new ArrayList<>();
        virtualHosts.add( createVirtualHostMapping( "a", "no.domain.com", "/source", "/other/a", 1 ) );
        virtualHosts.add( createVirtualHostMapping( "b", "domain.com ~(?<sub>.+)\\.domain\\.com", "/", "/other/b/${sub}", 1 ) );
        virtualHosts.add( createVirtualHostMapping( "c", "domain.com", "/", "/other/c", 1 ) );

        when( virtualHostService.getVirtualHosts() ).thenReturn( virtualHosts );

        HttpServletRequest req = mock( HttpServletRequest.class );
        when( req.getServerName() ).thenReturn( "no.domain.com" );
        when( req.getRequestURI() ).thenReturn( URI.create( "https://no.domain.com" ).getPath() );

        final VirtualHostResolver virtualHostResolver = new VirtualHostResolverImpl( virtualHostService );
        VirtualHost mapping = virtualHostResolver.resolveVirtualHost( req );

        assertNotNull( mapping );
        assertEquals( "b", mapping.getName() );
        assertEquals( "/other/b/no", mapping.getTarget() );
        assertEquals( "/", mapping.getSource() );
    }

    @Test
    public void testMatchesHostInLowerCase()
    {
        final List<VirtualHost> virtualHosts = new ArrayList<>();
        virtualHosts.add( createVirtualHostMapping( "a", "doMain.com", "/source", "/other/a", 1 ) );
        virtualHosts.add( createVirtualHostMapping( "b", "no.domain.com", "/", "/other/b", 1 ) );

        when( virtualHostService.getVirtualHosts() ).thenReturn( virtualHosts );

        HttpServletRequest req = mock( HttpServletRequest.class );
        when( req.getRequestURI() ).thenReturn( URI.create( "https://domain.com/source" ).getPath() );
        when( req.getServerName() ).thenReturn( "DoMaIn.com" );

        final VirtualHostResolver virtualHostResolver = new VirtualHostResolverImpl( virtualHostService );
        VirtualHost mapping = virtualHostResolver.resolveVirtualHost( req );

        assertNotNull( mapping );
        assertEquals( "a", mapping.getName() );
        assertEquals( "/other/a", mapping.getTarget() );
    }

    @Test
    public void testResolve_multipleHosts_sortedBySource_natural()
    {
        final List<VirtualHost> virtualHosts = new ArrayList<>();
        virtualHosts.add( createVirtualHostMapping( "a", "no.domain.com", "/", "/other/a", 1 ) );
        virtualHosts.add( createVirtualHostMapping( "b", "domain.com ~(?<sub>.+)\\.domain\\.com", "/source", "/other/b/${sub}", 1 ) );
        virtualHosts.add( createVirtualHostMapping( "c", "domain.com", "/", "/other/c", 1 ) );

        when( virtualHostService.getVirtualHosts() ).thenReturn( virtualHosts );

        HttpServletRequest req = mock( HttpServletRequest.class );
        when( req.getServerName() ).thenReturn( "no.domain.com" );
        when( req.getRequestURI() ).thenReturn( URI.create( "https://no.domain.com" ).getPath() );

        final VirtualHostResolver virtualHostResolver = new VirtualHostResolverImpl( virtualHostService );
        VirtualHost mapping = virtualHostResolver.resolveVirtualHost( req );

        assertNotNull( mapping );
        assertEquals( "a", mapping.getName() );
        assertEquals( "/other/a", mapping.getTarget() );
        assertEquals( "/", mapping.getSource() );
    }

    @Test
    public void testResolve_NotMatched()
    {
        final List<VirtualHost> virtualHosts = new ArrayList<>();
        virtualHosts.add( createVirtualHostMapping( "a", "domain.com", "/source", "/other/a", 1 ) );
        virtualHosts.add( createVirtualHostMapping( "b", "domain.com", "/", "/other/b", 1 ) );

        when( virtualHostService.getVirtualHosts() ).thenReturn( virtualHosts );

        HttpServletRequest req = mock( HttpServletRequest.class );
        when( req.getServerName() ).thenReturn( "foo.com" );

        final VirtualHostResolver virtualHostResolver = new VirtualHostResolverImpl( virtualHostService );
        assertNull( virtualHostResolver.resolveVirtualHost( req ) );
    }

    @Test
    public void testResolveVirtualHostWithContextConfig()
    {
        final List<VirtualHost> virtualHosts = new ArrayList<>();
        virtualHosts.add( createVirtualHostMapping( "a", "domain.com", "/source", "/other/a", 1, Map.of( "k1", "v1", "k2", "v2" ) ) );
        virtualHosts.add( createVirtualHostMapping( "b", "domain.com", "/", "/other/b", 1 ) );

        when( virtualHostService.getVirtualHosts() ).thenReturn( virtualHosts );

        HttpServletRequest req = mock( HttpServletRequest.class );
        when( req.getRequestURI() ).thenReturn( "/source" );
        when( req.getServerName() ).thenReturn( "domain.com" );

        final VirtualHostResolver virtualHostResolver = new VirtualHostResolverImpl( virtualHostService );
        final VirtualHost virtualHost = virtualHostResolver.resolveVirtualHost( req );
        assertNotNull( virtualHost );

        final Map<String, String> contextConfig = virtualHost.getContext();
        assertNotNull( contextConfig );
        assertEquals( contextConfig.get( "k1" ), "v1" );
        assertEquals( contextConfig.get( "k2" ), "v2" );
    }

    private VirtualHostMapping createVirtualHostMapping( String name, String host, String source, String target, Integer order,
                                                         Map<String, String> contextConfig )
    {
        return new VirtualHostMapping( name, host, source, target, VirtualHostIdProvidersMapping.create().build(),
                                       Objects.requireNonNullElse( order, Integer.MAX_VALUE ), contextConfig );
    }

    private VirtualHostMapping createVirtualHostMapping( String name, String host, String source, String target, Integer order )
    {
        return createVirtualHostMapping( name, host, source, target, order, null );
    }

}
