package com.enonic.wem.core.item.dao;

import org.junit.Test;

import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.item.Item;
import com.enonic.wem.api.item.ItemId;
import com.enonic.wem.api.item.ItemPath;

import static junit.framework.Assert.assertSame;


public class ItemInMemoryDaoTest
    extends AbstractItemDaoTest
{

    private ItemInMemoryDao dao = new ItemInMemoryDao();

    @Override
    ItemDao createDao()
    {
        return dao;
    }

    @Test
    public void given_an_item_when_storeNew_getItemById_returns_same()
        throws Exception
    {
        // setup
        ItemPath parent = new ItemPath( "/" );
        ItemId id = new ItemId();
        Item item = Item.newItem( id, "myItem" ).property( "propertyOfItem", new Value.Text( "A" ) ).build();

        // exercise
        dao.storeNew( item, parent );

        assertSame( item, dao.getItemById( id ) );
    }

    @Test
    public void given_an_stored_item_when_getItemByPath_then_returns_same_as_stored()
        throws Exception
    {
        // setup
        ItemPath parent = new ItemPath( "/" );
        ItemId id = new ItemId();
        Item item = Item.newItem( id, "myItem" ).property( "propertyOfItem", new Value.Text( "A" ) ).build();

        // exercise
        dao.storeNew( item, parent );

        assertSame( item, dao.getItemByPath( new ItemPath( "/myItem" ) ) );
    }

    @Test
    public void testUpdateExisting()
        throws Exception
    {

    }

    @Test
    public void testGetItemById()
        throws Exception
    {

    }

    @Test
    public void testGetItemByPath()
        throws Exception
    {

    }
}
