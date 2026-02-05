package com.enonic.xp.portal.impl.controller;

import org.junit.jupiter.api.Test;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ControllerScriptImpl_requestTest
    extends AbstractControllerTest
{
    @Test
    void testSimple()
    {
        this.portalRequest.setMethod( HttpMethod.GET );
        this.portalRequest.setMode( RenderMode.LIVE );
        this.portalRequest.setPort( 80 );

        execute( "myapplication:/controller/request.js" );

        assertEquals( HttpStatus.OK, this.portalResponse.getStatus() );
        assertBodyJson( "simple.json" );
    }

    @Test
    void testAll()
    {
        this.portalRequest.setMethod( HttpMethod.GET );
        this.portalRequest.setScheme( "http" );
        this.portalRequest.setHost( "enonic.com" );
        this.portalRequest.setPort( 80 );
        this.portalRequest.setRawPath( "/my/page" );
        this.portalRequest.setPath( "/my/page" );
        this.portalRequest.setUrl( "http://enonic.com/my/page?debug=true" );
        this.portalRequest.setRepositoryId( RepositoryId.from( "com.enonic.cms.myproject" ) );
        this.portalRequest.setBranch( Branch.from( "master" ) );
        this.portalRequest.setMode( RenderMode.EDIT );
        this.portalRequest.getParams().put( "debug", "true" );
        this.portalRequest.getHeaders().put( "Language", "en" );
        this.portalRequest.getHeaders().put( "Cookies", "mycookie=123; other=abc;" );
        this.portalRequest.getCookies().put( "mycookie", "123" );
        this.portalRequest.getCookies().put( "other", "abc" );

        execute( "myapplication:/controller/request.js" );

        assertEquals( HttpStatus.OK, this.portalResponse.getStatus() );
        assertBodyJson( "all.json" );
    }

    @Test
    void testCookiesParsedFromResponse()
    {
        this.portalRequest.setMethod( HttpMethod.GET );
        this.portalRequest.setScheme( "http" );
        this.portalRequest.setHost( "enonic.com" );
        this.portalRequest.setPort( 80 );
        this.portalRequest.setPath( "/my/page" );
        this.portalRequest.setUrl( "http://enonic.com/my/page?debug=true" );
        this.portalRequest.setBranch( Branch.from( "master" ) );
        this.portalRequest.setMode( RenderMode.EDIT );
        this.portalRequest.getParams().put( "debug", "true" );
        this.portalRequest.getHeaders().put( "Language", "en" );

        execute( "myapplication:/controller/requestWithCookies.js" );

        assertEquals( HttpStatus.OK, this.portalResponse.getStatus() );
        assertNotNull( this.portalResponse.getCookies() );
        assertEquals( 4, this.portalResponse.getCookies().size() );
        assertEquals( "plain1", this.portalResponse.getCookies().get( 0 ).getName() );
        assertEquals( "value2", this.portalResponse.getCookies().get( 1 ).getValue() );
        assertEquals( 2000, this.portalResponse.getCookies().get( 2 ).getMaxAge() );
        assertEquals( "/valid/path", this.portalResponse.getCookies().get( 2 ).getPath() );
        assertEquals( "Strict", this.portalResponse.getCookies().get( 2 ).getAttribute( "SameSite" ) );
        assertTrue( this.portalResponse.getCookies().get( 3 ).getSecure() );
        assertTrue( this.portalResponse.getCookies().get( 3 ).isHttpOnly() );
        assertEquals( "enonic.com", this.portalResponse.getCookies().get( 3 ).getDomain() );
    }

    @Test
    void testHead()
    {
        this.portalRequest.setMethod( HttpMethod.HEAD );
        execute( "myapplication:/controller/request.js" );
        assertEquals( HttpStatus.OK, this.portalResponse.getStatus() );
    }

    @Test
    void testAllMethod()
    {
        this.portalRequest.setMethod( HttpMethod.GET );
        execute( "myapplication:/controller/allHandler.js" );
        assertEquals( HttpStatus.OK, this.portalResponse.getStatus() );
    }
}

