package com.enonic.wem.web.rest.rpc.content.type;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.type.BaseType;
import com.enonic.wem.api.content.type.ContentType;
import com.enonic.wem.api.support.tree.Tree;
import com.enonic.wem.api.support.tree.TreeNode;
import com.enonic.wem.core.content.type.BaseTypeJsonSerializer;
import com.enonic.wem.web.json.JsonResult;
import com.enonic.wem.web.rest.resource.content.ContentTypeImageUriResolver;

public class GetBaseTypeTreeJsonResult
    extends JsonResult
{
    private final static BaseTypeJsonSerializer baseTypeSerializer = new BaseTypeJsonSerializer();

    private Tree<BaseType> baseTypeTree;

    GetBaseTypeTreeJsonResult( final Tree<BaseType> baseTypeTree )
    {
        Preconditions.checkNotNull( baseTypeTree );
        this.baseTypeTree = baseTypeTree;
    }

    @Override
    protected void serialize( final ObjectNode json )
    {
        json.put( "total", baseTypeTree.deepSize() );
        json.put( "baseTypes", serializeTree() );
    }

    private JsonNode serializeTree()
    {
        final ArrayNode contentTypesNode = arrayNode();

        for ( TreeNode<BaseType> branch : baseTypeTree )
        {
            contentTypesNode.add( serializeNode( branch ) );
        }

        return contentTypesNode;
    }

    private JsonNode serializeNode( final TreeNode<BaseType> node )
    {
        final ObjectNode contentTypeJson = serializeBaseType( node.getObject() );
        final ArrayNode childArrayNode = contentTypeJson.putArray( "baseTypes" );
        contentTypeJson.put( "hasChildren", node.hasChildren() );

        for ( TreeNode<BaseType> child : node.getChildren() )
        {
            childArrayNode.add( serializeNode( child ) );
        }
        return contentTypeJson;
    }


    private ObjectNode serializeBaseType( final BaseType baseType )
    {
        final ObjectNode baseTypeJson = (ObjectNode) baseTypeSerializer.toJson( baseType );
        if ( baseType instanceof ContentType )
        {
            baseTypeJson.put( "iconUrl", ContentTypeImageUriResolver.resolve( ( (ContentType) baseType ).getQualifiedName() ) );
        }
        return baseTypeJson;
    }
}
