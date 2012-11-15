package com.enonic.wem.core.content.type;


import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.api.content.type.ContentType;
import com.enonic.wem.api.content.type.component.Component;
import com.enonic.wem.api.content.type.component.Components;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.core.content.AbstractSerializerJson;
import com.enonic.wem.core.content.JsonParserUtil;
import com.enonic.wem.core.content.JsonParsingException;
import com.enonic.wem.core.content.type.component.ComponentsSerializerJson;

public class ContentTypeSerializerJson
    extends AbstractSerializerJson<ContentType>
    implements ContentTypeSerializer
{
    private ComponentsSerializerJson componentsSerializer = new ComponentsSerializerJson();

    @Override
    protected JsonNode serialize( final ContentType contentType, final ObjectMapper objectMapper )
    {
        final ObjectMapper mapper = new ObjectMapper();
        final ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put( "name", contentType.getName() );
        objectNode.put( "module", contentType.getModule().getName() );
        objectNode.put( "qualifiedName", contentType.getQualifiedName().toString() );
        objectNode.put( "items", componentsSerializer.serialize( contentType.getComponents(), mapper ) );
        return objectNode;
    }

    @Override
    public ContentType toContentType( String json )
    {
        return toObject( json );
    }

    @Override
    protected ContentType parse( final JsonNode contentTypeNode )
    {
        final ContentType contentType = new ContentType();
        contentType.setName( JsonParserUtil.getStringValue( "name", contentTypeNode ) );
        contentType.setModule( new Module( JsonParserUtil.getStringValue( "module", contentTypeNode ) ) );

        try
        {
            final Components components = componentsSerializer.parse( contentTypeNode.get( "items" ) );
            for ( Component component : components )
            {
                contentType.addComponent( component );
            }

        }
        catch ( Exception e )
        {
            throw new JsonParsingException( "Failed to parse content type: " + contentTypeNode.toString(), e );
        }

        return contentType;
    }
}
