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
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import com.enonic.xp.app.ApplicationEvent;
import com.enonic.xp.content.ContentChangeEvent;
import com.enonic.xp.content.ContentCreatedEvent;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPaths;
import com.enonic.xp.content.ContentPublishedEvent;
import com.enonic.xp.content.ContentUpdatedEvent;

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
        assertJson( "ApplicationEvent.json", json );
    }

    @Test
    public void contentUpdatedEvent()
        throws Exception
    {
        final ContentUpdatedEvent event = new ContentUpdatedEvent( ContentId.from( "myid" ) );
        final ObjectNode json = this.serializer.toJson( event );

        assertNotNull( json );
        assertJson( "contentUpdatedEvent.json", json );
    }

    @Test
    public void contentPublishedEvent()
        throws Exception
    {
        final ContentPublishedEvent event = new ContentPublishedEvent( ContentId.from( "myid" ) );
        final ObjectNode json = this.serializer.toJson( event );

        assertNotNull( json );
        assertJson( "contentPublishedEvent.json", json );
    }

    @Test
    public void contentCreatedEvent()
        throws Exception
    {
        final ContentCreatedEvent event = new ContentCreatedEvent( ContentId.from( "myid" ) );
        final ObjectNode json = this.serializer.toJson( event );

        assertNotNull( json );
        assertJson( "contentCreatedEvent.json", json );
    }

    @Test
    public void contentChangeEvent()
        throws Exception
    {
        final ContentChangeEvent event =
            ContentChangeEvent.from( ContentChangeEvent.ContentChangeType.PUBLISH, ContentPaths.from( "my/path" ) );
        final ObjectNode json = this.serializer.toJson( event );

        assertNotNull( json );
        assertJson( "contentChangeEvent.json", json );
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
        mapper.registerModule( new JSR310Module() );
        return mapper;
    }
}
