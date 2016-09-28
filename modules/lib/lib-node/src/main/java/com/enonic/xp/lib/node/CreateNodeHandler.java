package com.enonic.xp.lib.node;

import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.index.IndexConfigDocument;
import com.enonic.xp.lib.node.mapper.NodeMapper;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeType;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.security.acl.AccessControlList;

import static org.apache.commons.lang.StringUtils.isBlank;

public class CreateNodeHandler
    extends BaseContextHandler
{
    private String name;

    private String parentPath;

    private Long manualOrderValue;

    private NodeType nodeType;

    private ChildOrder childOrder;

    private Map<String, Object> data;

    private IndexConfigDocument indexConfigDocument;

    private AccessControlList accessControlList;

    @Override
    protected Object doExecute()
    {
        final CreateNodeParams.Builder paramsBuilder = CreateNodeParams.create();

        if ( isBlank( this.name ) )
        {
            final NodeId nodeId = new NodeId();
            paramsBuilder.
                setNodeId( nodeId ).
                name( nodeId.toString() );
        }
        else
        {
            paramsBuilder.name( this.name );
        }

        if ( isBlank( this.parentPath ) )
        {
            paramsBuilder.parent( NodePath.ROOT );
        }
        else
        {
            paramsBuilder.parent( NodePath.create( parentPath ).build() );
        }

        paramsBuilder.data( this.data != null ? createPropertyTree( this.data ) : new PropertyTree() );

        if ( this.childOrder != null )
        {
            paramsBuilder.childOrder( this.childOrder );
        }

        if ( this.manualOrderValue != null )
        {
            paramsBuilder.manualOrderValue( this.manualOrderValue );
        }

        final Node node;

        node = this.nodeService.create( paramsBuilder.build() );
        return new NodeMapper( node );
    }

    private PropertyTree createPropertyTree( final Map<?, ?> value )
    {
        if ( value == null )
        {
            return null;
        }

        return this.translateToPropertyTree( createJson( value ) );
    }

    private JsonNode createJson( final Map<?, ?> value )
    {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.valueToTree( value );
    }

    public void setName( final String name )
    {
        this.name = name;
    }

    public void setParentPath( final String parentPath )
    {
        this.parentPath = parentPath;
    }

    public void setData( final ScriptValue data )
    {
        this.data = data != null ? data.getMap() : null;
    }

    public void setManualOrderValue( final Long manualOrderValue )
    {
        this.manualOrderValue = manualOrderValue;
    }

    public void setNodeType( final String nodeType )
    {
        this.nodeType = NodeType.from( nodeType );
    }

    public void setChildOrder( final String childOrder )
    {
        this.childOrder = ChildOrder.from( childOrder );
    }
}
