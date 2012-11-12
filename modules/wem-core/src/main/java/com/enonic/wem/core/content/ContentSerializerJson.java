package com.enonic.wem.core.content;


import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.type.QualifiedContentTypeName;
import com.enonic.wem.core.content.data.ContentDataSerializerJson;

public class ContentSerializerJson
    extends AbstractSerializerJson<Content>
    implements ContentSerializer
{
    private ContentDataSerializerJson contentDataSerializer = new ContentDataSerializerJson();


    public ContentSerializerJson()
    {
    }

    @Override
    protected JsonNode serialize( final Content content, final ObjectMapper objectMapper )
    {
        final ObjectNode jsonContent = objectMapper.createObjectNode();

        jsonContent.put( "name", content.getName() );
        if ( content.getType() != null )
        {
            jsonContent.put( "type", content.getType().toString() );
        }
        else
        {
            jsonContent.putNull( "type" );
        }
        jsonContent.put( "data", contentDataSerializer.serialize( content.getData(), objectMapper ) );
        return jsonContent;
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
        final String name = JsonParserUtil.getStringValue( "name", contentNode );
        if ( !StringUtils.isBlank( name ) )
        {
            content.setName( name );
        }

        final String typeAsString = JsonParserUtil.getStringValue( "type", contentNode, null );
        if ( typeAsString != null )
        {
            final QualifiedContentTypeName qualifiedContentTypeName = new QualifiedContentTypeName( typeAsString );
            content.setType( qualifiedContentTypeName );
        }

        content.setData( contentDataSerializer.parse( contentNode.get( "data" ) ) );

        return content;
    }
}
