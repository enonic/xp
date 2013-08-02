package com.enonic.wem.admin.rpc.schema.content;

import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.admin.json.JsonResult;
import com.enonic.wem.admin.rest.resource.schema.SchemaImageUriResolver;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.core.schema.content.serializer.ContentTypeJsonSerializer;

final class GetContentTypeJsonResult
    extends JsonResult
{
    private final static ContentTypeJsonSerializer contentTypeSerializer = new ContentTypeJsonSerializer().includeQualifiedName( true );

    private final ContentTypes contentTypes;

    public GetContentTypeJsonResult( final ContentTypes contentTypes )
    {
        this.contentTypes = contentTypes;
    }

    @Override
    protected void serialize( final ObjectNode json )
    {
        final ArrayNode array = json.putArray( "contentTypes" );

        for ( ContentType contentType : this.contentTypes )
        {
            final ObjectNode contentTypeObject = (ObjectNode) contentTypeSerializer.toJson( contentType );
            contentTypeObject.put( "iconUrl", SchemaImageUriResolver.resolve( contentType.getSchemaKey() ) );
            array.add( contentTypeObject );
        }
    }
}
