package com.enonic.wem.core.item.dao;


import java.util.LinkedHashMap;

import org.joda.time.DateTime;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.item.ItemId;
import com.enonic.wem.api.item.NoItemFoundException;
import com.enonic.wem.api.item.NoNodeAtPathFound;
import com.enonic.wem.api.item.Node;
import com.enonic.wem.api.item.NodeAlreadyExist;
import com.enonic.wem.api.item.NodePath;

import static com.enonic.wem.api.item.Node.newNode;

public class NodeInMemoryDao
    implements NodeDao
{
    private final NodeIdByPath nodeIdByPath;

    private final NodeById nodeById;

    public NodeInMemoryDao()
    {
        nodeById = new NodeById( new LinkedHashMap<ItemId, Node>() );
        nodeIdByPath = new NodeIdByPath( new LinkedHashMap<NodePath, ItemId>() );
    }

    @Override
    public Node createNode( final CreateNodeArgs createNodeArgs )
    {
        Preconditions.checkArgument( createNodeArgs.parent().isAbsolute(),
                                     "Path to parent Item must be absolute: " + createNodeArgs.parent().toString() );
        if ( !nodeIdByPath.pathHasItem( createNodeArgs.parent() ) )
        {
            throw new NoNodeAtPathFound( createNodeArgs.parent() );
        }

        final Node newNode = Node.newNode().
            id( new ItemId() ).
            createdTime( DateTime.now() ).
            creator( createNodeArgs.creator() ).
            parent( createNodeArgs.parent() ).
            name( createNodeArgs.name() ).
            icon( createNodeArgs.icon() ).
            rootDataSet( createNodeArgs.rootDataSet() ).
            build();

        if ( nodeIdByPath.pathHasItem( newNode.path() ) )
        {
            throw new NodeAlreadyExist( newNode.path() );
        }

        nodeById.storeNew( newNode );
        nodeIdByPath.put( newNode.path(), newNode.id() );
        return newNode;
    }

    @Override
    public Node updateNode( final UpdateNodeArgs updateNodeArgs )
    {
        final Node existing = nodeById.get( updateNodeArgs.itemToUpdate() );

        final Node persistedNode = newNode( existing ).
            modifiedTime( DateTime.now() ).
            modifier( updateNodeArgs.updater() ).
            name( updateNodeArgs.name() ).
            icon( updateNodeArgs.icon() ).
            rootDataSet( updateNodeArgs.rootDataSet() ).
            build();

        nodeById.updateExisting( persistedNode );
        return persistedNode;
    }

    public Node getNodeById( final ItemId id )
    {
        return this.nodeById.get( id );
    }

    public Node getNodeByPath( final NodePath path )
        throws NoItemFoundException
    {
        Preconditions.checkArgument( path.isAbsolute(), "path must be absolute: " + path.toString() );

        return this.nodeById.get( nodeIdByPath.get( path ) );
    }
}
