package com.enonic.wem.core.item.dao;


import javax.jcr.Node;
import javax.jcr.Session;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.item.Item;
import com.enonic.wem.api.item.ItemId;
import com.enonic.wem.api.item.ItemPath;

public class ItemJcrDao
    implements ItemDao
{

    private final Session session;

    private final ItemJcrHelper jcrHelper;

    public ItemJcrDao( final Session session )
    {
        this.session = session;
        this.jcrHelper = new ItemJcrHelper( this.session );
    }

    @Override
    public Item storeNew( final Item item )
    {
        Preconditions.checkArgument( item.id() == null, "New Item to store cannot have an ItemId: " + item.id() );
        final ItemPath path = item.path();
        Preconditions.checkNotNull( path, "Path of Item must be specified " );
        Preconditions.checkArgument( path.isAbsolute(), "Path to Item must be absolute: " + path.toString() );
        final ItemPath parentPath = path.getParentPath();

        final Node parentItemNode = jcrHelper.getItemNodeByPath( parentPath );
        return jcrHelper.persistNewItem( item, parentItemNode );
    }

    @Override
    public Item updateExisting( final Item item )
    {
        Preconditions.checkNotNull( item.id(), "Item to store must have an ItemId specified" );
        final Node existingItemNode = jcrHelper.getItemNodeById( item.id() );
        return jcrHelper.updateItemNode( existingItemNode, item );
    }

    public Item getItemById( final ItemId id )
    {
        return this.jcrHelper.getItemById( id );
    }

    public Item getItemByPath( final ItemPath path )
        throws NoItemFoundException
    {
        Preconditions.checkArgument( path.isAbsolute(), "path must be absolute: " + path.toString() );

        return this.jcrHelper.getItemByPath( path );
    }
}
