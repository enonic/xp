package com.enonic.xp.core.node;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.core.AbstractNodeTest;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.IndexPath;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeIndexPath;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.query.expr.CompareExpr;
import com.enonic.xp.query.expr.FieldExpr;
import com.enonic.xp.query.expr.QueryExpr;
import com.enonic.xp.query.expr.ValueExpr;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FindNodesByQueryCommandTest_compare
    extends AbstractNodeTest
{
    @BeforeEach
    void setUp()
    {
        this.createDefaultRootNode();
    }

    @Test
    void compare_gt()
    {
        final PropertyTree data = new PropertyTree();
        data.setDouble( "my-value", 5.5 );

        createNode( CreateNodeParams.create().
            name( "my-node-1" ).
            parent( NodePath.ROOT ).
            data( data ).
            build() );

        final PropertyTree data2 = new PropertyTree();
        data2.setDouble( "my-value", 10.0 );

        final Node node2 = createNode( CreateNodeParams.create().
            name( "my-node-2" ).
            parent( NodePath.ROOT ).
            data( data2 ).
            build() );
        nodeService.refresh( RefreshMode.ALL );

        final NodeQuery query = NodeQuery.create().
            query( QueryExpr.from( CompareExpr.gt( FieldExpr.from( IndexPath.from( "my-value" ) ), ValueExpr.number( 7 ) ) ) ).
            build();

        final FindNodesByQueryResult result = doFindByQuery( query );

        assertEquals( 1, result.getNodeIds().getSize() );
        assertTrue( result.getNodeIds().contains( node2.id() ) );
    }

    @Test
    void compare_eq()
    {
        final Node node1 = createNode( CreateNodeParams.create().
            name( "my-node-1" ).
            parent( NodePath.ROOT ).
            build() );

        createNode( CreateNodeParams.create().
            name( "my-node-2" ).
            parent( NodePath.ROOT ).
            build() );

        createNode( CreateNodeParams.create().
            name( "child-node" ).
            parent( node1.path() ).
            build() );
        nodeService.refresh( RefreshMode.ALL );

        final NodeQuery query = NodeQuery.create().
            query( QueryExpr.from( CompareExpr.eq( FieldExpr.from( NodeIndexPath.NAME ), ValueExpr.string( "my-node-1" ) ) ) ).
            build();

        final FindNodesByQueryResult result = doFindByQuery( query );

        assertEquals( 1, result.getNodeIds().getSize() );
        assertTrue( result.getNodeIds().contains( node1.id() ) );
    }

    @Test
    void compare_eq_numeric()
    {
        final PropertyTree data = new PropertyTree();
        data.addDouble( "myDouble", 2.0 );

        final Node node1 = createNode( CreateNodeParams.create().
            name( "my-node-1" ).
            parent( NodePath.ROOT ).
            data( data ).
            build() );

        final PropertyTree data2 = new PropertyTree();
        data2.addString( "myDouble", "2.0" );

        createNode( CreateNodeParams.create().
            name( "my-node-2" ).
            parent( NodePath.ROOT ).
            data( data2 ).
            build() );
        nodeService.refresh( RefreshMode.ALL );

        final NodeQuery query = NodeQuery.create().
            query( QueryExpr.from( CompareExpr.eq( FieldExpr.from( "myDouble" ), ValueExpr.number( 2.0 ) ) ) ).
            build();

        final FindNodesByQueryResult result = doFindByQuery( query );

        assertEquals( 1, result.getNodeIds().getSize() );
        assertTrue( result.getNodeIds().contains( node1.id() ) );
    }


    @Test
    void compare_exists()
    {
        final PropertyTree data = new PropertyTree();
        data.addString( "myCategory", "article" );

        final Node node1 = createNode( CreateNodeParams.create().
            name( "my-node-1" ).
            parent( NodePath.ROOT ).
            data( data ).
            build() );

        final PropertyTree data2 = new PropertyTree();
        data2.addString( "myCategory", "document" );

        createNode( CreateNodeParams.create().
            name( "my-node-2" ).
            parent( NodePath.ROOT ).
            data( data2 ).
            build() );

        createNode( CreateNodeParams.create().
            name( "child-node" ).
            parent( node1.path() ).
            build() );
        nodeService.refresh( RefreshMode.ALL );

        printContentRepoIndex();

        final FindNodesByQueryResult result = doQuery( "myCategory LIKE '*' AND NOT myCategory = 'article'" );
        assertEquals( 1, result.getNodeHits().getSize() );
    }


}
