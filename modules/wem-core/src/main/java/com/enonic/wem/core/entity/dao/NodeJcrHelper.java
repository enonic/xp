package com.enonic.wem.core.entity.dao;


import javax.jcr.ItemExistsException;
import javax.jcr.ItemNotFoundException;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.NoEntityWithIdFoundException;
import com.enonic.wem.api.entity.NoNodeAtPathFoundException;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodeAlreadyExistException;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.api.entity.Nodes;
import com.enonic.wem.core.jcr.JcrConstants;

public class NodeJcrHelper
{
    private static final String NODES_JCRNODE_NAME = "items";

    public static final String NODES_JCRPATH = JcrConstants.ROOT_NODE + "/" + NODES_JCRNODE_NAME + "/";

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
            return root.getNode( NODES_JCRPATH );
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

    Node persisteNewNode( final Node node, final javax.jcr.Node parentJcrNode )
    {
        try
        {
            final javax.jcr.Node newNodeJcrNode = parentJcrNode.addNode( node.name().toString(), JcrConstants.ITEM_NODETYPE );
            updateItemNode( newNodeJcrNode, node );
            return NodeJcrMapper.toNode( newNodeJcrNode ).build();
        }
        catch ( ItemExistsException e )
        {
            try
            {
                final javax.jcr.Node existingNodeNode = parentJcrNode.getNode( node.name().toString() );
                final Node existingNode = NodeJcrMapper.toNode( existingNodeNode ).build();
                throw new NodeAlreadyExistException( existingNode.path() );
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

    javax.jcr.Node getNodeByPath( final NodePath path )
        throws NoNodeAtPathFoundException
    {
        try
        {
            return nodesNode().getNode( path.asRelative().toString() );
        }
        catch ( PathNotFoundException e )
        {
            throw new NoNodeAtPathFoundException( path );
        }
        catch ( RepositoryException e )
        {
            throw new RuntimeException( "Failed to getItemNodeByPath", e );
        }
    }

    Node updateItemNode( final javax.jcr.Node jcrNode, final Node node )
    {
        try
        {
            NodeJcrMapper.toJcr( node, jcrNode );
            return NodeJcrMapper.toNode( jcrNode ).build();
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
            final boolean nameChanged = !existingNodeNode.getName().equals( updateNodeArgs.name().toString() );
            if ( nameChanged )
            {
                final NodePath existingNodePath = NodeJcrMapper.resolveNodePath( existingNodeNode );
                final NodePath newNodePath = new NodePath( existingNodePath.getParentPath(), updateNodeArgs.name() );
                final String newJcrNodePath = toJcrPath( newNodePath );
                session.move( existingNodeNode.getPath(), newJcrNodePath );
                final javax.jcr.Node renamedNode = getItemNodeById( updateNodeArgs.nodeToUpdate() );
                NodeJcrMapper.updateNodeJcrNode( updateNodeArgs, renamedNode );
                return NodeJcrMapper.toNode( renamedNode ).build();
            }
            else
            {
                NodeJcrMapper.updateNodeJcrNode( updateNodeArgs, existingNodeNode );
                return NodeJcrMapper.toNode( existingNodeNode ).build();
            }
        }
        catch ( RepositoryException e )
        {
            throw new RuntimeException( "Failed to updateItemNode", e );
        }
    }

    javax.jcr.Node getItemNodeById( final EntityId id )
        throws NoEntityWithIdFoundException
    {
        try
        {
            return session.getNodeByIdentifier( id.toString() );
        }
        catch ( ItemNotFoundException e )
        {
            throw new NoEntityWithIdFoundException( id );
        }
        catch ( RepositoryException e )
        {
            throw new RuntimeException( "Failed to getItemById", e );
        }
    }

    Node getItemById( final EntityId id )
        throws NoEntityWithIdFoundException
    {
        try
        {
            final javax.jcr.Node nodeNode = session.getNodeByIdentifier( id.toString() );
            return NodeJcrMapper.toNode( nodeNode ).build();
        }
        catch ( ItemNotFoundException e )
        {
            throw new NoEntityWithIdFoundException( id );
        }
        catch ( RepositoryException e )
        {
            throw new RuntimeException( "Failed to getItemById", e );
        }
    }

    Node getItemByPath( final NodePath path )
        throws NoNodeAtPathFoundException
    {
        final javax.jcr.Node nodeNode = getNodeByPath( path );
        return NodeJcrMapper.toNode( nodeNode ).build();
    }

    Nodes getNodesByParentPath( final NodePath parent )
        throws NoNodeAtPathFoundException
    {
        Nodes.Builder childNodes = Nodes.newNodes();

        final javax.jcr.Node parentNode = getNodeByPath( parent );

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
        return childNodes.build();
    }

    static String toJcrPath( final NodePath nodePath )
    {
        return "/" + NodeJcrHelper.NODES_JCRPATH + nodePath.asRelative().toString();
    }
}
