package com.enonic.wem.web.rest.rpc.content.type;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.api.content.type.ContentType;
import com.enonic.wem.api.content.type.ContentTypes;
import com.enonic.wem.core.content.type.ContentTypeJsonSerializer;
import com.enonic.wem.web.json.JsonResult;
import com.enonic.wem.web.rest.resource.content.ContentTypeImageUriResolver;

final class ListContentTypesRpcJsonResult
    extends JsonResult
{
    private final ContentTypeJsonSerializer contentTypeSerializer = new ContentTypeJsonSerializer();

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
        final ObjectNode contentTypeJson = (ObjectNode) contentTypeSerializer.toJson( contentType );
        contentTypeJson.put( "iconUrl", ContentTypeImageUriResolver.resolve( contentType ) );
        return contentTypeJson;
    }
}
