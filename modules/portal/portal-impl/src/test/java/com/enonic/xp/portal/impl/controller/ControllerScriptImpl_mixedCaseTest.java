package com.enonic.xp.portal.impl.controller;

import org.junit.jupiter.api.Test;

import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ControllerScriptImpl_mixedCaseTest
    extends AbstractControllerTest
{
    @Test
    void testUppercaseGET_preferredOverLowercase()
    {
        this.portalRequest.setMethod( HttpMethod.GET );
        execute( "myapplication:/controller/mixed.js" );
        assertEquals( HttpStatus.OK, this.portalResponse.getStatus() );
        assertEquals( "GET uppercase", this.portalResponse.getBody() );
    }

    @Test
    void testLowercasePost_fallbackWhenUppercaseNotAvailable()
    {
        this.portalRequest.setMethod( HttpMethod.POST );
        execute( "myapplication:/controller/mixed.js" );
        assertEquals( HttpStatus.CREATED, this.portalResponse.getStatus() );
        assertEquals( "post lowercase", this.portalResponse.getBody() );
    }
}
