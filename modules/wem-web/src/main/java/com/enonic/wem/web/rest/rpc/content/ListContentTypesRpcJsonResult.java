package com.enonic.wem.web.rest.rpc.content;

import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.NullNode;
import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.api.content.type.ContentType;
import com.enonic.wem.api.content.type.ContentTypes;
import com.enonic.wem.core.content.type.ContentTypeSerializerJson;
import com.enonic.wem.web.json.JsonResult;

final class ListContentTypesRpcJsonResult
    extends JsonResult
{
    private final ContentTypeSerializerJson contentTypeSerializer = new ContentTypeSerializerJson();

    private final ContentTypes contentTypes;

    public ListContentTypesRpcJsonResult( final ContentTypes contentTypes )
    {
        this.contentTypes = contentTypes;
    }

    @Override
    protected void serialize( final ObjectNode json )
    {
        final ArrayNode contentTypeArray = arrayNode();
        for ( ContentType contentType : contentTypes )
        {
            final JsonNode contentTypeJson = serializeContentType( contentType );
            contentTypeArray.add( contentTypeJson );
        }
        json.put( "contentTypes", contentTypeArray );
    }

    private JsonNode serializeContentType( final ContentType contentType )
    {
        final String contentTypeSerialized = contentTypeSerializer.toString( contentType );
        return parseJson( contentTypeSerialized );
    }

    private JsonNode parseJson( final String serializedJson )
    {
        final ObjectMapper mapper = new ObjectMapper();
        try
        {
            return mapper.readTree( serializedJson );
        }
        catch ( IOException e )
        {
            return NullNode.getInstance();
        }
    }

}
