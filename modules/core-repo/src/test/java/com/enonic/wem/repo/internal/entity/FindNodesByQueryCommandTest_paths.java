package com.enonic.wem.repo.internal.entity;

import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;

public class FindNodesByQueryCommandTest_paths
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
    public void path_equals()
        throws Exception
    {

        final Node rootNode = createNodeWithPath( NodePath.ROOT, "rootNode" );
        final Node node1 = createNodeWithPath( rootNode.path(), "node1" );
        final Node node2 = createNodeWithPath( rootNode.path(), "node2" );
        final Node node3 = createNodeWithPath( rootNode.path(), "node3" );
        createNodeWithPath( node1.path(), "node1_1" );
        createNodeWithPath( node2.path(), "node2_1" );
        createNodeWithPath( node3.path(), "node3_1" );

        queryAndAssert( "_path = '/rootNode'", 1 );
        queryAndAssert( "_path = '/rootNode/node1'", 1 );
        queryAndAssert( "_path = '/rootNode/node2'", 1 );
        queryAndAssert( "_path = '/rootNode/node3'", 1 );
        queryAndAssert( "_path = '/rootNode/node1/node1_1'", 1 );
        queryAndAssert( "_path = '/rootNode/node2/node2_1'", 1 );
        queryAndAssert( "_path = '/rootNode/node3/node3_1'", 1 );
    }

    @Test
    public void path_wildcard()
        throws Exception
    {

        final Node rootNode = createNodeWithPath( NodePath.ROOT, "rootNode" );
        final Node node1 = createNodeWithPath( rootNode.path(), "node1" );
        final Node node2 = createNodeWithPath( rootNode.path(), "node2" );
        final Node node3 = createNodeWithPath( rootNode.path(), "node3" );
        createNodeWithPath( node1.path(), "node1_1" );
        createNodeWithPath( node2.path(), "node2_1" );
        createNodeWithPath( node3.path(), "node3_1" );

        queryAndAssert( "_path LIKE '/rootNode*'", 7 );
        queryAndAssert( "_path LIKE '/rootNode/*'", 6 );
        queryAndAssert( "_path LIKE '/rootNode/node1/*'", 1 );
        queryAndAssert( "_path LIKE '/rootNode/node2/*'", 1 );
        queryAndAssert( "_path LIKE '/rootNode/node3/*'", 1 );
        queryAndAssert( "_path LIKE '/rootNode/node1/node1_1'", 1 );
    }


    private Node createNodeWithPath( final NodePath parent, final String name )
    {
        return createNode( CreateNodeParams.create().
            parent( NodePath.newPath( parent ).build() ).
            name( name ).
            setNodeId( NodeId.from( name ) ).
            build() );
    }

}
