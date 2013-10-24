package com.enonic.wem.core.item.dao;

import java.util.Map;

import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.NoItemWithIdFound;
import com.enonic.wem.api.entity.Node;

class NodeById
{
    private final Map<EntityId, Node> nodeByNodeId;

    NodeById( Map<EntityId, Node> nodeByNodeId )
    {
        this.nodeByNodeId = nodeByNodeId;
    }

    public void storeNew( final Node node )
    {
        nodeByNodeId.put( node.id(), node );
    }

    public void updateExisting( final Node node )
    {
        if ( !nodeByNodeId.containsKey( node.id() ) )
        {
            throw new NoItemWithIdFound( node.id() );
        }

        nodeByNodeId.put( node.id(), node );
    }

    public boolean containsKey( final EntityId id )
    {
        return nodeByNodeId.containsKey( id );
    }

    public Node get( final EntityId id )
    {
        final Node node = nodeByNodeId.get( id );
        if ( node == null )
        {
            throw new NoItemWithIdFound( id );
        }
        return node;
    }
}
