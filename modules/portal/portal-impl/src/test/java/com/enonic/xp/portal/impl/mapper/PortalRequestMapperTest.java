package com.enonic.xp.portal.impl.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.impl.ContentFixtures;
import com.enonic.xp.portal.impl.MapSerializableAssert;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.web.HttpMethod;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;


class PortalRequestMapperTest
{
    private PortalRequest portalRequest;

    private final MapSerializableAssert assertHelper = new MapSerializableAssert( PortalRequestMapperTest.class );

    @BeforeEach
    void setup()
    {
        this.portalRequest = new PortalRequest();
        this.portalRequest.setMethod( HttpMethod.GET );
        this.portalRequest.setScheme( "http" );
        this.portalRequest.setHost( "localhost" );
        this.portalRequest.setPort( 80 );
        this.portalRequest.setRepositoryId( RepositoryId.from( "com.enonic.cms.myproject" ) );
        this.portalRequest.setBranch( ContentConstants.BRANCH_DRAFT );
        this.portalRequest.setRemoteAddress( "10.0.0.1" );
        this.portalRequest.setPath( "/site/live/master/a/b" );
        this.portalRequest.setRawPath( "/site/live/master/a/b" );
        this.portalRequest.setContextPath( "/site/live/master/a" );
        this.portalRequest.setUrl( "http://localhost/site/live/master/a/b?param1=value1" );
        this.portalRequest.setValidTicket( Boolean.TRUE );
        this.portalRequest.getParams().put( "param1", "value1" );
        this.portalRequest.getParams().put( "param2", "value2" );
        this.portalRequest.getParams().put( "param3", "value3-A" );
        this.portalRequest.getParams().put( "param3", "value3-B" );

        this.portalRequest.getHeaders().put( "header1", "value1" );
        this.portalRequest.getHeaders().put( "header2", "value2" );
        this.portalRequest.getHeaders().put( "header3", "value3" );

        this.portalRequest.setApplicationKey( ApplicationKey.from( "myapplication" ) );
        this.portalRequest.setContent( ContentFixtures.newContent() );
        this.portalRequest.setSite( ContentFixtures.newSite() );
        this.portalRequest.setPageDescriptor( ContentFixtures.newPageDescriptor() );
    }

    @Test
    void testSimple()
    {
        assertHelper.assertJson( "request-simple.json", new PortalRequestMapper( this.portalRequest ) );
    }

    @Test
    void testCookies()
    {
        this.portalRequest.getCookies().put( "a", "1" );
        this.portalRequest.getCookies().put( "b", "2" );

        assertHelper.assertJson( "request-cookies.json", new PortalRequestMapper( this.portalRequest ) );
    }

    @Test
    void testBody()
    {
        this.portalRequest.setMethod( HttpMethod.POST );
        this.portalRequest.setContentType( "text/plain" );
        this.portalRequest.setBody( "Hello World" );

        assertHelper.assertJson( "request-body.json", new PortalRequestMapper( this.portalRequest ) );
    }

    @Test
    void getHeader()
    {
        final ScriptValue value = MapSerializableAssert.serializeJs( new PortalRequestMapper( this.portalRequest ) );
        assertEquals( "value1", value.getMember( "getHeader" ).call( "Header1" ).getValue( String.class ) );
        assertEquals( "value1", value.getMember( "getHeader" ).call( "header1" ).getValue( String.class ) );
        assertEquals( "value2", value.getMember( "getHeader" ).call( "hEader2" ).getValue( String.class ) );
        assertNull( value.getMember( "getHeader" ).call( "header4" ) );
    }
}
