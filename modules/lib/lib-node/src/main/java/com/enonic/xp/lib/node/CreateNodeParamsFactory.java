package com.enonic.xp.lib.node;

import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.index.IndexConfigDocument;
import com.enonic.xp.json.JsonToPropertyTreeTranslator;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeType;
import com.enonic.xp.script.ScriptValue;

public class CreateNodeParamsFactory
{
    public CreateNodeParamsFactory()
    {
    }

    public CreateNodeParams create( final ScriptValue value )
    {
        final PropertyTree properties = new JsonToPropertyTreeTranslator().translate( createJson( value.getMap() ) );

        final String name = properties.getString( NodePropertyConstants.NODE_NAME );
        final String parentPath = properties.getString( "_parentPath" );
        final Long manualOrderValue = properties.getLong( NodePropertyConstants.MANUAL_ORDER_VALUE );
        final String childOrder = properties.getString( NodePropertyConstants.CHILD_ORDER );
        final String nodeState = properties.getString( NodePropertyConstants.NODE_STATE );
        final String nodeType = properties.getString( NodePropertyConstants.NODE_TYPE );
        final PropertySet permissions = properties.getSet( NodePropertyConstants.PERMISSIONS );
        final PropertySet indexConfig = properties.getSet( NodePropertyConstants.INDEX_CONFIG );

        final CreateNodeParams.Builder builder = CreateNodeParams.create();
        setName( name, builder );
        setUserData( properties, builder );

        return builder.
            parent( Strings.isNullOrEmpty( parentPath ) ? NodePath.ROOT : NodePath.create( parentPath ).build() ).
            manualOrderValue( manualOrderValue ).
            childOrder( ChildOrder.from( childOrder ) ).
            nodeType( NodeType.from( nodeType ) ).
            indexConfigDocument( new IndexConfigFactory( indexConfig ).create() ).
            build();
    }

    private void setUserData( final PropertyTree properties, final CreateNodeParams.Builder builder )
    {
        final PropertyTree data = new PropertyTree();

        properties.getProperties().forEach( ( property ) -> {
            if ( !property.getName().startsWith( "_" ) )
            {
                handleUserProperty( property, data );
            }
        } );

        builder.data( data );
    }

    private void setName( final String name, final CreateNodeParams.Builder builder )
    {
        if ( Strings.isNullOrEmpty( name ) )
        {
            final NodeId nodeId = new NodeId();
            builder.setNodeId( nodeId );
            builder.name( nodeId.toString() );
        }
        else
        {
            builder.name( name );
        }
    }

    private IndexConfigDocument createIndexConfig( final PropertySet propertySet )
    {
        final String analyzer = propertySet.getString( "analyzer" );

        return null;
    }

    private void handleUserProperty( final Property property, final PropertyTree data )
    {
        property.copyTo( data.getRoot() );
    }

    private JsonNode createJson( final Map<?, ?> value )
    {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.valueToTree( value );
    }


}
