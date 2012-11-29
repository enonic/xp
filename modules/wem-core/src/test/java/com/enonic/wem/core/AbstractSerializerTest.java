package com.enonic.wem.core;


import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.jdom.Document;

import com.enonic.cms.framework.util.JDOMUtil;

public class AbstractSerializerTest
{
    protected String getXmlAsString( String fileName )
    {
        try
        {
            final URL resource = getClass().getResource( fileName );
            if ( resource == null )
            {
                throw new IllegalArgumentException( "File not found: " + fileName );
            }

            Document document = JDOMUtil.parseDocument( resource.openStream() );
            return JDOMUtil.prettyPrintDocument( document );
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e );
        }
    }

    protected String getJsonAsString( String fileName )
    {
        try
        {
            return toJsonString( getJson( fileName ) );
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e );
        }
    }

    private JsonNode getJson( String fileName )
    {
        try
        {
            final ObjectMapper mapper = createObjectMapper();
            final JsonFactory factory = mapper.getJsonFactory();
            final URL resource = getClass().getResource( fileName );
            if ( resource == null )
            {
                throw new IllegalArgumentException( "File not found: " + fileName );
            }
            final JsonParser parser = factory.createJsonParser( resource );
            return parser.readValueAsTree();
        }
        catch ( IOException e )
        {
            throw new RuntimeException( e );
        }
    }

    private String toJsonString( final JsonNode value )
        throws Exception
    {
        final ObjectMapper mapper = createObjectMapper();
        return mapper.defaultPrettyPrintingWriter().writeValueAsString( value );
    }


    private static ObjectMapper createObjectMapper()
    {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.setDateFormat( new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" ) );
        return mapper;
    }
}
