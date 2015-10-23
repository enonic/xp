package com.enonic.xp.portal.impl.exception;

import org.junit.Before;
import org.junit.Test;

import com.google.common.net.HttpHeaders;
import com.google.common.net.MediaType;

import com.enonic.xp.portal.PortalException;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.web.HttpStatus;

import static org.junit.Assert.*;

public class ExceptionRendererImplTest
{
    private ExceptionRendererImpl renderer;

    private PortalRequest request;

    @Before
    public void setup()
    {
        this.renderer = new ExceptionRendererImpl();
        this.request = new PortalRequest();
    }

    @Test
    public void render_json()
    {
        final PortalResponse res = this.renderer.render( this.request, new PortalException( HttpStatus.NOT_FOUND, "Custom message" ) );
        assertEquals( HttpStatus.NOT_FOUND, res.getStatus() );
        assertEquals( MediaType.JSON_UTF_8.withoutParameters(), res.getContentType() );

        final String body = res.getBody().toString();
        assertEquals( "{\"status\":404,\"message\":\"Custom message\"}", body );
    }

    @Test
    public void render_html()
    {
        this.request.getHeaders().put( HttpHeaders.ACCEPT, "text/html,text/*" );

        final PortalResponse res = this.renderer.render( this.request, new PortalException( HttpStatus.NOT_FOUND, "Custom message" ) );
        assertEquals( HttpStatus.NOT_FOUND, res.getStatus() );
        assertEquals( MediaType.HTML_UTF_8.withoutParameters(), res.getContentType() );

        final String body = res.getBody().toString();
        assertTrue( body.contains( "404 Not Found" ) );
        assertTrue( body.contains( "Custom message" ) );

        // Should not show exception
        assertTrue( body.contains( ExceptionRendererImplTest.class.getName() ) );
    }

    @Test
    public void render_json_withCause()
    {
        final RuntimeException cause = new RuntimeException( "Custom message" );
        final PortalResponse res = this.renderer.render( this.request, new PortalException( HttpStatus.BAD_REQUEST, cause ) );
        assertEquals( HttpStatus.BAD_REQUEST, res.getStatus() );
        assertEquals( MediaType.JSON_UTF_8.withoutParameters(), res.getContentType() );
        MediaType.create( "", "" );
       
        final String body = res.getBody().toString();
        assertEquals( "{\"status\":400,\"message\":\"Custom message (java.lang.RuntimeException)\"}", body );
    }

    @Test
    public void render_html_withCause()
    {
        this.request.getHeaders().put( HttpHeaders.ACCEPT, "text/html,text/*" );

        final RuntimeException cause = new RuntimeException( "Custom message" );
        final PortalResponse res = this.renderer.render( this.request, new PortalException( HttpStatus.BAD_REQUEST, cause ) );
        assertEquals( HttpStatus.BAD_REQUEST, res.getStatus() );
        assertEquals( MediaType.HTML_UTF_8.withoutParameters(), res.getContentType() );

        final String body = res.getBody().toString();
        assertTrue( body.contains( "400 Bad Request" ) );
        assertTrue( body.contains( "Custom message" ) );

        // Should show exception
        assertTrue( body.contains( RuntimeException.class.getName() ) );
    }
}
