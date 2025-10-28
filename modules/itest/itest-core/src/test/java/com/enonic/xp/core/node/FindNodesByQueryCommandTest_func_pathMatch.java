package com.enonic.xp.core.node;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.core.AbstractNodeTest;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.RefreshMode;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FindNodesByQueryCommandTest_func_pathMatch
    extends AbstractNodeTest
{
    @BeforeEach
    void setUp()
    {
        this.createDefaultRootNode();
    }

    @Test
    void matches_subPath()
    {
        createNode( CreateNodeParams.create().
            name( "node1" ).
            parent( NodePath.ROOT ).
            build() );
        nodeService.refresh( RefreshMode.ALL );

        final FindNodesByQueryResult result = doQuery( "pathMatch('_path', '/node1/node1_1/node1_1_1')" );

        assertEquals( 1, result.getNodeIds().getSize() );
    }


    @Test
    void matches_subPath_minimum_match()
    {
        final Node node1 = createNode( CreateNodeParams.create().
            name( "node1" ).
            setNodeId( NodeId.from( "node1" ) ).
            parent( NodePath.ROOT ).
            build() );

        final Node node1_1 = createNode( CreateNodeParams.create().
            name( "node1_1" ).
            setNodeId( NodeId.from( "node1_1" ) ).
            parent( node1.path() ).
            build() );

        final Node node1_1_1 = createNode( CreateNodeParams.create().
            name( "node1_1_1" ).
            setNodeId( NodeId.from( "node1_1_1" ) ).
            parent( node1_1.path() ).
            build() );
        nodeService.refresh( RefreshMode.ALL );

        final FindNodesByQueryResult result = doQuery( "pathMatch('_path', '/node1/node1_1/node1_1_1', 2)" );

        assertOrder( result, node1_1_1.id(), node1_1.id() );
    }

    @Test
    void score_order_most_matching_first()
    {
        final Node node1 = createNode( CreateNodeParams.create().
            name( "node1" ).
            setNodeId( NodeId.from( "node1" ) ).
            parent( NodePath.ROOT ).
            build() );

        final Node node1_1 = createNode( CreateNodeParams.create().
            name( "node1_1" ).
            setNodeId( NodeId.from( "node1_1" ) ).
            parent( node1.path() ).
            build() );

        final Node node1_1_1 = createNode( CreateNodeParams.create().
            name( "node1_1_1" ).
            setNodeId( NodeId.from( "node1_1_1" ) ).
            parent( node1_1.path() ).
            build() );
        nodeService.refresh( RefreshMode.ALL );

        final FindNodesByQueryResult result = doQuery( "pathMatch('_path', '/node1/node1_1/node1_1_1')" );

        assertOrder( result, node1_1_1.id(), node1_1.id(), node1.id() );
    }
}
