package com.enonic.wem.admin.rest.rpc.schema;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import com.google.common.base.Preconditions;

import com.enonic.wem.admin.json.JsonResult;
import com.enonic.wem.admin.rest.resource.schema.SchemaImageUriResolver;
import com.enonic.wem.api.schema.Schema;
import com.enonic.wem.api.support.tree.Tree;
import com.enonic.wem.api.support.tree.TreeNode;
import com.enonic.wem.core.schema.SchemaJsonSerializer;

public class GetSchemaTreeJsonResult
    extends JsonResult
{
    private final static SchemaJsonSerializer schemaSerializer = new SchemaJsonSerializer();

    private Tree<Schema> schemaTree;

    GetSchemaTreeJsonResult( final Tree<Schema> schemaTree )
    {
        Preconditions.checkNotNull( schemaTree );
        this.schemaTree = schemaTree;
    }

    @Override
    protected void serialize( final ObjectNode json )
    {
        json.put( "total", schemaTree.deepSize() );
        json.put( "schemas", serializeTree() );
    }

    private JsonNode serializeTree()
    {
        final ArrayNode contentTypesNode = arrayNode();

        for ( TreeNode<Schema> branch : schemaTree )
        {
            contentTypesNode.add( serializeNode( branch ) );
        }

        return contentTypesNode;
    }

    private JsonNode serializeNode( final TreeNode<Schema> node )
    {
        final ObjectNode contentTypeJson = serializeSchema( node.getObject() );
        final ArrayNode childArrayNode = contentTypeJson.putArray( "schemas" );
        contentTypeJson.put( "hasChildren", node.hasChildren() );

        for ( TreeNode<Schema> child : node.getChildren() )
        {
            childArrayNode.add( serializeNode( child ) );
        }
        return contentTypeJson;
    }


    private ObjectNode serializeSchema( final Schema schema )
    {
        final ObjectNode schemaJson = (ObjectNode) schemaSerializer.toJson( schema );
        schemaJson.put( "iconUrl", SchemaImageUriResolver.resolve( schema.getSchemaKey() ) );
        return schemaJson;
    }
}
