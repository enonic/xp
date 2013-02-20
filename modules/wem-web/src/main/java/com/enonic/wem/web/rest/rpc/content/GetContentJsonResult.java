package com.enonic.wem.web.rest.rpc.content;


import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.core.content.serializer.ContentJsonSerializer;
import com.enonic.wem.web.json.JsonResult;
import com.enonic.wem.web.rest.resource.content.schema.SchemaImageUriResolver;

class GetContentJsonResult
    extends JsonResult
{
    private final static ObjectMapper objectMapper = new ObjectMapper();

    private final ContentJsonSerializer contentSerializerJson;

    private Content content;

    GetContentJsonResult( final Content content )
    {
        this.content = content;
        this.contentSerializerJson = new ContentJsonSerializer( objectMapper );
    }

    @Override
    protected void serialize( final ObjectNode json )
    {
        json.put( "success", true );
        final ObjectNode contentJson = (ObjectNode) contentSerializerJson.serialize( content );
        contentJson.put( "iconUrl", SchemaImageUriResolver.resolve( content.getType() ) );
        json.put( "content", contentJson );
    }
}
