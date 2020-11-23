package com.enonic.xp.web.vhost.impl;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.web.vhost.VirtualHost;
import com.enonic.xp.web.vhost.VirtualHostResolver;
import com.enonic.xp.web.vhost.VirtualHostService;
import com.enonic.xp.web.vhost.impl.mapping.VirtualHostMapping;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class VirtualHostResolverImplTest
{
    private VirtualHostMapping virtualHostMapping;

    private VirtualHostResolver virtualHostResolver;

    @BeforeEach
    public void setUp()
    {
        this.virtualHostMapping = new VirtualHostMapping( "mymapping" );

        final VirtualHostService virtualHostService = Mockito.mock( VirtualHostService.class );
        Mockito.when( virtualHostService.getVirtualHosts() ).thenReturn( List.of( this.virtualHostMapping ) );

        this.virtualHostResolver = new VirtualHostResolverImpl( virtualHostService );
    }

    @Test
    public void testMatches_wrongHost()
    {
        this.virtualHostMapping.setHost( "foo.no" );
        this.virtualHostMapping.setSource( "/" );
        this.virtualHostMapping.setTarget( "/a" );

        HttpServletRequest req = mock( HttpServletRequest.class );
        when( req.getServerName() ).thenReturn( "localhost" );
        when( req.getRequestURI() ).thenReturn( "/a/b" );

        assertNull( virtualHostResolver.resolveVirtualHost( req ) );
    }

    @Test
    public void testMatches_wrongSource()
    {
        this.virtualHostMapping.setHost( "foo.no" );
        this.virtualHostMapping.setSource( "/b" );
        this.virtualHostMapping.setTarget( "/a" );

        HttpServletRequest req = mock( HttpServletRequest.class );
        when( req.getServerName() ).thenReturn( "foo.no" );
        when( req.getRequestURI() ).thenReturn( "/a" );

        assertNull( virtualHostResolver.resolveVirtualHost( req ) );
    }

    @Test
    public void testMatches_host()
    {
        this.virtualHostMapping.setHost( "foo.no" );
        this.virtualHostMapping.setSource( "/" );
        this.virtualHostMapping.setTarget( "/a" );

        HttpServletRequest req = mock( HttpServletRequest.class );
        when( req.getServerName() ).thenReturn( "foo.no" );
        when( req.getRequestURI() ).thenReturn( "/a/b" );

        assertNotNull( virtualHostResolver.resolveVirtualHost( req ) );
    }

    @Test
    public void testResolve()
    {
        final List<VirtualHost> virtualHosts = new ArrayList<>();

        virtualHosts.add( createVirtualHostMapping( "a", "localhost", "/", "/other/a" ) );
        virtualHosts.add( createVirtualHostMapping( "b", "enonic.com", "/", "/other/d" ) );

        final VirtualHostService virtualHostService = Mockito.mock( VirtualHostService.class );

        Mockito.when( virtualHostService.getVirtualHosts() ).thenReturn( virtualHosts );

        HttpServletRequest req = mock( HttpServletRequest.class );
        when( req.getServerName() ).thenReturn( "enonic.com" );

        final VirtualHostResolver virtualHostResolver = new VirtualHostResolverImpl( virtualHostService );

        final VirtualHost mapping = virtualHostResolver.resolveVirtualHost( req );

        assertNotNull( mapping );
        assertEquals( "b", mapping.getName() );
    }

    @Test
    public void testResolve_notFound()
    {
        final List<VirtualHost> virtualHosts = new ArrayList<>();

        virtualHosts.add( createVirtualHostMapping( "a", "localhost", "/", "/other/a" ) );
        virtualHosts.add( createVirtualHostMapping( "b", "enonic.com", "/", "/other/d" ) );

        final VirtualHostService virtualHostService = Mockito.mock( VirtualHostService.class );

        Mockito.when( virtualHostService.getVirtualHosts() ).thenReturn( virtualHosts );

        HttpServletRequest req = mock( HttpServletRequest.class );
        when( req.getServerName() ).thenReturn( "foo.no" );

        final VirtualHostResolver virtualHostResolver = new VirtualHostResolverImpl( virtualHostService );

        final VirtualHost mapping = virtualHostResolver.resolveVirtualHost( req );

        assertNull( mapping );
    }

    private VirtualHostMapping createVirtualHostMapping( String name, String host, String source, String target )
    {
        final VirtualHostMapping virtualHostMapping = new VirtualHostMapping( name );

        virtualHostMapping.setHost( host );
        virtualHostMapping.setSource( source );
        virtualHostMapping.setTarget( target );

        return virtualHostMapping;
    }

}
