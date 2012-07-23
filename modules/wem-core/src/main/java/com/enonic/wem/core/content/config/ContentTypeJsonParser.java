package com.enonic.wem.core.content.config;


import java.io.IOException;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;

import com.enonic.wem.core.content.JsonFactoryHolder;
import com.enonic.wem.core.content.config.field.ConfigItemsJsonParser;

public class ContentTypeJsonParser
{
    public static ContentType parse( String json )
    {
        try
        {
            final JsonFactory f = JsonFactoryHolder.DEFAULT_FACTORY;
            final JsonParser jp = f.createJsonParser( json );
            try
            {

                ObjectMapper mapper = new ObjectMapper();
                final JsonNode contentTypeNode = mapper.readValue( jp, JsonNode.class );
                return parse( contentTypeNode );
            }
            finally
            {
                jp.close();
            }
        }
        catch ( IOException e )
        {
            throw new RuntimeException( "Failed to read json", e );
        }
    }

    public static ContentType parse( JsonNode contentTypeNode )
        throws IOException
    {
        ContentType contentType = new ContentType();
        contentType.setConfigItems( ConfigItemsJsonParser.parse( contentTypeNode.get( "items" ) ) );
        return contentType;
    }
}
