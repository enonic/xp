package com.enonic.wem.web.rest.rpc.content.schema.content;

import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.api.content.schema.content.ContentType;
import com.enonic.wem.core.content.schema.content.serializer.ContentTypeJsonSerializer;
import com.enonic.wem.web.json.JsonResult;
import com.enonic.wem.web.rest.resource.content.schema.SchemaImageUriResolver;

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
