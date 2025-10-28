package com.enonic.xp.core.node;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.core.AbstractNodeTest;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.RefreshMode;

class FindNodesByQueryCommandTest_numeric
    extends AbstractNodeTest
{
    @BeforeEach
    void setUp()
    {
        this.createDefaultRootNode();
    }

    @Test
    void double_values()
    {

        createNodeWithDoubleValue( 1.0, "node1", NodePath.ROOT );
        createNodeWithDoubleValue( 5.0, "node2", NodePath.ROOT );
        createNodeWithDoubleValue( 10.0, "node3", NodePath.ROOT );
        nodeService.refresh( RefreshMode.ALL );

        queryAndAssert( "myNumber = 1", 1 );
        queryAndAssert( "myNumber = 1.0", 1 );
        queryAndAssert( "myNumber > 1", 2 );
        queryAndAssert( "myNumber < 10", 2 );
    }

    @Test
    void double_values_in()
    {

        createNodeWithDoubleValue( 1.0, "node1", NodePath.ROOT );
        createNodeWithDoubleValue( 5.2, "node2", NodePath.ROOT );
        createNodeWithDoubleValue( 10.7, "node3", NodePath.ROOT );
        nodeService.refresh( RefreshMode.ALL );

        queryAndAssert( "myNumber IN (1, 10.7)", 2 );
    }


    @Test
    void long_values()
    {

        createNodeWithLongValue( 1L, "node1", NodePath.ROOT );
        createNodeWithLongValue( 5L, "node2", NodePath.ROOT );
        createNodeWithLongValue( 10L, "node3", NodePath.ROOT );
        nodeService.refresh( RefreshMode.ALL );

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
