package com.enonic.wem.core.item.dao;


import javax.jcr.Session;

import org.joda.time.DateTime;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.item.ItemId;
import com.enonic.wem.api.item.NoItemWithIdFound;
import com.enonic.wem.api.item.NoNodeAtPathFound;
import com.enonic.wem.api.item.Node;
import com.enonic.wem.api.item.NodePath;

public class NodeJcrDao
    implements NodeDao
{
    private final Session session;

    private final NodeJcrHelper jcrHelper;

    public NodeJcrDao( final Session session )
    {
        this.session = session;
        this.jcrHelper = new NodeJcrHelper( this.session );

        // TODO: A temporary hack to ensure that paths to root containers of different types of items already exists
        ensurePath( new NodePath( "mixins" ) );
    }

    private void ensurePath( final NodePath path )
    {
        jcrHelper.ensurePath( path );
    }

    @Override
    public Node createNode( final CreateNodeArgs createNodeArgs )
    {
        Preconditions.checkNotNull( createNodeArgs.parent(), "Path of parent Item must be specified" );
        Preconditions.checkArgument( createNodeArgs.parent().isAbsolute(),
                                     "Path to parent Item must be absolute: " + createNodeArgs.parent() );

        final javax.jcr.Node parentNodeNode = jcrHelper.getItemNodeByPath( createNodeArgs.parent() );

        final Node newNode = Node.newNode().
            createdTime( DateTime.now() ).
            creator( createNodeArgs.creator() ).
            parent( createNodeArgs.parent() ).
            name( createNodeArgs.name() ).
            icon( createNodeArgs.icon() ).
            rootDataSet( createNodeArgs.rootDataSet() ).
            build();

        return jcrHelper.persistNewItem( newNode, parentNodeNode );
    }

    @Override
    public Node updateNode( final UpdateNodeArgs updateNodeArgs )
    {
        Preconditions.checkNotNull( updateNodeArgs.itemToUpdate(), "itemToUpdate must be specified" );
        final javax.jcr.Node existingNodeNode = jcrHelper.getItemNodeById( updateNodeArgs.itemToUpdate() );
        return jcrHelper.updateItemNode( existingNodeNode, updateNodeArgs );
    }

    public Node getNodeById( final ItemId id )
        throws NoItemWithIdFound
    {
        return this.jcrHelper.getItemById( id );
    }

    public Node getNodeByPath( final NodePath path )
        throws NoNodeAtPathFound
    {
        Preconditions.checkArgument( path.isAbsolute(), "path must be absolute: " + path.toString() );

        return this.jcrHelper.getItemByPath( path );
    }
}
