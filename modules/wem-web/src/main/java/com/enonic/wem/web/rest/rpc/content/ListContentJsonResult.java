package com.enonic.wem.web.rest.rpc.content;


import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.web.json.JsonResult;

class ListContentJsonResult
    extends JsonResult
{
    private Contents contents;

    ListContentJsonResult( final Contents contents )
    {
        this.contents = contents;
    }

    @Override
    protected void serialize( final ObjectNode json )
    {
        json.put( "total", contents.getSize() );
        json.put( "contents", serialize( contents.getList() ) );
    }

    private JsonNode serialize( final List<Content> list )
    {
        final ArrayNode contentsNode = arrayNode();

        for ( Content content : list )
        {
            serialize( contentsNode.addObject(), content );
        }

        return contentsNode;
    }

    private void serialize( final ObjectNode node, final Content content )
    {
        node.put( "path", content.getPath().toString() );
        node.put( "name", content.getName() );
        node.put( "contentType", content.getType().getQualifiedName().toString() );
    }
}
