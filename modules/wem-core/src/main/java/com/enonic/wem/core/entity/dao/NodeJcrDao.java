package com.enonic.wem.core.entity.dao;


import javax.jcr.ItemExistsException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.joda.time.DateTime;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.ContentAlreadyExistException;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.NoEntityFoundException;
import com.enonic.wem.api.entity.NoEntityWithIdFoundException;
import com.enonic.wem.api.entity.NoNodeAtPathFoundException;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodeName;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.api.entity.Nodes;

public class NodeJcrDao
    implements NodeDao
{

    public static final String EMBEDDED_NODE_ROOT_PATH = "__embedded";

    public static final String CONTENT_ROOT_NODE_NAME = "content";

    public static final NodePath CONTENT_ROOT_NODE = NodePath.newNodePath( NodePath.ROOT, CONTENT_ROOT_NODE_NAME ).build();

    private final Session session;

    private final NodeJcrHelper jcrHelper;

    public NodeJcrDao( final Session session )
    {
        this.session = session;
        this.jcrHelper = new NodeJcrHelper( this.session );

        // TODO: A temporary hack to ensure that paths to root containers of different types of nodes already exists
        ensurePath( new NodePath( "mixins" ) );
        ensurePath( new NodePath( "content-types" ) );
        ensurePath( new NodePath( CONTENT_ROOT_NODE_NAME ) );
    }

    private void ensurePath( final NodePath path )
    {
        jcrHelper.ensurePath( path );
    }

    @Override
    public Node createNode( final CreateNodeArguments createNodeArguments )
    {
        Preconditions.checkNotNull( createNodeArguments.parent(), "Path of parent Node must be specified" );
        Preconditions.checkArgument( createNodeArguments.parent().isAbsolute(),
                                     "Path to parent Node must be absolute: " + createNodeArguments.parent() );

        final javax.jcr.Node parentJcrNode = findJcrParentNode( createNodeArguments );

        final Node newNode = Node.newNode().
            createdTime( DateTime.now() ).
            creator( createNodeArguments.creator() ).
            parent( createNodeArguments.parent() ).
            name( NodeName.from( createNodeArguments.name() ) ).
            rootDataSet( createNodeArguments.rootDataSet() ).
            attachments( createNodeArguments.attachments() ).
            entityIndexConfig( createNodeArguments.entityIndexConfig() ).
            build();

        return jcrHelper.persisteNewNode( newNode, parentJcrNode );
    }

    private javax.jcr.Node findJcrParentNode( final CreateNodeArguments createNodeArguments )
    {
        if ( createNodeArguments.embed() )
        {
            return getEmbeddedNodePath( createNodeArguments );
        }

        final javax.jcr.Node parentJcrNode = jcrHelper.getNodeByPath( createNodeArguments.parent() );

        return parentJcrNode;
    }

    private javax.jcr.Node getEmbeddedNodePath( final CreateNodeArguments createNodeArguments )
    {
        final NodePath embeddedNodePath = NodePath.newNodePath( createNodeArguments.parent(), EMBEDDED_NODE_ROOT_PATH ).build();

        jcrHelper.ensurePath( embeddedNodePath );

        return jcrHelper.getNodeByPath( embeddedNodePath );
    }

    @Override
    public Node updateNode( final UpdateNodeArgs updateNodeArgs )
    {
        Preconditions.checkNotNull( updateNodeArgs.nodeToUpdate(), "nodeToUpdate must be specified" );
        final javax.jcr.Node existingNodeNode = jcrHelper.getItemNodeById( updateNodeArgs.nodeToUpdate() );
        return jcrHelper.updateItemNode( existingNodeNode, updateNodeArgs );
    }


    @Override
    public boolean moveNode( final NodePath originalPath, final NodePath newPath )
    {
        if ( originalPath.equals( newPath ) )
        {
            return false;
        }

        try
        {
            final String jcrOriginalPath = NodeJcrHelper.toJcrPath( originalPath );
            final String jcrDestinationPath = NodeJcrHelper.toJcrPath( newPath );

            session.move( jcrOriginalPath, jcrDestinationPath );
            return true;
        }
        catch ( ItemExistsException e )
        {
            throw new ContentAlreadyExistException( ContentPath.from( newPath.toString() ) );
        }
        catch ( RepositoryException e )
        {
            throw new RuntimeException( "Failed to move node", e );
        }
    }

    @Override
    public void deleteNodeById( final EntityId id )
    {
        Preconditions.checkNotNull( id, "entity to delete must be specified" );
        javax.jcr.Node node = jcrHelper.getItemNodeById( id );
        try
        {
            node.remove();
        }
        catch ( RepositoryException e )
        {
            throw new RuntimeException( "Failed to deleteNodeById", e );
        }
    }

    @Override
    public void deleteNodeByPath( final NodePath path )
    {
        Preconditions.checkNotNull( path, "node to delete must be specified" );
        try
        {
            jcrHelper.getNodeByPath( path ).remove();
        }
        catch ( RepositoryException e )
        {
            throw new RuntimeException( "Failed to deleteNodeByPath", e );
        }
    }

    public Node getNodeById( final EntityId id )
        throws NoEntityWithIdFoundException
    {
        return this.jcrHelper.getItemById( id );
    }

    public Node getNodeByPath( final NodePath path )
        throws NoNodeAtPathFoundException
    {
        Preconditions.checkArgument( path.isAbsolute(), "path must be absolute: " + path.toString() );

        return this.jcrHelper.getItemByPath( path );
    }

    @Override
    public Nodes getNodesByParentPath( final NodePath parent )
        throws NoEntityFoundException
    {
        Preconditions.checkArgument( parent.isAbsolute(), "parent path must be absolute: " + parent.toString() );

        return this.jcrHelper.getNodesByParentPath( parent );
    }
}
