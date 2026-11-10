package com.enonic.xp.portal.impl.controller;

import org.junit.jupiter.api.Test;

import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ControllerScriptImpl_allMethodsTest
    extends AbstractControllerTest
{
    @Test
    void testGET()
    {
        this.portalRequest.setMethod( HttpMethod.GET );
        execute( "myapplication:/controller/methods.js" );
        assertEquals( HttpStatus.OK, this.portalResponse.getStatus() );
        assertEquals( "GET handler", this.portalResponse.getBody() );
    }

    @Test
    void testPOST()
    {
        this.portalRequest.setMethod( HttpMethod.POST );
        execute( "myapplication:/controller/methods.js" );
        assertEquals( HttpStatus.CREATED, this.portalResponse.getStatus() );
        assertEquals( "POST handler", this.portalResponse.getBody() );
    }

    @Test
    void testDELETE()
    {
        this.portalRequest.setMethod( HttpMethod.DELETE );
        execute( "myapplication:/controller/methods.js" );
        assertEquals( HttpStatus.OK, this.portalResponse.getStatus() );
        assertEquals( "DELETE handler", this.portalResponse.getBody() );
    }

    @Test
    void testPATCH()
    {
        this.portalRequest.setMethod( HttpMethod.PATCH );
        execute( "myapplication:/controller/methods.js" );
        assertEquals( HttpStatus.OK, this.portalResponse.getStatus() );
        assertEquals( "PATCH handler", this.portalResponse.getBody() );
    }

    @Test
    void testLowercase_backwardCompatibility()
    {
        this.portalRequest.setMethod( HttpMethod.POST );
        execute( "myapplication:/controller/mixedMethods.js" );
        assertEquals( HttpStatus.ACCEPTED, this.portalResponse.getStatus() );
    }

    @Test
    void testLowercase_all_backwardCompatibility()
    {
        this.portalRequest.setMethod( HttpMethod.GET );
        execute( "myapplication:/controller/allHandler.js" );
        assertEquals( HttpStatus.OK, this.portalResponse.getStatus() );
    }

    @Test
    void testMethodNotSupported()
    {
        this.portalRequest.setMethod( HttpMethod.PUT );
        execute( "myapplication:/controller/methods.js" );
        assertEquals( HttpStatus.METHOD_NOT_ALLOWED, this.portalResponse.getStatus() );
    }

    @Test
    void testHEAD_fallsBackToGET()
    {
        this.portalRequest.setMethod( HttpMethod.HEAD );
        execute( "myapplication:/controller/methods.js" );
        assertEquals( HttpStatus.OK, this.portalResponse.getStatus() );
    }

    @Test
    void testOPTIONS_fallsBackToAll()
    {
        this.portalRequest.setMethod( HttpMethod.OPTIONS );
        execute( "myapplication:/controller/allHandler.js" );
        assertEquals( HttpStatus.OK, this.portalResponse.getStatus() );
    }

    @Test
    void testPreflight_neverFallsBackToAll()
    {
        this.portalRequest.setMethod( HttpMethod.OPTIONS );
        executePreflight( "myapplication:/controller/allHandler.js" );
        assertEquals( HttpStatus.METHOD_NOT_ALLOWED, this.portalResponse.getStatus() );
    }

    @Test
    void testPreflight_explicitOptionsExport()
    {
        this.portalRequest.setMethod( HttpMethod.OPTIONS );
        executePreflight( "myapplication:/controller/optionsHandler.js" );
        assertEquals( HttpStatus.NO_CONTENT, this.portalResponse.getStatus() );
        assertEquals( "https://example.com", this.portalResponse.getHeaders().get( "Access-Control-Allow-Origin" ) );
    }

    @Test
    void testPreflight_lowercaseOptionsExport()
    {
        this.portalRequest.setMethod( HttpMethod.OPTIONS );
        executePreflight( "myapplication:/controller/lowercaseOptionsHandler.js" );
        assertEquals( HttpStatus.NO_CONTENT, this.portalResponse.getStatus() );
    }
}
