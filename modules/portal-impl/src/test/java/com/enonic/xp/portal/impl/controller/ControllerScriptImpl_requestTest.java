package com.enonic.xp.portal.impl.controller;

import org.junit.Test;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.RenderMode;

import static org.junit.Assert.*;

public class ControllerScriptImpl_requestTest
    extends AbstractControllerTest
{
    @Test
    public void testSimple()
        throws Exception
    {
        this.portalRequest.setMethod( "GET" );
        this.portalRequest.setPort( 80 );
        execute( "myapplication:/controller/request.js" );

        assertEquals( PortalResponse.STATUS_OK, this.portalResponse.getStatus() );
        assertJson( "simple", getResponseAsString() );
    }

    @Test
    public void testAll()
        throws Exception
    {
        this.portalRequest.setMethod( "GET" );
        this.portalRequest.setScheme( "http" );
        this.portalRequest.setHost( "enonic.com" );
        this.portalRequest.setPort( 80 );
        this.portalRequest.setPath( "/my/page" );
        this.portalRequest.setUrl( "http://enonic.com/my/page?debug=true" );
        this.portalRequest.setBranch( Branch.from( "master" ) );
        this.portalRequest.setMode( RenderMode.EDIT );
        this.portalRequest.getParams().put( "debug", "true" );
        this.portalRequest.getHeaders().put( "Language", "en" );
        this.portalRequest.getHeaders().put( "Cookies", "mycookie=123; other=abc;" );
        this.portalRequest.getCookies().put( "mycookie", "123" );
        this.portalRequest.getCookies().put( "other", "abc" );

        execute( "myapplication:/controller/request.js" );

        assertEquals( PortalResponse.STATUS_OK, this.portalResponse.getStatus() );
        assertJson( "all", getResponseAsString() );
    }

    @Test
    public void testHead()
        throws Exception
    {
        this.portalRequest.setMethod( "HEAD" );
        execute( "myapplication:/controller/request.js" );
        assertEquals( PortalResponse.STATUS_OK, this.portalResponse.getStatus() );
    }
}

