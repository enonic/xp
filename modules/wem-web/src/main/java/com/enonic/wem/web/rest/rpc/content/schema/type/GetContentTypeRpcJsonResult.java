package com.enonic.wem.web.rest.rpc.content.schema.type;

import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.api.content.schema.type.ContentType;
import com.enonic.wem.core.content.schema.type.ContentTypeJsonSerializer;
import com.enonic.wem.web.json.JsonResult;
import com.enonic.wem.web.rest.resource.content.schema.SchemaImageUriResolver;

final class GetContentTypeRpcJsonResult
    extends JsonResult
{
    private final static ContentTypeJsonSerializer contentTypeSerializer = new ContentTypeJsonSerializer();

    private final ContentType contentType;

    public GetContentTypeRpcJsonResult( final ContentType contentType )
    {
        this.contentType = contentType;
    }

    @Override
    protected void serialize( final ObjectNode json )
    {
        json.put( "contentType", contentTypeSerializer.toJson( contentType ) );
        json.put( "iconUrl", SchemaImageUriResolver.resolve( contentType.getSchemaKey() ) );
    }
}
