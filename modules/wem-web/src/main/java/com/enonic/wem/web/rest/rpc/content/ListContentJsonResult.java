package com.enonic.wem.web.rest.rpc.content;


import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.core.content.ContentJsonSerializer;
import com.enonic.wem.web.json.JsonResult;
import com.enonic.wem.web.rest.resource.content.ContentTypeImageUriResolver;

class ListContentJsonResult
    extends JsonResult
{
    private final static ObjectMapper objectMapper = new ObjectMapper();

    private final ContentJsonSerializer contentSerializerJson;

    private Contents contents;

    ListContentJsonResult( final Contents contents )
    {
        this.contents = contents;
        this.contentSerializerJson = new ContentJsonSerializer( objectMapper );
    }

    @Override
    protected void serialize( final ObjectNode json )
    {
        json.put( "success", true );
        json.put( "total", contents.getSize() );
        json.put( "contents", serialize( contents.getList() ) );
    }

    private JsonNode serialize( final List<Content> list )
    {
        final ArrayNode contentsNode = arrayNode();
        for ( Content content : list )
        {
            final ObjectNode contentJson = (ObjectNode) contentSerializerJson.serialize( content );
            contentJson.put( "iconUrl", ContentTypeImageUriResolver.resolve( content.getType() ) );
            contentsNode.add( contentJson );
        }
        return contentsNode;
    }

}
