package com.enonic.wem.core;


import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.jdom.Document;

import com.enonic.wem.core.support.util.JdomHelper;

public class TestUtil
{
    private final JdomHelper jdomHelper = new JdomHelper();

    private Object testInstance;

    private boolean prettyPrintJson = true;

    public TestUtil( final Object testInstance )
    {
        this.testInstance = testInstance;
    }

    public TestUtil prettyPrintJson( final boolean value )
    {
        this.prettyPrintJson = value;
        return this;
    }

    public URL getResource( final String fileName )
    {
        final URL resource = testInstance.getClass().getResource( fileName );
        if ( resource == null )
        {
            throw new IllegalArgumentException( "Resource [" + fileName + "] not found relative to: " + testInstance.getClass() );
        }
        return resource;
    }

    public JsonNode parseFileAsJson( final String fileName )
    {
        try
        {
            final ObjectMapper mapper = createObjectMapper();
            final JsonFactory factory = mapper.getJsonFactory();
            final URL resource = getResource( fileName );
            final JsonParser parser = factory.createJsonParser( resource );
            return parser.readValueAsTree();
        }
        catch ( IOException e )
        {
            throw new RuntimeException( e );
        }
    }

    public String getJsonFileAsString( String fileName )
    {
        try
        {
            return jsonToString( parseFileAsJson( fileName ) );
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e );
        }
    }

    public String jsonToString( final JsonNode value )
    {
        try
        {
            final ObjectMapper mapper = createObjectMapper();
            if ( prettyPrintJson )
            {
                return mapper.defaultPrettyPrintingWriter().writeValueAsString( value );
            }
            else
            {
                return mapper.writeValueAsString( value );
            }
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e );
        }
    }

    public JsonNode stringToJson( final String jsonString )
    {
        try
        {
            final ObjectMapper mapper = createObjectMapper();
            final JsonFactory factory = mapper.getJsonFactory();
            final JsonParser parser = factory.createJsonParser( jsonString );
            return parser.readValueAsTree();
        }
        catch ( IOException e )
        {
            throw new RuntimeException( e );
        }
    }

    public static ObjectMapper createObjectMapper()
    {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.setDateFormat( new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" ) );
        return mapper;
    }

    public String getXmlFileAsString( final String fileName )
    {
        try
        {
            final URL resource = getResource( fileName );
            Document document = this.jdomHelper.parse( resource.openStream() );
            return this.jdomHelper.serialize( document, true );
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e );
        }
    }
}
