package com.enonic.xp.web.vhost.impl;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.FilterChain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.enonic.xp.web.vhost.VirtualHost;
import com.enonic.xp.web.vhost.VirtualHostHelper;
import com.enonic.xp.web.vhost.VirtualHostResolver;
import com.enonic.xp.web.vhost.VirtualHostService;
import com.enonic.xp.web.vhost.impl.mapping.VirtualHostMapping;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class VirtualHostFilterTest
{
    private VirtualHostFilter filter;

    private VirtualHostService virtualHostService;

    private MockHttpServletRequest req;

    private MockHttpServletResponse res;

    private List<VirtualHost> virtualHosts;

    private FilterChain chain;

    @BeforeEach
    public void setup()
    {
        this.req = Mockito.spy( new MockHttpServletRequest() );
        this.res = new MockHttpServletResponse();
        this.chain = Mockito.mock( FilterChain.class );

        this.virtualHosts = new ArrayList<>();
        this.virtualHostService = Mockito.mock( VirtualHostService.class );

        final VirtualHostResolver virtualHostResolver = new VirtualHostResolverImpl( virtualHostService );

        Mockito.when( virtualHostService.getVirtualHosts() ).thenReturn( this.virtualHosts );

        this.filter = new VirtualHostFilter( virtualHostService, virtualHostResolver );
    }

    @Test
    public void testNotEnabled()
        throws Exception
    {
        Mockito.when( this.virtualHostService.isEnabled() ).thenReturn( false );
        this.filter.doFilter( this.req, this.res, this.chain );

        Mockito.verify( this.chain, Mockito.times( 1 ) ).doFilter( this.req, this.res );
    }

    @Test
    public void testNoMapping()
        throws Exception
    {
        Mockito.when( this.virtualHostService.isEnabled() ).thenReturn( true );
        this.filter.doFilter( this.req, this.res, this.chain );

        Mockito.verify( this.chain, Mockito.times( 0 ) ).doFilter( this.req, this.res );
        assertFalse( VirtualHostHelper.hasVirtualHost( this.req ) );
        assertEquals( 404, this.res.getStatus() );
    }

    @Test
    public void testMapping_notFound()
        throws Exception
    {
        addMapping();

        Mockito.when( this.virtualHostService.isEnabled() ).thenReturn( true );
        this.filter.doFilter( this.req, this.res, this.chain );

        Mockito.verify( this.chain, Mockito.times( 0 ) ).doFilter( this.req, this.res );
        assertFalse( VirtualHostHelper.hasVirtualHost( this.req ) );
        assertEquals( 404, this.res.getStatus() );
    }

    @Test
    public void testMapping_found()
        throws Exception
    {
        addMapping();
        Mockito.when( this.virtualHostService.isEnabled() ).thenReturn( true );

        this.req.setServerName( "enonic.com" );
        this.req.setRequestURI( "/rest/status" );

        this.filter.doFilter( this.req, this.res, this.chain );

        Mockito.verify( this.chain, Mockito.times( 0 ) ).doFilter( this.req, this.res );
        Mockito.verify( this.req, Mockito.times( 1 ) ).getRequestDispatcher( "/admin/rest/status" );
        assertTrue( VirtualHostHelper.hasVirtualHost( this.req ) );

        final VirtualHost virtualHost = VirtualHostHelper.getVirtualHost( this.req );
        assertNotNull( virtualHost );
        assertEquals( "test", virtualHost.getName() );
    }

    private void addMapping()
    {
        final VirtualHostMapping mapping = new VirtualHostMapping( "test" );
        mapping.setHost( "enonic.com" );
        mapping.setSource( "/rest" );
        mapping.setTarget( "/admin/rest" );
        this.virtualHosts.add( mapping );
    }
}
