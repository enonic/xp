package com.enonic.wem.core.content;


import java.io.IOException;
import java.io.StringWriter;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;

import com.enonic.wem.api.content.type.ContentType;
import com.enonic.wem.api.content.type.ContentTypeFetcher;
import com.enonic.wem.api.content.type.ContentTypeQualifiedName;
import com.enonic.wem.core.content.data.ContentDataSerializerJson;

public class ContentSerializerJson
    implements ContentSerializer
{
    private ContentTypeFetcher contentTypeFetcher;

    private ContentDataSerializerJson contentDataSerializer = new ContentDataSerializerJson();


    public ContentSerializerJson( final ContentTypeFetcher contentTypeFetcher )
    {
        this.contentTypeFetcher = contentTypeFetcher;
    }

    public String toString( Content content )
    {
        try
        {
            StringWriter sw = new StringWriter();
            JsonGenerator g = JsonFactoryHolder.DEFAULT_FACTORY.createJsonGenerator( sw );
            g.useDefaultPrettyPrinter();

            generate( content, g );

            g.close();
            sw.close();
            return sw.toString();
        }
        catch ( IOException e )
        {
            throw new RuntimeException( "Failed to generate json", e );
        }
    }

    public void generate( Content content, JsonGenerator g )
        throws IOException
    {
        g.writeStartObject();
        g.writeStringField( "name", content.getName() );
        if ( content.getType() != null )
        {
            g.writeStringField( "type", content.getType().getQualifiedName().toString() );
        }
        else
        {
            g.writeNullField( "type" );
        }
        contentDataSerializer.generate( content.getData(), g );
        g.writeEndObject();
    }

    public Content toContent( final String json )
    {
        try
        {
            final JsonFactory f = JsonFactoryHolder.DEFAULT_FACTORY;
            final JsonParser jp = f.createJsonParser( json );

            final ObjectMapper mapper = new ObjectMapper();
            final JsonNode contentNode = mapper.readValue( jp, JsonNode.class );

            try
            {
                return parse( contentNode );
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

    public Content parse( final JsonNode contentNode )
    {
        final Content content = new Content();
        content.setName( JsonParserUtil.getStringValue( "name", contentNode ) );

        final String typeAsString = JsonParserUtil.getStringValue( "type", contentNode, null );
        if ( typeAsString != null )
        {
            final ContentTypeQualifiedName contentTypeQualifiedName = new ContentTypeQualifiedName( typeAsString );
            final ContentType contentType = contentTypeFetcher.getContentType( contentTypeQualifiedName );
            content.setType( contentType );
        }

        content.setData( contentDataSerializer.parse( contentNode ) );

        return content;
    }
}
