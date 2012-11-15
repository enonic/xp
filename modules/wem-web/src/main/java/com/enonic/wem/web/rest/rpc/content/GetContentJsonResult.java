package com.enonic.wem.web.rest.rpc.content;


import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.core.content.ContentSerializerJson;
import com.enonic.wem.web.json.JsonResult;

class GetContentJsonResult
    extends JsonResult
{

    private final static ContentSerializerJson contentSerializerJson = new ContentSerializerJson();

    private final static ObjectMapper objectMapper = new ObjectMapper();

    private Content content;

    GetContentJsonResult( final Content content )
    {
        this.content = content;
    }

    @Override
    protected void serialize( final ObjectNode json )
    {
        json.put( "success", true );
        final JsonNode contentJson = contentSerializerJson.serialize( content, objectMapper );
        json.put( "content", contentJson );
    }
}
