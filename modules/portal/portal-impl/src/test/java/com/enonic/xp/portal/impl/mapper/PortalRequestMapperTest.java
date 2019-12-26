package com.enonic.xp.portal.impl.mapper;

import java.net.URL;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.impl.ContentFixtures;
import com.enonic.xp.script.serializer.JsonMapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;
import com.enonic.xp.web.HttpMethod;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PortalRequestMapperTest
{
    private static final ObjectMapper MAPPER = new ObjectMapper().
        enable( SerializationFeature.INDENT_OUTPUT ).
        enable( SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS );

    private PortalRequest portalRequest;

    private void assertJson( final String name, final MapSerializable value )
        throws Exception
    {
        final String resource = "/" + getClass().getName().replace( '.', '/' ) + "-" + name + ".json";
        final URL url = getClass().getResource( resource );

        assertNotNull( url, "File [" + resource + "] not found" );
        final JsonNode expectedJson = MAPPER.readTree( url );

        final JsonMapGenerator generator = new JsonMapGenerator();
        value.serialize( generator );
        final JsonNode actualJson = (JsonNode) generator.getRoot();

        final String expectedStr = MAPPER.writeValueAsString( expectedJson );
        final String actualStr = MAPPER.writeValueAsString( actualJson );

        assertEquals( expectedStr, actualStr );
    }

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
        assertJson( "simple", new PortalRequestMapper( this.portalRequest ) );
    }

    @Test
    public void testCookies()
        throws Exception
    {
        this.portalRequest.getCookies().put( "a", "1" );
        this.portalRequest.getCookies().put( "b", "2" );

        assertJson( "cookies", new PortalRequestMapper( this.portalRequest ) );
    }

    @Test
    public void testBody()
        throws Exception
    {
        this.portalRequest.setMethod( HttpMethod.POST );
        this.portalRequest.setContentType( "text/plain" );
        this.portalRequest.setBody( "Hello World" );

        assertJson( "body", new PortalRequestMapper( this.portalRequest ) );
    }
}
