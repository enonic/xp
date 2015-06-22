package com.enonic.xp.web.vhost.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.enonic.xp.web.handler.WebHandlerChain;
import com.enonic.xp.web.vhost.VirtualHost;
import com.enonic.xp.web.vhost.VirtualHostHelper;
import com.enonic.xp.web.vhost.impl.config.VirtualHostConfig;
import com.enonic.xp.web.vhost.impl.mapping.VirtualHostMapping;
import com.enonic.xp.web.vhost.impl.mapping.VirtualHostMappings;

public class VirtualHostHandlerTest
{
    private VirtualHostHandler handler;

    private VirtualHostConfig config;

    private MockHttpServletRequest req;

    private MockHttpServletResponse res;

    private VirtualHostMappings mappings;

    private WebHandlerChain chain;

    @Before
    public void setup()
    {
        this.req = Mockito.spy( new MockHttpServletRequest() );
        this.res = new MockHttpServletResponse();
        this.chain = Mockito.mock( WebHandlerChain.class );

        this.mappings = new VirtualHostMappings();
        this.config = Mockito.mock( VirtualHostConfig.class );
        Mockito.when( this.config.getMappings() ).thenReturn( this.mappings );

        this.handler = new VirtualHostHandler();
        this.handler.setConfig( this.config );
    }

    @Test
    public void testNotEnabled()
        throws Exception
    {
        Mockito.when( this.config.isEnabled() ).thenReturn( false );
        this.handler.handle( this.req, this.res, this.chain );

        Mockito.verify( this.chain, Mockito.times( 1 ) ).handle( this.req, this.res );
    }

    @Test
    public void testNoMapping()
        throws Exception
    {
        Mockito.when( this.config.isEnabled() ).thenReturn( true );
        this.handler.handle( this.req, this.res, this.chain );

        Mockito.verify( this.chain, Mockito.times( 1 ) ).handle( this.req, this.res );
    }

    @Test
    public void testMapping_notFound()
        throws Exception
    {
        addMapping();

        Mockito.when( this.config.isEnabled() ).thenReturn( true );
        this.handler.handle( this.req, this.res, this.chain );

        Mockito.verify( this.chain, Mockito.times( 1 ) ).handle( this.req, this.res );
        Assert.assertFalse( VirtualHostHelper.hasVirtualHost( this.req ) );
    }

    @Test
    public void testMapping_found()
        throws Exception
    {
        addMapping();
        Mockito.when( this.config.isEnabled() ).thenReturn( true );

        this.req.setServerName( "enonic.com" );
        this.req.setRequestURI( "/rest/status" );

        this.handler.handle( this.req, this.res, this.chain );

        Mockito.verify( this.chain, Mockito.times( 0 ) ).handle( this.req, this.res );
        Mockito.verify( this.req, Mockito.times( 1 ) ).getRequestDispatcher( "/admin/rest/status" );
        Assert.assertTrue( VirtualHostHelper.hasVirtualHost( this.req ) );

        final VirtualHost virtualHost = VirtualHostHelper.getVirtualHost( this.req );
        Assert.assertNotNull( virtualHost );
        Assert.assertEquals( "test", virtualHost.getName() );
    }

    private void addMapping()
    {
        final VirtualHostMapping mapping = new VirtualHostMapping( "test" );
        mapping.setHost( "enonic.com" );
        mapping.setSource( "/rest" );
        mapping.setTarget( "/admin/rest" );
        this.mappings.add( mapping );
    }
}
