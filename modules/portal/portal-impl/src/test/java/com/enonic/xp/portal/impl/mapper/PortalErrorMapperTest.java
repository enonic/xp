package com.enonic.xp.portal.impl.mapper;

import java.net.URL;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.exception.NotFoundException;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.impl.ContentFixtures;
import com.enonic.xp.portal.impl.error.PortalError;
import com.enonic.xp.script.serializer.JsonMapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PortalErrorMapperTest
{
    private static final ObjectMapper MAPPER = new ObjectMapper().
        enable( SerializationFeature.INDENT_OUTPUT ).
        enable( SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS );

    private PortalError error;

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
        final PortalRequest portalRequest = new PortalRequest();
        portalRequest.setMethod( HttpMethod.GET );
        portalRequest.setScheme( "http" );
        portalRequest.setHost( "localhost" );
        portalRequest.setPort( 80 );
        portalRequest.setPath( "/site/live/master/a/b" );
        portalRequest.setRawPath( "/site/live/master/a/b" );
        portalRequest.setContextPath( "/site/live/master/a" );
        portalRequest.setUrl( "http://localhost/site/live/master/a/b?param1=value1" );
        portalRequest.setRemoteAddress( "10.0.0.1" );
        portalRequest.getParams().put( "param1", "value1" );
        portalRequest.getParams().put( "param2", "value2" );
        portalRequest.getParams().put( "param3", "value3-A" );
        portalRequest.getParams().put( "param3", "value3-B" );

        portalRequest.getHeaders().put( "header1", "value1" );
        portalRequest.getHeaders().put( "header2", "value2" );
        portalRequest.getHeaders().put( "header3", "value3" );

        portalRequest.setApplicationKey( ApplicationKey.from( "myapplication" ) );
        portalRequest.setContent( ContentFixtures.newContent() );
        portalRequest.setSite( ContentFixtures.newSite() );
        portalRequest.setPageDescriptor( ContentFixtures.newPageDescriptor() );

        final NotFoundException exception = new NotFoundException( "Not found." )
        {
        };

        final PortalError.Builder errorBuilder = PortalError.create();
        errorBuilder.status( HttpStatus.NOT_FOUND ).
            message( "Message: Not found." ).
            exception( exception ).
            request( portalRequest );

        this.error = errorBuilder.build();
    }

    @Test
    public void testSimple()
        throws Exception
    {
        assertJson( "simple", new PortalErrorMapper( this.error ) );
    }
}
