package com.enonic.wem.core.entity.dao;

import java.util.Map;

import com.enonic.wem.core.entity.EntityId;
import com.enonic.wem.core.entity.NoEntityWithIdFoundException;
import com.enonic.wem.core.entity.Node;

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
            throw new NoEntityWithIdFoundException( node.id() );
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
            throw new NoEntityWithIdFoundException( id );
        }
        return node;
    }
}
