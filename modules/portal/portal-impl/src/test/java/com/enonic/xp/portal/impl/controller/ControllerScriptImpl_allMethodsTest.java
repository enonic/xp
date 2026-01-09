package com.enonic.xp.portal.impl.controller;

import org.junit.jupiter.api.Test;

import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ControllerScriptImpl_allMethodsTest
    extends AbstractControllerTest
{
    @Test
    void testGET_uppercase()
    {
        this.portalRequest.setMethod( HttpMethod.GET );
        execute( "myapplication:/controller/uppercase.js" );
        assertEquals( HttpStatus.OK, this.portalResponse.getStatus() );
        assertEquals( "GET handler", this.portalResponse.getBody() );
    }

    @Test
    void testPOST_uppercase()
    {
        this.portalRequest.setMethod( HttpMethod.POST );
        execute( "myapplication:/controller/uppercase.js" );
        assertEquals( HttpStatus.CREATED, this.portalResponse.getStatus() );
        assertEquals( "POST handler", this.portalResponse.getBody() );
    }

    @Test
    void testDELETE_uppercase()
    {
        this.portalRequest.setMethod( HttpMethod.DELETE );
        execute( "myapplication:/controller/uppercase.js" );
        assertEquals( HttpStatus.OK, this.portalResponse.getStatus() );
        assertEquals( "DELETE handler", this.portalResponse.getBody() );
    }

    @Test
    void testPATCH_uppercase()
    {
        this.portalRequest.setMethod( HttpMethod.PATCH );
        execute( "myapplication:/controller/uppercase.js" );
        assertEquals( HttpStatus.OK, this.portalResponse.getStatus() );
        assertEquals( "PATCH handler", this.portalResponse.getBody() );
    }

    @Test
    void testLowercase_backwardCompatibility()
    {
        this.portalRequest.setMethod( HttpMethod.GET );
        execute( "myapplication:/controller/simple.js" );
        assertEquals( HttpStatus.OK, this.portalResponse.getStatus() );
    }

    @Test
    void testLowercase_all_backwardCompatibility()
    {
        this.portalRequest.setMethod( HttpMethod.GET );
        execute( "myapplication:/controller/allHandler.js" );
        assertEquals( HttpStatus.OK, this.portalResponse.getStatus() );
    }

    @Test
    void testUppercase_ALL()
    {
        this.portalRequest.setMethod( HttpMethod.OPTIONS );
        execute( "myapplication:/controller/uppercaseAll.js" );
        assertEquals( HttpStatus.OK, this.portalResponse.getStatus() );
    }
}
