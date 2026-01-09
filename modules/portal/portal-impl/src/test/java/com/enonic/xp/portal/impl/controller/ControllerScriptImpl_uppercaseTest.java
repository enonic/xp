package com.enonic.xp.portal.impl.controller;

import org.junit.jupiter.api.Test;

import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ControllerScriptImpl_uppercaseTest
    extends AbstractControllerTest
{
    @Test
    void testUppercaseGET()
    {
        this.portalRequest.setMethod( HttpMethod.GET );
        execute( "myapplication:/controller/uppercase.js" );
        assertEquals( HttpStatus.OK, this.portalResponse.getStatus() );
        assertEquals( "GET handler", this.portalResponse.getBody() );
    }

    @Test
    void testUppercasePOST()
    {
        this.portalRequest.setMethod( HttpMethod.POST );
        execute( "myapplication:/controller/uppercase.js" );
        assertEquals( HttpStatus.CREATED, this.portalResponse.getStatus() );
        assertEquals( "POST handler", this.portalResponse.getBody() );
    }

    @Test
    void testUppercaseDELETE()
    {
        this.portalRequest.setMethod( HttpMethod.DELETE );
        execute( "myapplication:/controller/uppercase.js" );
        assertEquals( HttpStatus.OK, this.portalResponse.getStatus() );
        assertEquals( "DELETE handler", this.portalResponse.getBody() );
    }

    @Test
    void testUppercasePATCH()
    {
        this.portalRequest.setMethod( HttpMethod.PATCH );
        execute( "myapplication:/controller/uppercase.js" );
        assertEquals( HttpStatus.OK, this.portalResponse.getStatus() );
        assertEquals( "PATCH handler", this.portalResponse.getBody() );
    }

    @Test
    void testUppercaseALL()
    {
        this.portalRequest.setMethod( HttpMethod.GET );
        execute( "myapplication:/controller/uppercaseAll.js" );
        assertEquals( HttpStatus.OK, this.portalResponse.getStatus() );
    }

    @Test
    void testUppercaseALL_POST()
    {
        this.portalRequest.setMethod( HttpMethod.POST );
        execute( "myapplication:/controller/uppercaseAll.js" );
        assertEquals( HttpStatus.OK, this.portalResponse.getStatus() );
    }

    @Test
    void testMethodNotSupported()
    {
        this.portalRequest.setMethod( HttpMethod.PUT );
        execute( "myapplication:/controller/uppercase.js" );
        assertEquals( HttpStatus.METHOD_NOT_ALLOWED, this.portalResponse.getStatus() );
    }

    @Test
    void testHEAD_fallsBackToGET()
    {
        this.portalRequest.setMethod( HttpMethod.HEAD );
        execute( "myapplication:/controller/uppercase.js" );
        assertEquals( HttpStatus.OK, this.portalResponse.getStatus() );
    }
}
