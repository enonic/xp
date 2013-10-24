package com.enonic.wem.core.item.dao;

import java.util.Map;

import com.enonic.wem.api.item.ItemId;
import com.enonic.wem.api.item.NoItemWithIdFound;
import com.enonic.wem.api.item.Node;

class NodeById
{
    private final Map<ItemId, Node> nodeByNodeId;

    NodeById( Map<ItemId, Node> nodeByNodeId )
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

    public boolean containsKey( final ItemId id )
    {
        return nodeByNodeId.containsKey( id );
    }

    public Node get( final ItemId id )
    {
        final Node node = nodeByNodeId.get( id );
        if ( node == null )
        {
            throw new NoItemWithIdFound( id );
        }
        return node;
    }
}
