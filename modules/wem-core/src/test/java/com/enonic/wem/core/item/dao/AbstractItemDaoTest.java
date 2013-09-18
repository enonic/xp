package com.enonic.wem.core.item.dao;


import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.item.Item;
import com.enonic.wem.api.item.ItemId;
import com.enonic.wem.api.item.ItemPath;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

public abstract class AbstractItemDaoTest
{
    private ItemDao dao;

    abstract ItemDao createDao();

    @Before
    public void before()
    {
        dao = createDao();
    }

    @Test
    public void when_storeNew_given_a_parent_path_that_is_not_absolute_then_IllegalArgumentException()
        throws Exception
    {
        // setup
        ItemPath parent = new ItemPath( "not-absolute" );
        ItemId id = new ItemId();
        Item item = Item.newItem( id, "myItem" ).property( "propertyOfItem", new Value.Text( "A" ) ).build();

        // exercise
        try
        {
            dao.storeNew( item, parent );
            fail( "Expecte exception" );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof IllegalArgumentException );
            assertEquals( "Path to parent Item must be absolute: not-absolute", e.getMessage() );
        }
    }

    @Test
    public void when_storeNew_given_a_parent_path_to_an_item_that_does_not_exist_then_IllegalArgumentException()
        throws Exception
    {
        // setup
        ItemPath parent = new ItemPath( "/does-not-exist" );
        ItemId id = new ItemId();
        Item item = Item.newItem( id, "myItem" ).property( "propertyOfItem", new Value.Text( "A" ) ).build();

        // exercise
        try
        {
            dao.storeNew( item, parent );
            fail( "Expected exception" );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof IllegalArgumentException );
            assertTrue( e.getCause() instanceof NoItemAtPathFound );
        }
    }
}
