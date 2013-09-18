package com.enonic.wem.admin.rpc.content;


import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.wem.admin.json.JsonResult;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.Contents;

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
            final ObjectNode contentJson = contentsNode.addObject();
            ContentJsonTemplate.forContentListing( contentJson, content );
        }
        return contentsNode;
    }

}
