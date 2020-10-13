package com.enonic.xp.portal.impl.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.impl.ContentFixtures;
import com.enonic.xp.portal.impl.MapSerializableAssert;
import com.enonic.xp.web.HttpMethod;

public class PortalRequestMapperTest
{
    private PortalRequest portalRequest;

    private final MapSerializableAssert assertHelper = new MapSerializableAssert( PortalRequestMapperTest.class );

    @BeforeEach
    public void setup()
    {
        this.portalRequest = new PortalRequest();
        this.portalRequest.setMethod( HttpMethod.GET );
        this.portalRequest.setScheme( "http" );
        this.portalRequest.setHost( "localhost" );
        this.portalRequest.setPort( 80 );
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
    public void testSimple()
        throws Exception
    {
        assertHelper.assertJson( "request-simple.json", new PortalRequestMapper( this.portalRequest ) );
    }

    @Test
    public void testCookies()
        throws Exception
    {
        this.portalRequest.getCookies().put( "a", "1" );
        this.portalRequest.getCookies().put( "b", "2" );

        assertHelper.assertJson( "request-cookies.json", new PortalRequestMapper( this.portalRequest ) );
    }

    @Test
    public void testBody()
        throws Exception
    {
        this.portalRequest.setMethod( HttpMethod.POST );
        this.portalRequest.setContentType( "text/plain" );
        this.portalRequest.setBody( "Hello World" );

        assertHelper.assertJson( "request-body.json", new PortalRequestMapper( this.portalRequest ) );
    }
}
