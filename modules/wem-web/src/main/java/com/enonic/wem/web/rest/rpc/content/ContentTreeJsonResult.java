package com.enonic.wem.web.rest.rpc.content;


import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentBranch;
import com.enonic.wem.api.content.ContentTree;
import com.enonic.wem.web.json.JsonResult;

class ContentTreeJsonResult
    extends JsonResult
{
    private ContentTree contentTree;

    ContentTreeJsonResult( final ContentTree contentTree )
    {
        Preconditions.checkNotNull( contentTree );
        this.contentTree = contentTree;
    }

    @Override
    protected void serialize( final ObjectNode json )
    {
        json.put( "total", contentTree.deepSize() );
        json.put( "contentTree", serialize( contentTree ) );
    }

    private JsonNode serialize( final ContentTree tree )
    {
        final ArrayNode contentsNode = arrayNode();

        for ( ContentBranch branch : tree.getBranches() )
        {
            serializeBranch( branch, contentsNode );
        }

        return contentsNode;
    }

    private void serializeBranch( final ContentBranch branch, final ArrayNode arrayNode )
    {
        //
        final ObjectNode contentNode = arrayNode.addObject();
        serializeContent( contentNode, branch.getParent() );

        final ArrayNode childArrayNode = contentNode.arrayNode();
        contentNode.put( "hasChildren", branch.hasChildren() );
        contentNode.put( "children", childArrayNode );

        for ( ContentBranch child : branch.getChildren() )
        {
            serializeBranch( child, childArrayNode );
        }
    }


    private void serializeContent( final ObjectNode contentNode, final Content content )
    {
        contentNode.put( "path", content.getPath().toString() );
        contentNode.put( "name", content.getName() );
        contentNode.put( "type", content.getType() != null ? content.getType().toString() : null );
        contentNode.put( "displayName", content.getDisplayName() );
        contentNode.put( "owner", content.getOwner() != null ? content.getOwner().toString() : null );
        contentNode.put( "createdTime", content.getCreatedTime().toString() );
        contentNode.put( "modifier", content.getModifier() != null ? content.getModifier().toString() : null );
        contentNode.put( "modifiedTime", content.getModifiedTime() != null ? content.getModifiedTime().toString() : null );
        contentNode.put( "editable", true );
        contentNode.put( "deletable", true );
    }
}
