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
        json.put( "success", true );
        json.put( "total", contents.getSize() );
        json.put( "contents", serialize( contents.getList() ) );
    }

    private JsonNode serialize( final List<Content> list )
    {
        final ArrayNode contentsNode = arrayNode();

        for ( Content content : list )
        {
            contentsNode.add( serializeContent( content ) );
        }

        return contentsNode;
    }

    private ObjectNode serializeContent( final Content content )
    {
        final ObjectNode node = objectNode();
        node.put( "path", content.getPath().toString() );
        node.put( "name", content.getName() );
        node.put( "type", content.getType() != null ? content.getType().toString() : null );
        node.put( "displayName", content.getDisplayName() );
        node.put( "owner", content.getOwner() != null ? content.getOwner().toString() : null );
        node.put( "modifier", content.getModifier() != null ? content.getModifier().toString() : null );
        node.put( "modifiedTime", content.getModifiedTime() != null ? content.getModifiedTime().toString() : null );
        node.put( "createdTime", content.getCreatedTime() != null ? content.getCreatedTime().toString() : null );
        node.put( "status", "Online" );
        node.put( "editable", true );
        node.put( "deletable", true );

        return node;
    }

}
