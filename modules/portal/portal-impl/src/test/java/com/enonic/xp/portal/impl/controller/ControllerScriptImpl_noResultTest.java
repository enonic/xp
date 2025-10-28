package com.enonic.xp.portal.impl.controller;

import org.junit.jupiter.api.Test;

import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ControllerScriptImpl_noResultTest
    extends AbstractControllerTest
{
    @Test
    void nullResult()
    {
        this.portalRequest.setMethod( HttpMethod.GET );

        execute( "myapplication:/controller/no-result.js" );
        assertEquals( HttpStatus.OK, this.portalResponse.getStatus() );
        assertNull( this.portalResponse.getBody() );
    }

    @Test
    void emptyResult()
    {
        this.portalRequest.setMethod( HttpMethod.GET );
        this.portalRequest.getParams().put( "mode", "empty" );

        execute( "myapplication:/controller/no-result.js" );
        assertEquals( HttpStatus.OK, this.portalResponse.getStatus() );
        assertNull( this.portalResponse.getBody() );
    }

    @Test
    void onlyBody()
    {
        this.portalRequest.setMethod( HttpMethod.GET );
        this.portalRequest.getParams().put( "mode", "body" );

        execute( "myapplication:/controller/no-result.js" );
        assertEquals( HttpStatus.OK, this.portalResponse.getStatus() );
        assertEquals( "content", this.portalResponse.getBody() );
    }

    @Test
    void onlyStatus()
    {
        this.portalRequest.setMethod( HttpMethod.GET );
        this.portalRequest.getParams().put( "mode", "status" );

        execute( "myapplication:/controller/no-result.js" );
        assertEquals( HttpStatus.OK, this.portalResponse.getStatus() );
        assertNull( this.portalResponse.getBody() );
    }
}
