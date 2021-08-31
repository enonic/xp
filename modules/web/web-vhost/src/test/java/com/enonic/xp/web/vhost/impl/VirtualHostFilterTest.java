package com.enonic.xp.web.vhost.impl;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.web.vhost.VirtualHost;
import com.enonic.xp.web.vhost.VirtualHostHelper;
import com.enonic.xp.web.vhost.VirtualHostResolver;
import com.enonic.xp.web.vhost.VirtualHostService;
import com.enonic.xp.web.vhost.impl.mapping.VirtualHostIdProvidersMapping;
import com.enonic.xp.web.vhost.impl.mapping.VirtualHostMapping;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class VirtualHostFilterTest
{
    private VirtualHostFilter filter;

    private VirtualHostService virtualHostService;

    private HttpServletRequest req;

    private HttpServletResponse res;

    private List<VirtualHost> virtualHosts;

    private FilterChain chain;

    @BeforeEach
    public void setup()
    {
        this.req = mock( HttpServletRequest.class );
        this.res = mock( HttpServletResponse.class );
        this.chain = mock( FilterChain.class );

        this.virtualHosts = new ArrayList<>();
        this.virtualHostService = mock( VirtualHostService.class );

        final VirtualHostResolver virtualHostResolver = new VirtualHostResolverImpl( virtualHostService );

        when( virtualHostService.getVirtualHosts() ).thenReturn( this.virtualHosts );

        this.filter = new VirtualHostFilter( virtualHostService, virtualHostResolver );
    }

    @Test
    public void testNotEnabled()
        throws Exception
    {
        when( this.virtualHostService.isEnabled() ).thenReturn( false );
        when( req.getServerName() ).thenReturn( "enonic.com" );

        this.filter.doFilter( this.req, this.res, this.chain );

        verify( this.chain, times( 1 ) ).doFilter( this.req, this.res );
    }

    @Test
    public void testNoMapping()
        throws Exception
    {
        when( this.virtualHostService.isEnabled() ).thenReturn( true );
        this.filter.doFilter( this.req, this.res, this.chain );

        verify( this.chain, times( 0 ) ).doFilter( this.req, this.res );
        assertFalse( VirtualHostHelper.hasVirtualHost( this.req ) );
        verify( res ).setStatus( 404 );
    }

    @Test
    public void testMapping_notFound()
        throws Exception
    {
        addMapping();

        when( this.virtualHostService.isEnabled() ).thenReturn( true );

        this.filter.doFilter( this.req, this.res, this.chain );

        verify( this.chain, times( 0 ) ).doFilter( this.req, this.res );
        assertFalse( VirtualHostHelper.hasVirtualHost( this.req ) );
        verify( res ).setStatus( 404 );
    }

    @Test
    public void testMapping_found()
        throws Exception
    {
        addMapping();
        when( this.virtualHostService.isEnabled() ).thenReturn( true );

        when( req.getServerName() ).thenReturn( "enonic.com" );
        when( req.getRequestURI() ).thenReturn( "/rest/status" );

        final RequestDispatcher requestDispatcher = mock( RequestDispatcher.class );
        when( req.getRequestDispatcher( "/admin/rest/status" ) ).thenReturn( requestDispatcher );
        when( req.getAttribute( VirtualHost.class.getName() ) ).thenReturn( virtualHosts.get( 0 ) );

        this.filter.doFilter( this.req, this.res, this.chain );

        verify( this.chain, times( 0 ) ).doFilter( this.req, this.res );
        verify( requestDispatcher ).forward( this.req, this.res );
        assertTrue( VirtualHostHelper.hasVirtualHost( this.req ) );

        final VirtualHost virtualHost = VirtualHostHelper.getVirtualHost( this.req );
        assertNotNull( virtualHost );
        assertEquals( "test", virtualHost.getName() );
    }

    private void addMapping()
    {
        final VirtualHostMapping mapping =
            new VirtualHostMapping( "test", "enonic.com", "/rest", "/admin/rest", VirtualHostIdProvidersMapping.create().build(), 0 );
        this.virtualHosts.add( mapping );
    }
}
