package com.enonic.wem.admin.rpc.schema.content;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import com.google.common.base.Preconditions;

import com.enonic.wem.admin.json.JsonResult;
import com.enonic.wem.admin.rest.resource.schema.SchemaImageUriResolver;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.support.tree.Tree;
import com.enonic.wem.api.support.tree.TreeNode;
import com.enonic.wem.core.schema.content.serializer.ContentTypeJsonSerializer;

public class GetContentTypeTreeJsonResult
    extends JsonResult
{
    private final static ContentTypeJsonSerializer contentTypeSerializer = new ContentTypeJsonSerializer().includeQualifiedName( true );

    private Tree<ContentType> tree;

    GetContentTypeTreeJsonResult( final Tree<ContentType> tree )
    {
        Preconditions.checkNotNull( tree );
        this.tree = tree;
    }

    @Override
    protected void serialize( final ObjectNode json )
    {
        json.put( "total", tree.deepSize() );
        json.put( "contentTypes", serializeTree() );
    }

    private JsonNode serializeTree()
    {
        final ArrayNode contentTypesNode = arrayNode();

        for ( final TreeNode<ContentType> rootNode : tree )
        {
            contentTypesNode.add( serializeNode( rootNode ) );
        }

        return contentTypesNode;
    }

    private JsonNode serializeNode( final TreeNode<ContentType> node )
    {
        final ObjectNode contentTypeJson = serializeContentType( node.getObject() );
        final ArrayNode childArrayNode = contentTypeJson.putArray( "contentTypes" );
        contentTypeJson.put( "hasChildren", node.hasChildren() );

        for ( final TreeNode<ContentType> child : node )
        {
            childArrayNode.add( serializeNode( child ) );
        }
        return contentTypeJson;
    }

    private ObjectNode serializeContentType( final ContentType contentType )
    {
        final ObjectNode contentTypeJson = (ObjectNode) contentTypeSerializer.toJson( contentType );
        contentTypeJson.put( "iconUrl", SchemaImageUriResolver.resolve( contentType.getSchemaKey() ) );
        return contentTypeJson;
    }
}
