package com.enonic.wem.core.item.dao;


import javax.jcr.Node;
import javax.jcr.Session;

import org.joda.time.DateTime;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.item.Item;
import com.enonic.wem.api.item.ItemId;
import com.enonic.wem.api.item.ItemPath;

import static com.enonic.wem.api.item.Item.newItem;

public class ItemJcrDao
    implements ItemDao
{
    private final Session session;

    private final ItemJcrHelper jcrHelper;

    public ItemJcrDao( final Session session )
    {
        this.session = session;
        this.jcrHelper = new ItemJcrHelper( this.session );

        // TODO: A temporary hack to ensure that paths to root containers of different types of items already exists
        ensurePath( new ItemPath( "mixins" ) );
    }

    private void ensurePath( final ItemPath path )
    {
        jcrHelper.ensurePath( path );
    }

    @Override
    public Item createItem( final CreateItemArgs createItemArgs )
    {
        Preconditions.checkNotNull( createItemArgs.parent(), "Path of parent Item must be specified" );
        Preconditions.checkArgument( createItemArgs.parent().isAbsolute(),
                                     "Path to parent Item must be absolute: " + createItemArgs.parent() );

        final Node parentItemNode = jcrHelper.getItemNodeByPath( createItemArgs.parent() );

        final Item newItem = newItem().
            createdTime( DateTime.now() ).
            creator( createItemArgs.creator() ).
            parent( createItemArgs.parent() ).
            name( createItemArgs.name() ).
            icon( createItemArgs.icon() ).
            rootDataSet( createItemArgs.rootDataSet() ).
            build();

        return jcrHelper.persistNewItem( newItem, parentItemNode );
    }

    @Override
    public Item updateItem( final UpdateItemArgs updateItemArgs )
    {
        Preconditions.checkNotNull( updateItemArgs.itemToUpdate(), "itemToUpdate must be specified" );
        final Node existingItemNode = jcrHelper.getItemNodeById( updateItemArgs.itemToUpdate() );
        return jcrHelper.updateItemNode( existingItemNode, updateItemArgs );
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
