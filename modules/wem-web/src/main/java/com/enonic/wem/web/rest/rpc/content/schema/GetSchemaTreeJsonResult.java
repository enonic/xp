package com.enonic.wem.web.rest.rpc.content.schema;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.schema.Schema;
import com.enonic.wem.api.support.tree.Tree;
import com.enonic.wem.api.support.tree.TreeNode;
import com.enonic.wem.core.content.schema.SchemaJsonSerializer;
import com.enonic.wem.web.json.JsonResult;
import com.enonic.wem.web.rest.resource.content.schema.SchemaImageUriResolver;

public class GetSchemaTreeJsonResult
    extends JsonResult
{
    private final static SchemaJsonSerializer baseTypeSerializer = new SchemaJsonSerializer();

    private Tree<Schema> baseTypeTree;

    GetSchemaTreeJsonResult( final Tree<Schema> baseTypeTree )
    {
        Preconditions.checkNotNull( baseTypeTree );
        this.baseTypeTree = baseTypeTree;
    }

    @Override
    protected void serialize( final ObjectNode json )
    {
        json.put( "total", baseTypeTree.deepSize() );
        json.put( "schemas", serializeTree() );
    }

    private JsonNode serializeTree()
    {
        final ArrayNode contentTypesNode = arrayNode();

        for ( TreeNode<Schema> branch : baseTypeTree )
        {
            contentTypesNode.add( serializeNode( branch ) );
        }

        return contentTypesNode;
    }

    private JsonNode serializeNode( final TreeNode<Schema> node )
    {
        final ObjectNode contentTypeJson = serializeBaseType( node.getObject() );
        final ArrayNode childArrayNode = contentTypeJson.putArray( "schemas" );
        contentTypeJson.put( "hasChildren", node.hasChildren() );

        for ( TreeNode<Schema> child : node.getChildren() )
        {
            childArrayNode.add( serializeNode( child ) );
        }
        return contentTypeJson;
    }


    private ObjectNode serializeBaseType( final Schema schema )
    {
        final ObjectNode baseTypeJson = (ObjectNode) baseTypeSerializer.toJson( schema );
        baseTypeJson.put( "iconUrl", SchemaImageUriResolver.resolve( schema.getBaseTypeKey() ) );
        return baseTypeJson;
    }
}
