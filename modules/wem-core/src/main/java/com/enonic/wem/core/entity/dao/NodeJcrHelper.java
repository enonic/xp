package com.enonic.wem.core.entity.dao;


import java.util.ArrayList;
import java.util.List;

import javax.jcr.ItemExistsException;
import javax.jcr.ItemNotFoundException;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.NoEntityWithIdFound;
import com.enonic.wem.api.entity.NoNodeAtPathFound;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodeAlreadyExist;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.core.jcr.JcrConstants;

class NodeJcrHelper
{
    private static final String ITEMS_NODE = "items";

    static final String ITEMS_PATH = JcrConstants.ROOT_NODE + "/" + ITEMS_NODE + "/";

    private final Session session;

    NodeJcrHelper( final Session session )
    {
        this.session = session;
    }

    private javax.jcr.Node nodesNode()
    {
        try
        {
            final javax.jcr.Node root = session.getRootNode();
            return root.getNode( ITEMS_PATH );
        }
        catch ( RepositoryException e )
        {
            throw new RuntimeException( "Failed to get nodesJcrNode", e );
        }
    }

    void ensurePath( final NodePath path )
    {
        try
        {
            javax.jcr.Node parentNode = nodesNode();
            for ( int i = 0; i < path.elementCount(); i++ )
            {
                final String pathElement = path.getElementAsString( i );
                if ( !parentNode.hasNode( pathElement ) )
                {
                    parentNode = parentNode.addNode( pathElement );
                }
                else
                {
                    parentNode = parentNode.getNode( pathElement );
                }
            }

        }
        catch ( RepositoryException e )
        {
            throw new RuntimeException( "Failed to ensurePath: " + path, e );
        }
    }

    Node persistNewItem( final Node node, final javax.jcr.Node parentNodeNode )
    {
        try
        {
            final javax.jcr.Node newNodeJcrNode = parentNodeNode.addNode( node.name(), JcrConstants.ITEM_NODETYPE );
            updateItemNode( newNodeJcrNode, node );
            return NodeJcrMapper.toNode( newNodeJcrNode ).build();
        }
        catch ( ItemExistsException e )
        {
            try
            {
                final javax.jcr.Node existingNodeNode = parentNodeNode.getNode( node.name() );
                final Node existingNode = NodeJcrMapper.toNode( existingNodeNode ).build();
                throw new NodeAlreadyExist( existingNode.path() );
            }
            catch ( RepositoryException e1 )
            {
                throw new RuntimeException( "Failed to createChild", e );
            }
        }
        catch ( RepositoryException e )
        {
            throw new RuntimeException( "Failed to createChild", e );
        }
    }

    javax.jcr.Node getItemNodeByPath( final NodePath path )
        throws NoNodeAtPathFound
    {
        try
        {
            return nodesNode().getNode( path.asRelative().toString() );
        }
        catch ( PathNotFoundException e )
        {
            throw new NoNodeAtPathFound( path );
        }
        catch ( RepositoryException e )
        {
            throw new RuntimeException( "Failed to getItemNodeByPath", e );
        }
    }

    Node updateItemNode( final javax.jcr.Node nodeNode, final Node node )
    {
        try
        {
            NodeJcrMapper.toJcr( node, nodeNode );
            return NodeJcrMapper.toNode( nodeNode ).build();
        }
        catch ( RepositoryException e )
        {
            throw new RuntimeException( "Failed to updateItemNode", e );
        }
    }

    Node updateItemNode( final javax.jcr.Node existingNodeNode, final UpdateNodeArgs updateNodeArgs )
    {
        try
        {
            NodeJcrMapper.updateNodeJcrNode( updateNodeArgs, existingNodeNode );
            return NodeJcrMapper.toNode( existingNodeNode ).build();
        }
        catch ( RepositoryException e )
        {
            throw new RuntimeException( "Failed to updateItemNode", e );
        }
    }

    javax.jcr.Node getItemNodeById( final EntityId id )
        throws NoEntityWithIdFound
    {
        try
        {
            return session.getNodeByIdentifier( id.toString() );
        }
        catch ( ItemNotFoundException e )
        {
            throw new NoEntityWithIdFound( id );
        }
        catch ( RepositoryException e )
        {
            throw new RuntimeException( "Failed to getItemById", e );
        }
    }

    Node getItemById( final EntityId id )
        throws NoEntityWithIdFound
    {
        try
        {
            final javax.jcr.Node nodeNode = session.getNodeByIdentifier( id.toString() );
            return NodeJcrMapper.toNode( nodeNode ).build();
        }
        catch ( ItemNotFoundException e )
        {
            throw new NoEntityWithIdFound( id );
        }
        catch ( RepositoryException e )
        {
            throw new RuntimeException( "Failed to getItemById", e );
        }
    }

    Node getItemByPath( final NodePath path )
        throws NoNodeAtPathFound
    {
        final javax.jcr.Node nodeNode = getItemNodeByPath( path );
        return NodeJcrMapper.toNode( nodeNode ).build();
    }

    List<Node> getNodesByParentPath( final NodePath parent )
        throws NoNodeAtPathFound
    {
        List<Node> childNodes = new ArrayList<>();
        final javax.jcr.Node parentNode = getItemNodeByPath( parent );
        try
        {
            final NodeIterator childIterator = parentNode.getNodes();
            while ( childIterator.hasNext() )
            {
                childNodes.add( NodeJcrMapper.toNode( childIterator.nextNode() ).build() );
            }
        }
        catch ( RepositoryException e )
        {
            throw new RuntimeException( "Failed to getItemsByPath", e );
        }
        return childNodes;
    }

}
