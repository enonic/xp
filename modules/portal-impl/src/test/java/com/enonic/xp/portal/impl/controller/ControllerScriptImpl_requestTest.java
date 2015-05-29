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
        execute( "mymodule:/controller/request.js" );

        assertEquals( PortalResponse.STATUS_OK, this.portalResponse.getStatus() );
        assertJson( "simple", getResponseAsString() );
    }

    @Test
    public void testAll()
        throws Exception
    {
        this.portalRequest.setMethod( "GET" );
        this.portalRequest.setBranch( Branch.from( "master" ) );
        this.portalRequest.setMode( RenderMode.EDIT );
        this.portalRequest.setUri( "http://enonic.com/my/page" );
        this.portalRequest.getParams().put( "debug", "true" );
        this.portalRequest.getHeaders().put( "Language", "en" );
        this.portalRequest.getHeaders().put( "Cookies", "mycookie=123; other=abc;" );
        this.portalRequest.getCookies().put( "mycookie", "123" );
        this.portalRequest.getCookies().put( "other", "abc" );

        execute( "mymodule:/controller/request.js" );

        assertEquals( PortalResponse.STATUS_OK, this.portalResponse.getStatus() );
        assertJson( "all", getResponseAsString() );
    }

    @Test
    public void testHead()
        throws Exception
    {
        this.portalRequest.setMethod( "HEAD" );
        execute( "mymodule:/controller/request.js" );
        assertEquals( PortalResponse.STATUS_OK, this.portalResponse.getStatus() );
    }
}

