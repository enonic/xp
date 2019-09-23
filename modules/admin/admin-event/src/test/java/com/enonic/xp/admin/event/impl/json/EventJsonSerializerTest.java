package com.enonic.xp.admin.event.impl.json;

import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.io.Resources;

import com.enonic.xp.event.Event;
import com.enonic.xp.json.ObjectMapperHelper;

import static org.junit.jupiter.api.Assertions.*;

public class EventJsonSerializerTest
{
    private EventJsonSerializer serializer;

    private BundleEvent bundleEvent;

    @BeforeEach
    public void setup()
    {
        this.serializer = new EventJsonSerializer();

        bundleEvent = Mockito.mock( BundleEvent.class );
        final Bundle myBundle = Mockito.mock( Bundle.class );

        Mockito.when( bundleEvent.getType() ).thenReturn( 0x00000001 );
        Mockito.when( myBundle.getSymbolicName() ).thenReturn( "myapp" );
        Mockito.when( bundleEvent.getBundle() ).thenReturn( myBundle );
    }

    @Test
    public void testEvent()
        throws Exception
    {
        final Event event = Event.create( "node.created" ).
            timestamp( 0 ).
            value( "byte", (byte) 1 ).
            value( "short", (short) 2 ).
            value( "int", 3 ).
            value( "long", 4L ).
            value( "float", 5.6f ).
            value( "double", 6.7d ).
            value( "boolean", true ).
            value( "string", "test" ).
            build();

        final ObjectNode json = this.serializer.toJson( event );

        assertNotNull( json );
        assertJson( "testEvent.json", json );
    }

    private void assertJson( final String fileName, final JsonNode actualNode )
        throws Exception
    {
        final JsonNode expectedNode = parseJson( readFromFile( fileName ) );

        final String expectedStr = toJson( expectedNode );
        final String actualStr = toJson( actualNode );

        assertEquals( expectedStr, actualStr );
    }

    protected JsonNode parseJson( final String json )
        throws Exception
    {
        final ObjectMapper mapper = ObjectMapperHelper.create();
        return mapper.readTree( json );
    }

    protected String readFromFile( final String fileName )
        throws Exception
    {
        final URL url = getClass().getResource( fileName );
        if ( url == null )
        {
            throw new IllegalArgumentException( "Resource file [" + fileName + "] not found" );
        }

        return Resources.toString( url, StandardCharsets.UTF_8 );
    }

    private String toJson( final Object value )
        throws Exception
    {
        final ObjectMapper mapper = ObjectMapperHelper.create();
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString( value );
    }
}
