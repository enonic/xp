package com.enonic.xp.lib.node;

import com.enonic.xp.data.PropertyPath;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.ValueType;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.EditableNode;
import com.enonic.xp.node.NodeType;

import static com.enonic.xp.lib.node.NodePropertyConstants.CHILD_ORDER;
import static com.enonic.xp.lib.node.NodePropertyConstants.INDEX_CONFIG;
import static com.enonic.xp.lib.node.NodePropertyConstants.MANUAL_ORDER_VALUE;
import static com.enonic.xp.lib.node.NodePropertyConstants.NODE_TYPE;

class UpdateNodeExecutor
{
    private final EditableNode editableNode;

    private final PropertyTree propertyTree;

    private UpdateNodeExecutor( final Builder builder )
    {
        editableNode = builder.editableNode;
        propertyTree = builder.propertyTree;
    }

    public static Builder create()
    {
        return new Builder();
    }


    public void execute()
    {
        setSystemData();
        setUserData();
    }

    private void setSystemData()
    {
        if ( exists( propertyTree, CHILD_ORDER, ValueTypes.STRING ) )
        {
            editableNode.childOrder = ChildOrder.from( propertyTree.getString( CHILD_ORDER ) );
        }

        if ( exists( propertyTree, INDEX_CONFIG, ValueTypes.PROPERTY_SET ) )
        {
            editableNode.indexConfigDocument = new IndexConfigFactory( propertyTree.getSet( INDEX_CONFIG ) ).create();
        }

        if ( exists( propertyTree, MANUAL_ORDER_VALUE, ValueTypes.LONG ) )
        {
            editableNode.manualOrderValue = propertyTree.getLong( MANUAL_ORDER_VALUE );
        }

        if ( exists( propertyTree, NODE_TYPE, ValueTypes.STRING ) )
        {
            editableNode.nodeType = NodeType.from( propertyTree.getString( NODE_TYPE ) );
        }
    }

    private void setUserData()
    {
        final PropertyTree newPropertyTree = new PropertyTree();

        this.propertyTree.getProperties().forEach( ( property ) -> {
            if ( !property.getName().startsWith( "_" ) )
            {
                property.copyTo( newPropertyTree.getRoot() );
            }
        } );

        this.editableNode.data = newPropertyTree;
    }

    private boolean exists( final PropertyTree propertyTree, final PropertyPath path, final ValueType valueType )
    {
        if ( !propertyTree.hasProperty( path ) )
        {
            return false;
        }

        return propertyTree.getValue( path ).getType().equals( valueType );
    }

    private boolean exists( final PropertyTree propertyTree, final String name, final ValueType valueType )
    {
        return exists( propertyTree, PropertyPath.from( name ), valueType );
    }

    public static final class Builder
    {
        private EditableNode editableNode;

        private PropertyTree propertyTree;

        private Builder()
        {
        }

        public Builder editableNode( final EditableNode val )
        {
            editableNode = val;
            return this;
        }

        public Builder propertyTree( final PropertyTree val )
        {
            propertyTree = val;
            return this;
        }

        public UpdateNodeExecutor build()
        {
            return new UpdateNodeExecutor( this );
        }
    }
}
