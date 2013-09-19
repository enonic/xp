package com.enonic.wem.core.item.dao;


import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.item.Item;
import com.enonic.wem.api.item.ItemId;
import com.enonic.wem.api.item.ItemPath;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

/**
 * Base test class for all ItemDao implementations.
 */
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
    public void when_storeNew_given_a_parent_path_that_is_not_absolute_then_IllegalArgumentException_is_thrown()
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
    public void when_storeNew_given_a_parent_path_to_an_item_that_does_not_exist_then_IllegalArgumentException_is_thrown()
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

    @Test
    public void when_updateExisting_given_an_Item_that_have_been_modified_since_read_then_ItemModifiedSinceRead_is_thrown()
        throws Exception
    {
        // setup
        ItemPath parent = new ItemPath( "/" );
        ItemId id = new ItemId();

        dao.storeNew( Item.newItem( id, "myItem" ).
            createdTime( new DateTime( 2013, 1, 1, 12, 0, 0 ) ).
            modifiedTime( new DateTime( 2013, 1, 1, 12, 0, 0 ) ).
            build(), parent );

        dao.updateExisting( Item.newItem( id, "myItem" ).
            createdTime( new DateTime( 2013, 1, 1, 12, 0, 0 ) ).
            modifiedTime( new DateTime( 2013, 1, 1, 13, 0, 0 ) ).
            build() );

        // exercise
        try
        {
            dao.updateExisting( Item.newItem( id, "myItem" ).
                createdTime( new DateTime( 2013, 1, 1, 12, 0, 0 ) ).
                modifiedTime( new DateTime( 2013, 1, 1, 12, 5, 0 ) ).
                build() );

        }
        catch ( Exception e )
        {
            assertTrue( e instanceof ItemModifiedSinceRead );
            assertEquals(
                "Item has been modified since it was read by you. Persisted Item's modified time is [2013-01-01T13:00:00.000+01:00] while yours is [2013-01-01T12:05:00.000+01:00]",
                e.getMessage() );
        }
    }
}
