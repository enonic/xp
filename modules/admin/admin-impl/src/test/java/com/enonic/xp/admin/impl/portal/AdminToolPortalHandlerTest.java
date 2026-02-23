package com.enonic.xp.admin.impl.portal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.servlet.http.HttpServletRequest;

import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.exception.ExceptionRenderer;
import com.enonic.xp.web.handler.WebHandlerChain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AdminToolPortalHandlerTest
{
    private WebRequest request;

    private WebResponse response;

    private AdminToolPortalHandler handler;

    private WebHandlerChain chain;

    @BeforeEach
    void setup()
        throws Exception
    {
        final ExceptionRenderer exceptionRenderer = mock();
        when( exceptionRenderer.maybeThrow( any(), any() ) ).thenAnswer( invocation -> invocation.getArgument( 1 ) );
        this.handler = new AdminToolPortalHandler( exceptionRenderer );

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
    void testCanNotHandleAdminSite()
    {
        this.request.setRawPath( "/admin/site/edit/repo/branch/mysite" );
        assertFalse( this.handler.canHandle( request ) );
    }

    @Test
    void testDoHandleOnSlashAdmin()
        throws Exception
    {
        this.request.setRawPath( "/admin" );

        final WebResponse webResponse = this.handler.handle( this.request, response, chain );
        assertEquals( this.response, webResponse );
    }

    @Test
    void testDoHandle()
        throws Exception
    {
        this.request.setRawPath( "/admin/app/toolname" );

        final WebResponse webResponse = this.handler.handle( this.request, response, chain );
        assertEquals( this.response, webResponse );
    }

    @Test
    void testDoHandleDescriptorNotFoundFallbackToSlashAdmin()
        throws Exception
    {
        this.request.setRawPath( "/admin/app" );

        final WebResponse webResponse = this.handler.handle( this.request, response, chain );
        assertEquals( this.response, webResponse );
    }
}
