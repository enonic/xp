package com.enonic.xp.admin.event.impl.json;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import com.enonic.xp.core.internal.json.ObjectMapperHelper;
import com.enonic.xp.event.Event;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class EventJsonSerializerTest
{
    private static final ObjectMapper MAPPER = ObjectMapperHelper.create();

    private static final ObjectWriter OBJECT_WRITER = MAPPER.writerWithDefaultPrettyPrinter();

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
            value( "collection", List.of( "a", "b" ) ).
            build();

        final String json = this.serializer.toJson( event );

        assertNotNull( json );
        assertJson( "testEvent.json", json );
    }

    private void assertJson( final String fileName, final String actual )
        throws Exception
    {
        final JsonNode expectedNode = MAPPER.readTree( readFromFile( fileName ) );
        final JsonNode actualNode = MAPPER.readTree( actual );

        final String expectedStr = OBJECT_WRITER.writeValueAsString( expectedNode );
        final String actualStr = OBJECT_WRITER.writeValueAsString( actualNode );
        assertEquals( expectedStr, actualStr );
    }

    protected String readFromFile( final String fileName )
        throws Exception
    {
        final InputStream stream =
            Objects.requireNonNull( getClass().getResourceAsStream( fileName ), "Resource file [" + fileName + "] not found" );
        try (stream)
        {
            return new String( stream.readAllBytes(), StandardCharsets.UTF_8 );
        }
    }

}
