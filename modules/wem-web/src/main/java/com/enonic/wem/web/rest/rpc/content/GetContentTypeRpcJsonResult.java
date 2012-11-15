package com.enonic.wem.web.rest.rpc.content;

import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.NullNode;
import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.api.content.type.ContentType;
import com.enonic.wem.core.content.type.ContentTypeJsonSerializer;
import com.enonic.wem.web.json.JsonResult;

final class GetContentTypeRpcJsonResult
    extends JsonResult
{
    private final ContentType contentType;

    public GetContentTypeRpcJsonResult( final ContentType contentType )
    {
        this.contentType = contentType;
    }

    @Override
    protected void serialize( final ObjectNode json )
    {
        // TODO for the moment we use the same serialization format as is used for persistence (in JCR)

        final ContentTypeJsonSerializer contentTypeSerializer = new ContentTypeJsonSerializer();
        final String contentTypeSerialized = contentTypeSerializer.toString( contentType );
        json.put( "contentType", parseJson( contentTypeSerialized ) );
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
