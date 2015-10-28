package com.enonic.xp.admin.event.impl.json;

import java.net.URL;
import java.text.SimpleDateFormat;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import com.enonic.xp.app.ApplicationEvent;
import com.enonic.xp.event.Event2;

import static org.junit.Assert.*;

public class EventJsonSerializerTest
{
    private EventJsonSerializer serializer;

    private BundleEvent bundleEvent;

    private Bundle myBundle;

    @Before
    public void setup()
    {
        this.serializer = new EventJsonSerializer();

        bundleEvent = Mockito.mock( BundleEvent.class );
        myBundle = Mockito.mock( Bundle.class );

        Mockito.when( bundleEvent.getType() ).thenReturn( 0x00000001 );
        Mockito.when( myBundle.getSymbolicName() ).thenReturn( "myapp" );
        Mockito.when( bundleEvent.getBundle() ).thenReturn( myBundle );
    }

    @Test
    public void ApplicationEvent()
        throws Exception
    {
        final ApplicationEvent event = new ApplicationEvent( bundleEvent );
        final ObjectNode json = this.serializer.toJson( event );

        assertNotNull( json );
        assertJson( "applicationEvent.json", json );
    }

    @Test
    public void testEvent()
        throws Exception
    {
        final Event2 event = Event2.create( "node.created" ).
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
        final ObjectMapper mapper = createMapper();
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

        return Resources.toString( url, Charsets.UTF_8 );
    }

    private String toJson( final Object value )
        throws Exception
    {
        final ObjectMapper mapper = createMapper();
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString( value );
    }

    public static ObjectMapper createMapper()
    {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.setDateFormat( new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" ) );
        mapper.disable( SerializationFeature.WRITE_DATES_AS_TIMESTAMPS );
        mapper.disable( SerializationFeature.FAIL_ON_EMPTY_BEANS );
        mapper.enable( MapperFeature.SORT_PROPERTIES_ALPHABETICALLY );
        mapper.enable( SerializationFeature.WRITE_NULL_MAP_VALUES );
        mapper.setSerializationInclusion( JsonInclude.Include.ALWAYS );
        mapper.registerModule( new JavaTimeModule() );
        return mapper;
    }
}
