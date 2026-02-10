package com.enonic.xp.web.vhost.impl;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.enonic.xp.web.dispatch.DispatchConstants;
import com.enonic.xp.web.vhost.VirtualHost;
import com.enonic.xp.web.vhost.VirtualHostService;
import com.enonic.xp.web.vhost.impl.mapping.VirtualHostIdProvidersMapping;
import com.enonic.xp.web.vhost.impl.mapping.VirtualHostMapping;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class VirtualHostFilterTest
{
    private VirtualHostService virtualHostService;

    private HttpServletRequest req;

    private HttpServletResponse res;

    private List<VirtualHost> virtualHosts;

    private FilterChain chain;

    @BeforeEach
    void setup()
    {
        this.req = mock( HttpServletRequest.class );
        this.res = mock( HttpServletResponse.class );
        this.chain = mock( FilterChain.class );

        this.virtualHosts = new ArrayList<>();
        this.virtualHostService = mock( VirtualHostService.class );

        when( virtualHostService.getVirtualHosts() ).thenReturn( this.virtualHosts );
    }

    @Test
    void testNotEnabled_localhostVhostUsed()
        throws Exception
    {
        when( this.virtualHostService.isEnabled() ).thenReturn( false );
        when( req.getServerName() ).thenReturn( "enonic.com" );

        VirtualHostFilter filter = new VirtualHostFilter( virtualHostService, new VirtualHostResolverImpl( virtualHostService ) );

        filter.doFilter( this.req, this.res, this.chain );

        verify( this.chain, times( 1 ) ).doFilter( any(), eq( this.res ) );
    }

    @Test
    void testManagementPort_localhostVhostUsed()
        throws Exception
    {
        when( this.virtualHostService.isEnabled() ).thenReturn( true );
        when( this.req.getServerName() ).thenReturn( "domain.com" );
        when( this.req.getAttribute( DispatchConstants.CONNECTOR_ATTRIBUTE ) ).thenReturn( DispatchConstants.API_CONNECTOR );

        VirtualHostFilter filter = new VirtualHostFilter( virtualHostService, new VirtualHostResolverImpl( virtualHostService ) );
        filter.doFilter( this.req, this.res, this.chain );

        verify( this.chain, times( 1 ) ).doFilter( any(), eq( this.res ) );
    }

    @Test
    void testNoMapping()
        throws Exception
    {
        when( this.virtualHostService.isEnabled() ).thenReturn( true );
        when( this.req.getServerName() ).thenReturn( "domain.com" );
        when( this.req.getAttribute( DispatchConstants.CONNECTOR_ATTRIBUTE ) ).thenReturn( DispatchConstants.XP_CONNECTOR );

        VirtualHostFilter filter = new VirtualHostFilter( virtualHostService, new VirtualHostResolverImpl( virtualHostService ) );
        filter.doFilter( this.req, this.res, this.chain );

        verify( this.chain, never() ).doFilter( this.req, this.res );
        verify( req, never() ).setAttribute( eq( VirtualHost.class.getName() ), notNull() );
        verify( res ).setStatus( 404 );
    }

    @Test
    void testMapping_notFound()
        throws Exception
    {
        addMapping();

        when( this.virtualHostService.isEnabled() ).thenReturn( true );
        when( this.req.getAttribute( DispatchConstants.CONNECTOR_ATTRIBUTE ) ).thenReturn( DispatchConstants.XP_CONNECTOR );
        when( req.getServerName() ).thenReturn( "not-exists.com" );
        when( req.getRequestURI() ).thenReturn( "/rest/status" );

        VirtualHostFilter filter = new VirtualHostFilter( virtualHostService, new VirtualHostResolverImpl( virtualHostService ) );
        filter.doFilter( this.req, this.res, this.chain );

        verify( this.chain, never() ).doFilter( this.req, this.res );
        verify( req, never() ).setAttribute( eq( VirtualHost.class.getName() ), notNull() );
        verify( res ).setStatus( 404 );
    }

    @Test
    void testMapping_found()
        throws Exception
    {
        addMapping();
        when( this.virtualHostService.isEnabled() ).thenReturn( true );
        when( this.req.getAttribute( DispatchConstants.CONNECTOR_ATTRIBUTE ) ).thenReturn( DispatchConstants.XP_CONNECTOR );
        when( req.getServerName() ).thenReturn( "enonic.com" );
        when( req.getRequestURI() ).thenReturn( "/rest/status" );
        when( req.getPathInfo() ).thenReturn( "/rest/status" );

        VirtualHostFilter filter = new VirtualHostFilter( virtualHostService, new VirtualHostResolverImpl( virtualHostService ) );
        filter.doFilter( this.req, this.res, this.chain );

        verify( req ).setAttribute( eq( VirtualHost.class.getName() ), notNull() );
        final ArgumentCaptor<HttpServletRequest> requestCaptor = forClass( HttpServletRequest.class );
        verify( this.chain ).doFilter( requestCaptor.capture(), eq( this.res ) );
        assertEquals( "/admin/rest/status", requestCaptor.getValue().getRequestURI() );
    }

    private void addMapping()
    {
        final VirtualHostMapping mapping =
            new VirtualHostMapping( "test", "enonic.com", "/rest", "/admin/rest", VirtualHostIdProvidersMapping.create().build(), 0 );
        this.virtualHosts.add( mapping );
    }
}
