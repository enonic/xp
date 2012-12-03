package com.enonic.wem.web.rest.rpc.content.type;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.type.ContentType;
import com.enonic.wem.api.content.type.ContentTypeTreeNode;
import com.enonic.wem.api.content.type.ContentTypeTree;
import com.enonic.wem.core.content.type.ContentTypeJsonSerializer;
import com.enonic.wem.web.json.JsonResult;

public class GetContentTypeTreeJsonResult
    extends JsonResult
{
    private final static ContentTypeJsonSerializer contentTypeSerializer = new ContentTypeJsonSerializer();

    private ContentTypeTree contentTypeTree;

    GetContentTypeTreeJsonResult( final ContentTypeTree contentTypeTree )
    {
        Preconditions.checkNotNull( contentTypeTree );
        this.contentTypeTree = contentTypeTree;
    }

    @Override
    protected void serialize( final ObjectNode json )
    {
        json.put( "total", contentTypeTree.deepSize() );
        json.put( "contentTypes", serialize( contentTypeTree ) );
    }

    private JsonNode serialize( final ContentTypeTree tree )
    {
        final ArrayNode contentTypesNode = arrayNode();

        for ( ContentTypeTreeNode branch : tree.getChildren() )
        {
            contentTypesNode.add( serializeBranch( branch ) );
        }

        return contentTypesNode;
    }

    private JsonNode serializeBranch( final ContentTypeTreeNode branch )
    {
        final ObjectNode contentTypeJson = serializeContentType( branch );
        final ArrayNode childArrayNode = contentTypeJson.putArray( "contentTypes" );
        contentTypeJson.put( "hasChildren", branch.hasChildren() );

        for ( ContentTypeTreeNode child : branch.getChildren() )
        {
            childArrayNode.add( serializeBranch( child ) );
        }
        return contentTypeJson;
    }


    private ObjectNode serializeContentType( final ContentTypeTreeNode contentTypeNode )
    {
        final ContentType contentType = contentTypeNode.getContentType();
        final ObjectNode contentTypeJson = (ObjectNode) contentTypeSerializer.toJson( contentType );
        return contentTypeJson;
    }
}
