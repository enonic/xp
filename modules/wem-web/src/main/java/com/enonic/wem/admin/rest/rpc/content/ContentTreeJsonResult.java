package com.enonic.wem.admin.rest.rpc.content;


import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import com.google.common.base.Preconditions;

import com.enonic.wem.admin.json.JsonResult;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.support.tree.Tree;
import com.enonic.wem.api.support.tree.TreeNode;

class ContentTreeJsonResult
    extends JsonResult
{
    private Tree<Content> contentTree;

    ContentTreeJsonResult( final Tree<Content> contentTree )
    {
        Preconditions.checkNotNull( contentTree );
        this.contentTree = contentTree;
    }

    @Override
    protected void serialize( final ObjectNode json )
    {
        json.put( "total", contentTree.deepSize() );
        json.put( "contents", serialize( contentTree ) );
    }

    private JsonNode serialize( final Tree<Content> tree )
    {
        final ArrayNode contentsNode = arrayNode();

        for ( TreeNode<Content> node : tree )
        {
            serializeNode( node, contentsNode );
        }

        return contentsNode;
    }

    private void serializeNode( final TreeNode<Content> node, final ArrayNode arrayNode )
    {
        //
        final ObjectNode contentNode = arrayNode.addObject();
        ContentJsonTemplate.forContentListing( contentNode, node.getObject() );

        final ArrayNode childArrayNode = contentNode.arrayNode();
        contentNode.put( "hasChildren", node.hasChildren() );
        contentNode.put( "contents", childArrayNode );

        for ( TreeNode<Content> child : node )
        {
            serializeNode( child, childArrayNode );
        }
    }
}
