package com.enonic.xp.lib.node;

import com.google.common.base.Strings;

import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeType;

import static com.enonic.xp.lib.node.NodePropertyConstants.CHILD_ORDER;
import static com.enonic.xp.lib.node.NodePropertyConstants.INDEX_CONFIG;
import static com.enonic.xp.lib.node.NodePropertyConstants.MANUAL_ORDER_VALUE;
import static com.enonic.xp.lib.node.NodePropertyConstants.NODE_NAME;
import static com.enonic.xp.lib.node.NodePropertyConstants.NODE_TYPE;
import static com.enonic.xp.lib.node.NodePropertyConstants.PARENT_PATH;
import static com.enonic.xp.lib.node.NodePropertyConstants.PERMISSIONS;

public class CreateNodeParamsFactory
{
    public CreateNodeParamsFactory()
    {
    }

    public CreateNodeParams create( final PropertyTree properties )
    {
        final String name = properties.getString( NODE_NAME );
        final String parentPath =  properties.getString( PARENT_PATH ) ;
        final Long manualOrderValue = properties.getLong( MANUAL_ORDER_VALUE );
        final String childOrder = properties.getString( CHILD_ORDER );
        final String nodeType = properties.getString( NODE_TYPE );
        final Iterable<PropertySet> permissions = properties.getSets( PERMISSIONS );
        final PropertySet indexConfig = properties.getSet( INDEX_CONFIG );

        final CreateNodeParams.Builder builder = CreateNodeParams.create();
        setName( name, builder );
        setUserData( properties, builder );

        return builder.
            parent( Strings.isNullOrEmpty( parentPath ) ? NodePath.ROOT : NodePath.create( parentPath ).build() ).
            manualOrderValue( manualOrderValue ).
            childOrder( ChildOrder.from( childOrder ) ).
            nodeType( Strings.isNullOrEmpty( nodeType ) ? NodeType.DEFAULT_NODE_COLLECTION : NodeType.from( nodeType ) ).
            indexConfigDocument( new IndexConfigFactory( indexConfig ).create() ).
            permissions( new PermissionsFactory( permissions ).create() ).
            build();
    }

    private void setUserData( final PropertyTree properties, final CreateNodeParams.Builder builder )
    {
        final PropertyTree data = new PropertyTree();

        properties.getProperties().forEach( ( property ) -> {
            if ( !property.getName().startsWith( "_" ) )
            {
                property.copyTo( data.getRoot() );
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

}
