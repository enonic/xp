package com.enonic.xp.repo.impl.entity;

import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;

public class FindNodesByQueryCommandTest_numeric
    extends AbstractNodeTest
{
    @Before
    public void setUp()
        throws Exception
    {
        super.setUp();
        this.createDefaultRootNode();
    }

    @Test
    public void double_values()
        throws Exception
    {

        createNodeWithDoubleValue( 1.0, "node1", NodePath.ROOT );
        createNodeWithDoubleValue( 5.0, "node2", NodePath.ROOT );
        createNodeWithDoubleValue( 10.0, "node3", NodePath.ROOT );

        queryAndAssert( "myNumber = 1", 1 );
        queryAndAssert( "myNumber = 1.0", 1 );
        queryAndAssert( "myNumber > 1", 2 );
        queryAndAssert( "myNumber < 10", 2 );
    }

    @Test
    public void long_values()
        throws Exception
    {

        createNodeWithLongValue( 1L, "node1", NodePath.ROOT );
        createNodeWithLongValue( 5L, "node2", NodePath.ROOT );
        createNodeWithLongValue( 10L, "node3", NodePath.ROOT );

        queryAndAssert( "myNumber = 1", 1 );
        queryAndAssert( "myNumber = 1.0", 1 );
        queryAndAssert( "myNumber > 1", 2 );
        queryAndAssert( "myNumber < 10", 2 );
    }


    private Node createNodeWithDoubleValue( final Double number, final String name, final NodePath parent )
    {
        final PropertyTree data = new PropertyTree();
        data.addDouble( "myNumber", number );

        return createNode( CreateNodeParams.create().
            parent( parent ).
            name( name ).
            data( data ).
            build() );
    }

    private Node createNodeWithLongValue( final Long number, final String name, final NodePath parent )
    {
        final PropertyTree data = new PropertyTree();
        data.addLong( "myNumber", number );

        return createNode( CreateNodeParams.create().
            parent( parent ).
            name( name ).
            data( data ).
            build() );
    }
}
