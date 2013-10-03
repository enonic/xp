package com.enonic.wem.core.item.dao;


import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Stopwatch;

import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.item.Item;
import com.enonic.wem.api.item.ItemId;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

/**
 * Base test class for all ItemDao implementations.
 */
public abstract class AbstractItemDaoTest
{
    static final DateTime CREATED_TIME = new DateTime( 2013, 1, 1, 12, 0, 0, DateTimeZone.UTC );

    private ItemDao dao;

    abstract ItemDao createDao();

    @Before
    public void before()
    {
        dao = createDao();
    }

    @Test
    public void when_storeNew_given_item_with_then_IllegalArgumentException_is_thrown()
        throws Exception
    {
        // setup
        Item item = Item.newItem( new ItemId() ).
            path( "/myItem" ).
            name( "myItem" ).
            property( "propertyOfItem", new Value.Text( "A" ) ).
            build();

        // exercise
        try
        {
            dao.storeNew( item );
            fail( "Expected IllegalArgumentException" );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof IllegalArgumentException );
        }
    }

    @Test
    public void when_storeNew_given_item_then_persisted_Item_is_returned()
        throws Exception
    {
        // setup
        Item item = Item.newItem().
            path( "/myItem" ).
            name( "myItem" ).
            property( "propertyOfItem", new Value.Text( "A" ) ).
            build();

        // exercise
        Item persisted = dao.storeNew( item );
        assertNotNull( persisted.id() );
        assertEquals( "myItem", persisted.name() );
        assertEquals( "A", persisted.property( "propertyOfItem" ).getString() );
    }

    @Test
    public void when_storeNew_performance_lots_of_children()
        throws Exception
    {
        // setup
        int count = 10000;
        List<Item> list = new ArrayList<>( count );
        for ( int i = 0; i < count; i++ )
        {
            list.add( Item.newItem().
                path( "/myItem-" + i ).
                name( "myItem-" + i ).
                property( "propertyOfItem", new Value.Text( "A" ) ).
                build() );
        }

        // exercise
        Stopwatch sw = new Stopwatch();
        sw.start();
        for ( Item item : list )
        {
            dao.storeNew( item );
        }
        sw.stop();
        System.out.println( sw.toString() );
    }

    @Test
    public void when_storeNew_performance_huge_tree()
        throws Exception
    {
        // TODO
    }

    @Test
    public void when_getItemById_given_id_of_persisted_Item_then_persisted_Item_is_returned()
        throws Exception
    {
        // setup
        Item item = Item.newItem().
            createdTime( CREATED_TIME ).
            path( "/myItem" ).
            name( "myItem" ).
            property( "propertyOfItem", new Value.Text( "A" ) ).
            build();

        Item persisted = dao.storeNew( item );

        // exercise
        Item fetched = dao.getItemById( persisted.id() );
        assertNotNull( fetched.id() );
        assertEquals( persisted.name(), fetched.name() );
        assertEquals( persisted.property( "propertyOfItem" ).getString(), fetched.property( "propertyOfItem" ).getString() );
    }

    @Test
    public void when_storeNew_given_a_path_that_is_not_absolute_then_IllegalArgumentException_is_thrown()
        throws Exception
    {
        // setup
        Item item = Item.newItem().
            path( "not-absolute" ).
            name( "not-absolute" ).
            property( "propertyOfItem", new Value.Text( "A" ) ).build();

        // exercise
        try
        {
            dao.storeNew( item );
            fail( "Expected exception" );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof IllegalArgumentException );
            assertEquals( "Path to Item must be absolute: not-absolute", e.getMessage() );
        }
    }

    @Test
    public void when_storeNew_given_a_parent_path_to_an_item_that_does_not_exist_then_IllegalArgumentException_is_thrown()
        throws Exception
    {
        // setup
        Item item = Item.newItem().
            path( "/does-not-exist/myItem" ).
            name( "myItem" ).
            property( "propertyOfItem", new Value.Text( "A" ) ).
            build();

        // exercise
        try
        {
            dao.storeNew( item );
            fail( "Expected exception" );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof NoItemAtPathFound );
            NoItemAtPathFound noItemAtPathFound = (NoItemAtPathFound) e;
            assertEquals( "/does-not-exist", noItemAtPathFound.getPath().toString() );
        }
    }

    @Test
    public void when_updateExisting_given_an_Item_that_have_been_modified_since_read_then_ItemModifiedSinceRead_is_thrown()
        throws Exception
    {
        // setup
        ItemId id = dao.storeNew( Item.newItem().
            path( "/myItem" ).
            name( "myItem" ).
            createdTime( CREATED_TIME ).
            modifiedTime( CREATED_TIME ).
            build() ).id();

        dao.updateExisting( Item.newItem( id ).
            path( "/myItem" ).
            name( "myItem" ).
            createdTime( CREATED_TIME ).
            modifiedTime( CREATED_TIME.plusHours( 1 ) ).
            build() );

        // exercise
        try
        {
            dao.updateExisting( Item.newItem( id ).
                path( "/myItem" ).
                name( "myItem" ).
                createdTime( CREATED_TIME ).
                modifiedTime( CREATED_TIME.plusMinutes( 5 ) ).
                build() );

        }
        catch ( Exception e )
        {
            assertTrue( e instanceof ItemModifiedSinceRead );
            assertEquals(
                "Item has been modified since it was read by you. Persisted Item's modified time is [2013-01-01T13:00:00.000Z] while yours is [2013-01-01T12:05:00.000Z]",
                e.getMessage() );
        }
    }
}
