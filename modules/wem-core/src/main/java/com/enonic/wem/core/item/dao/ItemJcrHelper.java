package com.enonic.wem.core.item.dao;


import javax.jcr.ItemExistsException;
import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.item.Item;
import com.enonic.wem.api.item.ItemId;
import com.enonic.wem.api.item.ItemPath;
import com.enonic.wem.core.jcr.JcrConstants;

class ItemJcrHelper
{
    private static final String ITEMS_NODE = "items";

    static final String ITEMS_PATH = JcrConstants.ROOT_NODE + "/" + ITEMS_NODE + "/";

    private final Session session;

    ItemJcrHelper( final Session session )
    {
        this.session = session;
    }

    Item persistNewItem( final Item item, final Node parentItemNode )
    {
        try
        {
            final Node newItemNode = parentItemNode.addNode( item.name(), JcrConstants.ITEM_NODETYPE );
            updateItemNode( newItemNode, item );
            return ItemJcrMapper.toItem( newItemNode ).build();
        }
        catch ( ItemExistsException e )
        {
            try
            {
                final Node existingItemNode = parentItemNode.getNode( item.name() );
                final Item existingItem = ItemJcrMapper.toItem( existingItemNode ).build();
                throw new ItemAlreadyExist( existingItem.path() );
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

    private Node itemsNode()
    {
        try
        {
            Node root = session.getRootNode();
            return root.getNode( ITEMS_PATH );
        }
        catch ( RepositoryException e )
        {
            throw new RuntimeException( "Failed to get itemsNode", e );
        }
    }

    Node getItemNodeByPath( final ItemPath path )
    {
        try
        {
            return itemsNode().getNode( path.asRelative().toString() );
        }
        catch ( PathNotFoundException e )
        {
            throw new NoItemAtPathFound( path );
        }
        catch ( RepositoryException e )
        {
            throw new RuntimeException( "Failed to getItemNodeByPath", e );
        }
    }

    Item updateItemNode( final Node itemNode, final Item item )
    {
        try
        {
            ItemJcrMapper.toJcr( item, itemNode );
            return ItemJcrMapper.toItem( itemNode ).build();
        }
        catch ( RepositoryException e )
        {
            throw new RuntimeException( "Failed to updateItemNode", e );
        }
    }

    Item updateItemNode( final Node itemNode, final UpdateItemArgs updateItemArgs )
    {
        try
        {
            ItemJcrMapper.updateItemNode( updateItemArgs, itemNode );
            return ItemJcrMapper.toItem( itemNode ).build();
        }
        catch ( RepositoryException e )
        {
            throw new RuntimeException( "Failed to updateItemNode", e );
        }
    }

    Node getItemNodeById( final ItemId id )
        throws NoItemWithIdFound
    {
        try
        {
            return session.getNodeByIdentifier( id.toString() );
        }
        catch ( ItemNotFoundException e )
        {
            throw new NoItemWithIdFound( id );
        }
        catch ( RepositoryException e )
        {
            throw new RuntimeException( "Failed to getItemById", e );
        }
    }

    Item getItemById( final ItemId id )
    {
        try
        {
            final Node itemNode = session.getNodeByIdentifier( id.toString() );
            return ItemJcrMapper.toItem( itemNode ).build();
        }
        catch ( ItemNotFoundException e )
        {
            throw new NoItemWithIdFound( id );
        }
        catch ( RepositoryException e )
        {
            throw new RuntimeException( "Failed to getItemById", e );
        }
    }

    Item getItemByPath( final ItemPath path )
    {
        final Node itemNode = getItemNodeByPath( path );
        return ItemJcrMapper.toItem( itemNode ).build();
    }


}
