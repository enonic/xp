package com.enonic.wem.admin.rest.rpc.schema.content;

import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.admin.json.JsonResult;
import com.enonic.wem.admin.rest.resource.schema.SchemaImageUriResolver;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.core.schema.content.serializer.ContentTypeJsonSerializer;

final class GetContentTypeRpcJsonResult
    extends JsonResult
{
    private final static ContentTypeJsonSerializer contentTypeSerializer = new ContentTypeJsonSerializer().includeQualifiedName( true );

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
