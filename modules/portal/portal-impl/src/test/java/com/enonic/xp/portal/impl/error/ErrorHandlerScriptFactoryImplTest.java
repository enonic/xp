package com.enonic.xp.portal.impl.error;

import org.junit.jupiter.api.Test;

import com.enonic.xp.web.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ErrorHandlerScriptFactoryImplTest
    extends AbstractErrorHandlerTest
{

    @Test
    public void testGenericErrorHandler()
    {
        execute( "myapplication:/error/error.js", HttpStatus.INTERNAL_SERVER_ERROR );
        assertEquals( HttpStatus.INTERNAL_SERVER_ERROR, this.portalResponse.getStatus() );
        assertEquals( "Generic error...", this.portalResponse.getBody() );
        assertEquals( "text/plain", this.portalResponse.getContentType().toString() );
    }

    @Test
    public void testSpecificErrorHandler()
    {
        execute( "myapplication:/error/error.js", HttpStatus.NOT_FOUND );
        assertEquals( HttpStatus.NOT_FOUND, this.portalResponse.getStatus() );
        assertEquals( "Something was not found", this.portalResponse.getBody() );
        assertEquals( "text/plain", this.portalResponse.getContentType().toString() );
    }

    @Test
    public void testNoHandlersInScript()
    {
        execute( "myapplication:/error/notAnErrorHandler.js", HttpStatus.NOT_FOUND );
        assertNull( this.portalResponse );
    }

    @Test
    public void testRedirectInErrorHandler()
    {
        execute( "myapplication:/error/errorRedirect.js", HttpStatus.INTERNAL_SERVER_ERROR );
        assertEquals( HttpStatus.SEE_OTHER, this.portalResponse.getStatus() );
        assertEquals( "/other/page", this.portalResponse.getHeaders().get( "Location" ) );
    }

    @Test
    public void testChangeStatusInErrorHandler()
    {
        execute( "myapplication:/error/errorChangeStatus.js", HttpStatus.INTERNAL_SERVER_ERROR );
        assertEquals( HttpStatus.MOVED_PERMANENTLY, this.portalResponse.getStatus() );
        assertEquals( "/another/page", this.portalResponse.getHeaders().get( "Location" ) );
    }
}
