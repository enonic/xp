package com.enonic.wem.core.content.type;


import java.io.IOException;
import java.io.StringWriter;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;

import com.enonic.wem.api.content.type.ContentType;
import com.enonic.wem.api.content.type.ContentTypeSerializer;
import com.enonic.wem.core.content.JsonFactoryHolder;
import com.enonic.wem.core.content.JsonParserUtil;
import com.enonic.wem.core.content.JsonParsingException;
import com.enonic.wem.core.content.type.formitem.FormItemsSerializerJson;

public class ContentTypeSerializerJson
    implements ContentTypeSerializer
{
    private FormItemsSerializerJson formItemsSerializer = new FormItemsSerializerJson();

    public String toString( ContentType contentType )
    {
        try
        {
            StringWriter sw = new StringWriter();
            JsonGenerator g = JsonFactoryHolder.DEFAULT_FACTORY.createJsonGenerator( sw );
            g.useDefaultPrettyPrinter();
            g.writeStartObject();
            g.writeStringField( "name", contentType.getName() );
            g.writeStringField( "module", contentType.getModule().getName() );

            formItemsSerializer.generate( contentType.getFormItems(), g );
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

    public ContentType toContentType( String json )
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

    private ContentType parse( final JsonNode contentTypeNode )
        throws IOException
    {
        final ContentType contentType = new ContentType();
        contentType.setName( JsonParserUtil.getStringValue( "name", contentTypeNode ) );

        try
        {
            contentType.setFormItems( formItemsSerializer.parse( contentTypeNode.get( "items" ) ) );
        }
        catch ( Exception e )
        {
            throw new JsonParsingException( "Failed to parse content type: " + contentTypeNode.toString(), e );
        }

        return contentType;
    }
}
