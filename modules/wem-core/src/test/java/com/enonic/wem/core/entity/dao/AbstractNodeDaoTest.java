package com.enonic.wem.core.entity.dao;


import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Stopwatch;

import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.entity.NoNodeAtPathFound;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodeAlreadyExist;
import com.enonic.wem.api.entity.NodePath;

import static com.enonic.wem.core.entity.dao.CreateNodeArguments.newCreateNodeArgs;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

/**
 * Base test class for all NodeDao implementations.
 */
public abstract class AbstractNodeDaoTest
{
    private NodeDao dao;

    abstract NodeDao createDao();

    @Before
    public void before()
    {
        dao = createDao();
    }

    @Test
    public void when_createNode_given_item_then_persisted_Item_is_returned()
        throws Exception
    {
        // setup
        RootDataSet rootDataSet = new RootDataSet();
        rootDataSet.setProperty( "propertyOfItem", new Value.String( "A" ) );
        CreateNodeArguments createNodeArguments = newCreateNodeArgs().
            parent( NodePath.ROOT ).
            name( "myItem" ).
            rootDataSet( rootDataSet ).
            build();

        // exercise
        Node persisted = dao.createNode( createNodeArguments );
        assertNotNull( persisted.id() );
        assertEquals( "myItem", persisted.name() );
        assertEquals( "A", persisted.property( "propertyOfItem" ).getString() );
    }

    @Test
    public void when_createNode_performance_lots_of_children()
        throws Exception
    {
        // setup
        int count = 1000;
        List<CreateNodeArguments> list = new ArrayList<>( count );
        RootDataSet rootDataSet = new RootDataSet();
        rootDataSet.setProperty( "propertyOfItem", new Value.String( "A" ) );

        for ( int i = 0; i < count; i++ )
        {
            list.add( newCreateNodeArgs().
                parent( NodePath.ROOT ).
                name( "myItem-" + i ).
                rootDataSet( rootDataSet ).
                build() );
        }

        // exercise
        Stopwatch sw = new Stopwatch();
        sw.start();
        for ( CreateNodeArguments createNodeArguments : list )
        {
            dao.createNode( createNodeArguments );
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
    public void when_getNodeById_given_id_of_persisted_Item_then_persisted_Item_is_returned()
        throws Exception
    {
        // setup
        RootDataSet rootDataSet = new RootDataSet();
        rootDataSet.setProperty( "propertyOfItem", new Value.String( "A" ) );
        CreateNodeArguments createNodeArguments = newCreateNodeArgs().
            parent( NodePath.ROOT ).
            name( "myItem" ).
            rootDataSet( rootDataSet ).
            build();

        Node persisted = dao.createNode( createNodeArguments );

        // exercise
        Node fetched = dao.getNodeById( persisted.id() );
        assertNotNull( fetched.id() );
        assertEquals( persisted.name(), fetched.name() );
        assertEquals( persisted.property( "propertyOfItem" ).getString(), fetched.property( "propertyOfItem" ).getString() );
    }

    @Test
    public void when_createNode_given_a_path_to_parent_that_is_not_absolute_then_IllegalArgumentException_is_thrown()
        throws Exception
    {
        // setup

        RootDataSet rootDataSet = new RootDataSet();
        rootDataSet.setProperty( "propertyOfItem", new Value.String( "A" ) );
        CreateNodeArguments createNodeArguments = newCreateNodeArgs().
            parent( new NodePath( "not-absolute-parent-path" ) ).
            name( "myItem" ).
            rootDataSet( rootDataSet ).
            build();

        // exercise
        try
        {
            dao.createNode( createNodeArguments );
            fail( "Expected exception" );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof IllegalArgumentException );
            assertEquals( "Path to parent Item must be absolute: not-absolute-parent-path", e.getMessage() );
        }
    }

    @Test
    public void when_createNode_given_a_parent_path_to_an_item_that_does_not_exist_then_NoNodeAtPathFound_is_thrown()
        throws Exception
    {
        // setup
        CreateNodeArguments createNodeArguments = CreateNodeArguments.newCreateNodeArgs().
            parent( new NodePath( "/does-not-exist" ) ).
            name( "myItem" ).
            build();

        // exercise
        try
        {
            dao.createNode( createNodeArguments );
            fail( "Expected exception NoItemAtPathFound" );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            assertTrue( e instanceof NoNodeAtPathFound );
            NoNodeAtPathFound noNodeAtPathFound = (NoNodeAtPathFound) e;
            assertEquals( "/does-not-exist", noNodeAtPathFound.getPath().toString() );
        }
    }

    @Test
    public void when_createNode_given_an_already_persisted_item_then_NodeAlreadyExist_is_thrown()
        throws Exception
    {
        // setup
        dao.createNode( newCreateNodeArgs().
            parent( NodePath.ROOT ).
            name( "myItem" ).
            build() );

        // exercise
        try
        {
            dao.createNode( newCreateNodeArgs().
                parent( NodePath.ROOT ).
                name( "myItem" ).
                build() );
            fail( "Expected exception ItemAlreadyExist" );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof NodeAlreadyExist );
            assertEquals( "Node already exist: /myItem", e.getMessage() );
        }
    }
}
