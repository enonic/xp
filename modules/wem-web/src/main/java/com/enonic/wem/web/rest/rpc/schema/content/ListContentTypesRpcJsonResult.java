package com.enonic.wem.web.rest.rpc.schema.content;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.core.schema.content.serializer.ContentTypeJsonSerializer;
import com.enonic.wem.web.json.JsonResult;
import com.enonic.wem.web.rest.resource.schema.SchemaImageUriResolver;

final class ListContentTypesRpcJsonResult
    extends JsonResult
{
    private final ContentTypeJsonSerializer contentTypeSerializer = new ContentTypeJsonSerializer().includeQualifiedName( true );

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
        contentTypeJson.put( "iconUrl", SchemaImageUriResolver.resolve( contentType.getSchemaKey() ) );
        return contentTypeJson;
    }
}
