package com.enonic.xp.web.vhost.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.enonic.xp.security.IdProviderKey;
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
        when( this.req.getAttribute( DispatchConstants.CONNECTOR_ATTRIBUTE ) ).thenReturn( DispatchConstants.WEB_CONNECTOR );
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
        when( this.req.getAttribute( DispatchConstants.CONNECTOR_ATTRIBUTE ) ).thenReturn( DispatchConstants.MANAGEMENT_CONNECTOR );

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
        when( this.req.getAttribute( DispatchConstants.CONNECTOR_ATTRIBUTE ) ).thenReturn( DispatchConstants.WEB_CONNECTOR );

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
        when( this.req.getAttribute( DispatchConstants.CONNECTOR_ATTRIBUTE ) ).thenReturn( DispatchConstants.WEB_CONNECTOR );
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
        when( this.req.getAttribute( DispatchConstants.CONNECTOR_ATTRIBUTE ) ).thenReturn( DispatchConstants.WEB_CONNECTOR );
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

    @Test
    void testManagementPort_mappingApplied()
        throws Exception
    {
        final VirtualHostMapping mapping =
            new VirtualHostMapping( "mgmt", "admin.enonic.com", "/", "/", VirtualHostIdProvidersMapping.create().build(), 0, Map.of(),
                                    DispatchConstants.MANAGEMENT_CONNECTOR );
        this.virtualHosts.add( mapping );

        when( this.virtualHostService.isEnabled() ).thenReturn( true );
        when( this.req.getServerName() ).thenReturn( "admin.enonic.com" );
        when( this.req.getPathInfo() ).thenReturn( "/repo" );
        when( this.req.getAttribute( DispatchConstants.CONNECTOR_ATTRIBUTE ) ).thenReturn( DispatchConstants.MANAGEMENT_CONNECTOR );

        VirtualHostFilter filter = new VirtualHostFilter( virtualHostService, new VirtualHostResolverImpl( virtualHostService ) );
        filter.doFilter( this.req, this.res, this.chain );

        final ArgumentCaptor<VirtualHost> vhostCaptor = forClass( VirtualHost.class );
        verify( req ).setAttribute( eq( VirtualHost.class.getName() ), vhostCaptor.capture() );
        assertEquals( "mgmt", vhostCaptor.getValue().getName() );
        verify( this.chain, times( 1 ) ).doFilter( any(), eq( this.res ) );
        verify( res, never() ).setStatus( 404 );
    }

    @Test
    void testManagementPort_noMatch_defaultVhostUsed()
        throws Exception
    {
        addMapping();

        when( this.virtualHostService.isEnabled() ).thenReturn( true );
        when( this.req.getServerName() ).thenReturn( "enonic.com" );
        when( this.req.getPathInfo() ).thenReturn( "/rest/status" );
        when( this.req.getAttribute( DispatchConstants.CONNECTOR_ATTRIBUTE ) ).thenReturn( DispatchConstants.MANAGEMENT_CONNECTOR );

        VirtualHostFilter filter = new VirtualHostFilter( virtualHostService, new VirtualHostResolverImpl( virtualHostService ) );
        filter.doFilter( this.req, this.res, this.chain );

        // the "enonic.com" mapping only applies to the xp connector, so the default vhost is used
        final ArgumentCaptor<VirtualHost> vhostCaptor = forClass( VirtualHost.class );
        verify( req ).setAttribute( eq( VirtualHost.class.getName() ), vhostCaptor.capture() );
        assertEquals( IdProviderKey.system(), vhostCaptor.getValue().getDefaultIdProviderKey() );
        // no flow restriction: interactive login is structurally impossible off the web connector
        assertEquals( Set.of(), vhostCaptor.getValue().getIdProviders().get( IdProviderKey.system() ).getFlows() );
        verify( this.chain, times( 1 ) ).doFilter( eq( this.req ), eq( this.res ) );
        verify( res, never() ).setStatus( 404 );
    }

    @Test
    void testManagementPort_mappingExistsButNoMatch_notFound()
        throws Exception
    {
        final VirtualHostMapping mapping =
            new VirtualHostMapping( "mgmt", "admin.enonic.com", "/", "/", VirtualHostIdProvidersMapping.create().build(), 0, Map.of(),
                                    DispatchConstants.MANAGEMENT_CONNECTOR );
        this.virtualHosts.add( mapping );

        when( this.virtualHostService.isEnabled() ).thenReturn( true );
        when( this.req.getServerName() ).thenReturn( "10.0.0.5" );
        when( this.req.getPathInfo() ).thenReturn( "/repo" );
        when( this.req.getAttribute( DispatchConstants.CONNECTOR_ATTRIBUTE ) ).thenReturn( DispatchConstants.MANAGEMENT_CONNECTOR );

        VirtualHostFilter filter = new VirtualHostFilter( virtualHostService, new VirtualHostResolverImpl( virtualHostService ) );
        filter.doFilter( this.req, this.res, this.chain );

        verify( this.chain, never() ).doFilter( any(), any() );
        verify( req, never() ).setAttribute( eq( VirtualHost.class.getName() ), any() );
        verify( res ).setStatus( 404 );
    }

    @Test
    void testStatusPort_mappingExistsButNoMatch_notFound()
        throws Exception
    {
        final VirtualHostMapping mapping =
            new VirtualHostMapping( "stats", "stats.enonic.com", "/", "/", VirtualHostIdProvidersMapping.create().build(), 0, Map.of(),
                                    DispatchConstants.STATISTICS_CONNECTOR );
        this.virtualHosts.add( mapping );

        when( this.virtualHostService.isEnabled() ).thenReturn( true );
        when( this.req.getServerName() ).thenReturn( "10.0.0.5" );
        when( this.req.getPathInfo() ).thenReturn( "/dump.threads" );
        when( this.req.getAttribute( DispatchConstants.CONNECTOR_ATTRIBUTE ) ).thenReturn( DispatchConstants.STATISTICS_CONNECTOR );

        VirtualHostFilter filter = new VirtualHostFilter( virtualHostService, new VirtualHostResolverImpl( virtualHostService ) );
        filter.doFilter( this.req, this.res, this.chain );

        verify( this.chain, never() ).doFilter( any(), any() );
        verify( req, never() ).setAttribute( eq( VirtualHost.class.getName() ), any() );
        verify( res ).setStatus( 404 );
    }

    @Test
    void testStatusPort_noMatch_defaultVhostUsed()
        throws Exception
    {
        when( this.virtualHostService.isEnabled() ).thenReturn( true );
        when( this.req.getServerName() ).thenReturn( "enonic.com" );
        when( this.req.getPathInfo() ).thenReturn( "/" );
        when( this.req.getAttribute( DispatchConstants.CONNECTOR_ATTRIBUTE ) ).thenReturn( DispatchConstants.STATISTICS_CONNECTOR );

        VirtualHostFilter filter = new VirtualHostFilter( virtualHostService, new VirtualHostResolverImpl( virtualHostService ) );
        filter.doFilter( this.req, this.res, this.chain );

        final ArgumentCaptor<VirtualHost> vhostCaptor = forClass( VirtualHost.class );
        verify( req ).setAttribute( eq( VirtualHost.class.getName() ), vhostCaptor.capture() );
        assertEquals( Set.of(), vhostCaptor.getValue().getIdProviders().get( IdProviderKey.system() ).getFlows() );
        verify( this.chain, times( 1 ) ).doFilter( eq( this.req ), eq( this.res ) );
    }

    @Test
    void testXpConnector_defaultVhost_hasDefaultFlows()
        throws Exception
    {
        when( this.virtualHostService.isEnabled() ).thenReturn( false );
        when( this.req.getServerName() ).thenReturn( "enonic.com" );

        VirtualHostFilter filter = new VirtualHostFilter( virtualHostService, new VirtualHostResolverImpl( virtualHostService ) );
        filter.doFilter( this.req, this.res, this.chain );

        final ArgumentCaptor<VirtualHost> vhostCaptor = forClass( VirtualHost.class );
        verify( req ).setAttribute( eq( VirtualHost.class.getName() ), vhostCaptor.capture() );
        assertEquals( Set.of(), vhostCaptor.getValue().getIdProviders().get( IdProviderKey.system() ).getFlows() );
    }

    private void addMapping()
    {
        final VirtualHostMapping mapping =
            new VirtualHostMapping( "test", "enonic.com", "/rest", "/admin/rest", VirtualHostIdProvidersMapping.create().build(), 0 );
        this.virtualHosts.add( mapping );
    }
}
