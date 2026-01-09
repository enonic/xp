package com.enonic.xp.portal.impl.controller;

import org.junit.jupiter.api.Test;

import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ControllerScriptImpl_additionalMethodsTest
    extends AbstractControllerTest
{
    @Test
    void testOPTIONS_uppercase()
    {
        this.portalRequest.setMethod( HttpMethod.OPTIONS );
        execute( "myapplication:/controller/additional-methods.js" );
        assertEquals( HttpStatus.OK, this.portalResponse.getStatus() );
        assertEquals( "OPTIONS handler", this.portalResponse.getBody() );
    }

    @Test
    void testPUT_uppercase()
    {
        this.portalRequest.setMethod( HttpMethod.PUT );
        execute( "myapplication:/controller/additional-methods.js" );
        assertEquals( HttpStatus.OK, this.portalResponse.getStatus() );
        assertEquals( "PUT handler", this.portalResponse.getBody() );
    }

    @Test
    void testTRACE_uppercase()
    {
        this.portalRequest.setMethod( HttpMethod.TRACE );
        execute( "myapplication:/controller/additional-methods.js" );
        assertEquals( HttpStatus.OK, this.portalResponse.getStatus() );
        assertEquals( "TRACE handler", this.portalResponse.getBody() );
    }
}
