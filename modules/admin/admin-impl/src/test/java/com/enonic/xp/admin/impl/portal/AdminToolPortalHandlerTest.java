package com.enonic.xp.admin.impl.portal;

import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.net.HttpHeaders;

import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.exception.ExceptionMapper;
import com.enonic.xp.web.exception.ExceptionRenderer;
import com.enonic.xp.web.handler.WebHandlerChain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AdminToolPortalHandlerTest
{
    private WebRequest request;

    private WebResponse response;

    private AdminToolPortalHandler handler;

    private WebHandlerChain chain;

    @BeforeEach
    public void setup()
        throws Exception
    {
        final ExceptionMapper exceptionMapper = mock( ExceptionMapper.class );
        final ExceptionRenderer exceptionRenderer = mock( ExceptionRenderer.class );

        this.handler = new AdminToolPortalHandler();
        this.handler.setWebExceptionMapper( exceptionMapper );
        this.handler.setExceptionRenderer( exceptionRenderer );

        final HttpServletRequest rawRequest = mock( HttpServletRequest.class );

        this.request = new WebRequest();
        this.request.setMethod( HttpMethod.GET );
        this.request.setRawRequest( rawRequest );

        this.response = WebResponse.create().build();

        this.chain = mock( WebHandlerChain.class );
        when( chain.handle( any( WebRequest.class ), any( WebResponse.class ) ) ).thenReturn( this.response );
    }

    @Test
    void testCanHandle()
    {
        this.request.setRawPath( "/admin" );
        assertTrue( this.handler.canHandle( request ) );

        this.request.setRawPath( "/admin/app/toolname" );
        assertTrue( this.handler.canHandle( request ) );

        this.request.setRawPath( "/path" );
        assertFalse( this.handler.canHandle( request ) );
    }

    @Test
    void testDoHandleRedirect()
    {
        this.request.setRawPath( "/admin/" );

        final WebResponse webResponse = this.handler.doHandle( this.request, response, null );
        assertEquals( HttpStatus.TEMPORARY_REDIRECT, webResponse.getStatus() );
        assertEquals( "/admin", webResponse.getHeaders().get( HttpHeaders.LOCATION ) );
    }

    @Test
    void testDoHandleOnSlashAdmin()
    {
        this.request.setRawPath( "/admin" );

        final WebResponse webResponse = this.handler.doHandle( this.request, response, chain );
        assertEquals( this.response, webResponse );
    }

    @Test
    void testDoHandle()
    {
        this.request.setRawPath( "/admin/app/toolname" );

        final WebResponse webResponse = this.handler.doHandle( this.request, response, chain );
        assertEquals( this.response, webResponse );
    }

    @Test
    void testDoHandleDescriptorNotFoundFallbackToSlashAdmin()
    {
        this.request.setRawPath( "/admin/app" );

        final WebResponse webResponse = this.handler.doHandle( this.request, response, chain );
        assertEquals( this.response, webResponse );
    }
}
