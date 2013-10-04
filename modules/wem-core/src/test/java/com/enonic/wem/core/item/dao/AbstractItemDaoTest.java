package com.enonic.wem.core.item.dao;


import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Optional;
import com.google.common.base.Stopwatch;

import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.item.Item;
import com.enonic.wem.api.item.ItemId;
import com.enonic.wem.api.item.ItemPath;

import static com.enonic.wem.core.item.dao.CreateItemArgs.newCreateItemArgs;
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
    public void when_createItem_given_item_then_persisted_Item_is_returned()
        throws Exception
    {
        // setup
        RootDataSet rootDataSet = new RootDataSet();
        rootDataSet.setProperty( "propertyOfItem", new Value.Text( "A" ) );
        CreateItemArgs createItemArgs = newCreateItemArgs().
            parent( ItemPath.ROOT ).
            name( "myItem" ).
            rootDataSet( rootDataSet ).
            build();

        // exercise
        Item persisted = dao.createItem( createItemArgs );
        assertNotNull( persisted.id() );
        assertEquals( "myItem", persisted.name() );
        assertEquals( "A", persisted.property( "propertyOfItem" ).getString() );
    }

    @Test
    public void when_createItem_performance_lots_of_children()
        throws Exception
    {
        // setup
        int count = 1000;
        List<CreateItemArgs> list = new ArrayList<>( count );
        RootDataSet rootDataSet = new RootDataSet();
        rootDataSet.setProperty( "propertyOfItem", new Value.Text( "A" ) );

        for ( int i = 0; i < count; i++ )
        {
            list.add( newCreateItemArgs().
                parent( ItemPath.ROOT ).
                name( "myItem-" + i ).
                rootDataSet( rootDataSet ).
                build() );
        }

        // exercise
        Stopwatch sw = new Stopwatch();
        sw.start();
        for ( CreateItemArgs createItemArgs : list )
        {
            dao.createItem( createItemArgs );
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
        RootDataSet rootDataSet = new RootDataSet();
        rootDataSet.setProperty( "propertyOfItem", new Value.Text( "A" ) );
        CreateItemArgs createItemArgs = newCreateItemArgs().
            parent( ItemPath.ROOT ).
            name( "myItem" ).
            rootDataSet( rootDataSet ).
            build();

        Item persisted = dao.createItem( createItemArgs );

        // exercise
        Item fetched = dao.getItemById( persisted.id() );
        assertNotNull( fetched.id() );
        assertEquals( persisted.name(), fetched.name() );
        assertEquals( persisted.property( "propertyOfItem" ).getString(), fetched.property( "propertyOfItem" ).getString() );
    }

    @Test
    public void when_createItem_given_a_path_to_parent_that_is_not_absolute_then_IllegalArgumentException_is_thrown()
        throws Exception
    {
        // setup

        RootDataSet rootDataSet = new RootDataSet();
        rootDataSet.setProperty( "propertyOfItem", new Value.Text( "A" ) );
        CreateItemArgs createItemArgs = newCreateItemArgs().
            parent( new ItemPath( "not-absolute-parent-path" ) ).
            name( "myItem" ).
            rootDataSet( rootDataSet ).
            build();

        // exercise
        try
        {
            dao.createItem( createItemArgs );
            fail( "Expected exception" );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof IllegalArgumentException );
            assertEquals( "Path to parent Item must be absolute: not-absolute-parent-path", e.getMessage() );
        }
    }

    @Test
    public void when_createItem_given_a_parent_path_to_an_item_that_does_not_exist_then_NoItemAtPathFound_is_thrown()
        throws Exception
    {
        // setup
        CreateItemArgs createItemArgs = CreateItemArgs.newCreateItemArgs().
            parent( new ItemPath( "/does-not-exist" ) ).
            name( "myItem" ).
            build();

        // exercise
        try
        {
            dao.createItem( createItemArgs );
            fail( "Expected exception NoItemAtPathFound" );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            assertTrue( e instanceof NoItemAtPathFound );
            NoItemAtPathFound noItemAtPathFound = (NoItemAtPathFound) e;
            assertEquals( "/does-not-exist", noItemAtPathFound.getPath().toString() );
        }
    }

    @Test
    public void when_createItem_given_an_already_persisted_item_then_ItemAlreadyExist_is_thrown()
        throws Exception
    {
        // setup
        dao.createItem( newCreateItemArgs().
            parent( ItemPath.ROOT ).
            name( "myItem" ).
            build() );

        // exercise
        try
        {
            dao.createItem( newCreateItemArgs().
                parent( ItemPath.ROOT ).
                name( "myItem" ).
                build() );
            fail( "Expected exception ItemAlreadyExist" );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof ItemAlreadyExist );
            assertEquals( "Item already exist: /myItem", e.getMessage() );
        }
    }

    @Test
    public void when_updateItem_given_an_Item_that_have_been_modified_since_read_then_ItemModifiedSinceRead_is_thrown()
        throws Exception
    {
        // setup
        DateTimeUtils.setCurrentMillisFixed( CREATED_TIME.getMillis() );
        RootDataSet initialData = new RootDataSet();
        initialData.setProperty( "propertyOfItem", new Value.Text( "A" ) );
        ItemId id = dao.createItem( CreateItemArgs.newCreateItemArgs().
            parent( ItemPath.ROOT ).
            name( "myItem" ).
            rootDataSet( initialData ).
            build() ).id();

        // setup: ensure modifiedTime is one hour after created time
        DateTimeUtils.setCurrentMillisFixed( CREATED_TIME.plusHours( 1 ).getMillis() );
        RootDataSet changedData = new RootDataSet();
        changedData.setProperty( "propertyOfItem", new Value.Text( "B" ) );
        dao.updateItem( UpdateItemArgs.newUpdateItemArgs().
            itemToUpdate( id ).
            name( "myItem" ).
            rootDataSet( changedData ).
            readAt( Optional.<DateTime>absent() ).
            build() );

        // exercise: pretend item was read one minute before current modifiedTime
        try
        {
            DateTimeUtils.setCurrentMillisFixed( CREATED_TIME.plusMinutes( 5 ).getMillis() );

            dao.updateItem( UpdateItemArgs.newUpdateItemArgs().
                itemToUpdate( id ).
                name( "myItem" ).
                rootDataSet( changedData ).
                readAt( Optional.of( CREATED_TIME.plusMinutes( 59 ) ) ).
                build() );

            fail( "Expected exception ItemModifiedSinceRead" );

        }
        catch ( Exception e )
        {
            assertTrue( e instanceof ItemModifiedSinceRead );
            assertEquals(
                "Item has been modified since it was read by you. Persisted Item's modified time is [2013-01-01T13:00:00.000Z] while yours is [2013-01-01T12:59:00.000Z]",
                e.getMessage() );
        }
    }
}
