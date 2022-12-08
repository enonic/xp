package com.enonic.xp.repo.impl.node;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.RefreshMode;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FindNodesByQueryCommandTest_func_pathMatch
    extends AbstractNodeTest
{
    @BeforeEach
    public void setUp()
        throws Exception
    {
        this.createDefaultRootNode();
    }

    @Test
    public void matches_subPath()
        throws Exception
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
    public void matches_subPath_minimum_match()
        throws Exception
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

        assertOrder( result, node1_1_1, node1_1 );
    }

    @Test
    public void score_order_most_matching_first()
        throws Exception
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

        assertOrder( result, node1_1_1, node1_1, node1 );
    }
}
