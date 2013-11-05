package com.enonic.wem.core.entity.dao;


import java.util.LinkedHashMap;

import org.joda.time.DateTime;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.NoEntityFoundException;
import com.enonic.wem.api.entity.NoNodeAtPathFound;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodeAlreadyExist;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.api.entity.Nodes;

import static com.enonic.wem.api.entity.Node.newNode;

public class NodeInMemoryDao
    implements NodeDao
{
    private final NodeIdByPath nodeIdByPath;

    private final NodeById nodeById;

    public NodeInMemoryDao()
    {
        nodeById = new NodeById( new LinkedHashMap<EntityId, Node>() );
        nodeIdByPath = new NodeIdByPath( new LinkedHashMap<NodePath, EntityId>() );
    }

    @Override
    public Node createNode( final CreateNodeArguments createNodeArguments )
    {
        Preconditions.checkArgument( createNodeArguments.parent().isAbsolute(),
                                     "Path to parent Item must be absolute: " + createNodeArguments.parent().toString() );
        if ( !nodeIdByPath.pathHasItem( createNodeArguments.parent() ) )
        {
            throw new NoNodeAtPathFound( createNodeArguments.parent() );
        }

        final Node newNode = Node.newNode().
            id( new EntityId() ).
            createdTime( DateTime.now() ).
            creator( createNodeArguments.creator() ).
            parent( createNodeArguments.parent() ).
            name( createNodeArguments.name() ).
            icon( createNodeArguments.icon() ).
            rootDataSet( createNodeArguments.rootDataSet() ).
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

    public Node getNodeById( final EntityId id )
    {
        return this.nodeById.get( id );
    }

    public Node getNodeByPath( final NodePath path )
        throws NoEntityFoundException
    {
        Preconditions.checkArgument( path.isAbsolute(), "path must be absolute: " + path.toString() );

        return this.nodeById.get( nodeIdByPath.get( path ) );
    }

    @Override
    public Nodes getNodesByParentPath( final NodePath parent )
        throws NoEntityFoundException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteNodeById( final EntityId id )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteNodeByPath( final NodePath path )
    {
        throw new UnsupportedOperationException();
    }
}
