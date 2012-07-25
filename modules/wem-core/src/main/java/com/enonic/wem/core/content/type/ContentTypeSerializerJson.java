package com.enonic.wem.core.content.type;


import java.io.IOException;
import java.io.StringWriter;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;

import com.enonic.wem.core.content.JsonFactoryHolder;
import com.enonic.wem.core.content.type.configitem.ConfigItemsSerializerJson;

public class ContentTypeSerializerJson
{
    public static String toJson( ContentType contentType )
    {
        try
        {
            StringWriter sw = new StringWriter();
            JsonGenerator g = JsonFactoryHolder.DEFAULT_FACTORY.createJsonGenerator( sw );
            g.useDefaultPrettyPrinter();
            g.writeStartObject();
            ConfigItemsSerializerJson.generate( contentType.getConfigItems(), g );
            g.writeEndObject();
            g.close();
            sw.close();
            return sw.toString();
        }
        catch ( IOException e )
        {
            throw new RuntimeException( "Failed to generate json", e );
        }
    }

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
        contentType.setConfigItems( ConfigItemsSerializerJson.parse( contentTypeNode.get( "items" ) ) );
        return contentType;
    }
}
