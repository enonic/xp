package com.enonic.xp.core.node;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.core.AbstractNodeTest;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.RefreshMode;

class FindNodesByQueryCommandTest_paths
    extends AbstractNodeTest
{
    @BeforeEach
    void setUp()
    {
        this.createDefaultRootNode();
    }


    @Test
    void path_equals()
    {

        final Node rootNode = createNode( NodePath.ROOT, "rootNode" );
        final Node node1 = createNode( rootNode.path(), "node1" );
        final Node node2 = createNode( rootNode.path(), "node2" );
        final Node node3 = createNode( rootNode.path(), "node3" );
        createNode( node1.path(), "node1_1" );
        createNode( node2.path(), "node2_1" );
        createNode( node3.path(), "node3_1" );

        nodeService.refresh( RefreshMode.ALL );

        queryAndAssert( "_path = '/rootNode'", 1 );
        queryAndAssert( "_path = '/rootNode/node1'", 1 );
        queryAndAssert( "_path = '/rootNode/node2'", 1 );
        queryAndAssert( "_path = '/rootNode/node3'", 1 );
        queryAndAssert( "_path = '/rootNode/node1/node1_1'", 1 );
        queryAndAssert( "_path = '/rootNode/node2/node2_1'", 1 );
        queryAndAssert( "_path = '/rootNode/node3/node3_1'", 1 );
    }

    @Test
    void path_wildcard()
    {

        final Node rootNode = createNode( NodePath.ROOT, "rootNode" );
        final Node node1 = createNode( rootNode.path(), "node1" );
        final Node node2 = createNode( rootNode.path(), "node2" );
        final Node node3 = createNode( rootNode.path(), "node3" );
        createNode( node1.path(), "node1_1" );
        createNode( node2.path(), "node2_1" );
        createNode( node3.path(), "node3_1" );

        nodeService.refresh( RefreshMode.ALL );

        queryAndAssert( "_path LIKE '/rootNode*'", 7 );
        queryAndAssert( "_path LIKE '/rootNode/*'", 6 );
        queryAndAssert( "_path LIKE '/rootNode/node1/*'", 1 );
        queryAndAssert( "_path LIKE '/rootNode/node2/*'", 1 );
        queryAndAssert( "_path LIKE '/rootNode/node3/*'", 1 );
        queryAndAssert( "_path LIKE '/rootNode/node1/node1_1'", 1 );
    }

}
