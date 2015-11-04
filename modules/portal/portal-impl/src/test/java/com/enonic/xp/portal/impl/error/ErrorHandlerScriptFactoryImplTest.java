package com.enonic.xp.portal.impl.error;

import org.junit.Test;

import com.enonic.xp.web.HttpStatus;

import static org.junit.Assert.*;

public class ErrorHandlerScriptFactoryImplTest
    extends AbstractErrorHandlerTest
{

    @Test
    public void testGenericErrorHandler()
    {
        execute( "myapplication:/error/error.js", HttpStatus.INTERNAL_SERVER_ERROR );
        assertEquals( HttpStatus.INTERNAL_SERVER_ERROR, this.portalResponse.getStatus() );
        assertEquals( "Generic error...", this.portalResponse.getAsString() );
        assertEquals( "text/plain", this.portalResponse.getContentType().toString() );
    }

    @Test
    public void testSpecificErrorHandler()
    {
        execute( "myapplication:/error/error.js", HttpStatus.NOT_FOUND );
        assertEquals( HttpStatus.NOT_FOUND, this.portalResponse.getStatus() );
        assertEquals( "Something was not found", this.portalResponse.getAsString() );
        assertEquals( "text/plain", this.portalResponse.getContentType().toString() );
    }

    @Test
    public void testNoHandlersInScript()
    {
        execute( "myapplication:/error/notAnErrorHandler.js", HttpStatus.NOT_FOUND );
        assertNull( this.portalResponse );
    }

}
