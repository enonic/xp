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
        this.context.setMethod( "GET" );
        execute( "mymodule:/controller/request.js" );

        assertEquals( PortalResponse.STATUS_OK, this.response.getStatus() );
        assertJson( "simple", getResponseAsString() );
    }

    @Test
    public void testAll()
        throws Exception
    {
        this.context.setMethod( "GET" );
        this.context.setBranch( Branch.from( "master" ) );
        this.context.setMode( RenderMode.EDIT );
        this.context.setUri( "http://enonic.com/my/page" );
        this.context.getParams().put( "debug", "true" );
        this.context.getHeaders().put( "Language", "en" );
        this.context.getHeaders().put( "Cookies", "mycookie=123; other=abc;" );
        this.context.getCookies().put( "mycookie", "123" );
        this.context.getCookies().put( "other", "abc" );

        execute( "mymodule:/controller/request.js" );

        assertEquals( PortalResponse.STATUS_OK, this.response.getStatus() );
        assertJson( "all", getResponseAsString() );
    }

    @Test
    public void testHead()
        throws Exception
    {
        this.context.setMethod( "HEAD" );
        execute( "mymodule:/controller/request.js" );
        assertEquals( PortalResponse.STATUS_OK, this.response.getStatus() );
    }
}

