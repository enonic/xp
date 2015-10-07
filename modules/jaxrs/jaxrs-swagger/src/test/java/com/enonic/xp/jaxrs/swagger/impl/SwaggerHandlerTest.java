package com.enonic.xp.jaxrs.swagger.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.enonic.xp.jaxrs.JaxRsService;
import com.enonic.xp.web.handler.WebHandlerChain;

import static org.junit.Assert.*;

public class SwaggerHandlerTest
{
    private SwaggerHandler handler;

    private MockHttpServletRequest req;

    private MockHttpServletResponse res;

    private WebHandlerChain chain;

    @Before
    public void setup()
        throws Exception
    {
        final JaxRsService jaxRsService = Mockito.mock( JaxRsService.class );

        this.handler = new SwaggerHandler();
        this.handler.setJaxRsService( jaxRsService );

        this.req = new MockHttpServletRequest();
        this.res = new MockHttpServletResponse();
        this.chain = Mockito.mock( WebHandlerChain.class );
        this.req.setMethod( "GET" );
    }

    @Test
    public void canHandle()
    {
        this.req.setRequestURI( "/something" );
        assertFalse( this.handler.canHandle( this.req ) );

        this.req.setRequestURI( "/swaggertest" );
        assertFalse( this.handler.canHandle( this.req ) );

        this.req.setRequestURI( "/swagger" );
        assertTrue( this.handler.canHandle( this.req ) );

        this.req.setRequestURI( "/swagger/anything" );
        assertTrue( this.handler.canHandle( this.req ) );
    }

    @Test
    public void methodNotAllowed()
        throws Exception
    {
        this.req.setMethod( "POST" );
        this.req.setRequestURI( "/swagger" );
        this.handler.handle( this.req, this.res, this.chain );

        assertEquals( 405, this.res.getStatus() );
    }

    @Test
    public void redirect_swagger()
        throws Exception
    {
        this.req.setRequestURI( "/swagger" );
        this.handler.handle( this.req, this.res, this.chain );

        assertEquals( "/swagger/index.html", this.res.getRedirectedUrl() );
    }

    @Test
    public void redirect_swagger_slash()
        throws Exception
    {
        this.req.setRequestURI( "/swagger/" );
        this.handler.handle( this.req, this.res, this.chain );

        assertEquals( "/swagger/index.html", this.res.getRedirectedUrl() );
    }

    @Test
    public void serveModel()
        throws Exception
    {
        this.req.setRequestURI( "/swagger/swagger.json" );
        this.handler.handle( this.req, this.res, this.chain );

        assertEquals( 200, this.res.getStatus() );
        assertEquals( "application/json; charset=utf-8", this.res.getContentType() );
    }

    @Test
    public void serveResource_indexHtml()
        throws Exception
    {
        this.req.setRequestURI( "/swagger/index.html" );
        this.handler.handle( this.req, this.res, this.chain );

        assertEquals( 200, this.res.getStatus() );
        assertEquals( "text/html", this.res.getContentType() );
    }

    @Test
    public void serveResource_notFound()
        throws Exception
    {
        this.req.setRequestURI( "/swagger/not/found.txt" );
        this.handler.handle( this.req, this.res, this.chain );

        assertEquals( 404, this.res.getStatus() );
    }
}
