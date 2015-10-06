package com.enonic.xp.repo.impl.node;

import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.IndexPath;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeIndexPath;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.query.expr.CompareExpr;
import com.enonic.xp.query.expr.FieldExpr;
import com.enonic.xp.query.expr.QueryExpr;
import com.enonic.xp.query.expr.ValueExpr;

import static org.junit.Assert.*;

public class FindNodesByQueryCommandTest_compare
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
    public void compare_gt()
        throws Exception
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

        final NodeQuery query = NodeQuery.create().
            query( QueryExpr.from( CompareExpr.gt( FieldExpr.from( IndexPath.from( "my-value" ) ), ValueExpr.number( 7 ) ) ) ).
            build();

        final FindNodesByQueryResult result = doFindByQuery( query );

        assertEquals( 1, result.getNodes().getSize() );
        assertNotNull( result.getNodes().getNodeById( node2.id() ) );
    }

    @Test
    public void compare_eq()
        throws Exception
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

        final NodeQuery query = NodeQuery.create().
            query( QueryExpr.from( CompareExpr.eq( FieldExpr.from( NodeIndexPath.NAME ), ValueExpr.string( "my-node-1" ) ) ) ).
            build();

        final FindNodesByQueryResult result = doFindByQuery( query );

        assertEquals( 1, result.getNodes().getSize() );
        assertNotNull( result.getNodes().getNodeById( node1.id() ) );
    }

    @Test
    public void compare_eq_numeric()
        throws Exception
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

        final NodeQuery query = NodeQuery.create().
            query( QueryExpr.from( CompareExpr.eq( FieldExpr.from( "myDouble" ), ValueExpr.number( 2.0 ) ) ) ).
            build();

        final FindNodesByQueryResult result = doFindByQuery( query );

        assertEquals( 1, result.getNodes().getSize() );
        assertNotNull( result.getNodes().getNodeById( node1.id() ) );
    }


    @Test
    public void compare_exists()
        throws Exception
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

        printContentRepoIndex();

        final FindNodesByQueryResult result = doQuery( "myCategory LIKE '*' AND NOT myCategory = 'article'" );
        assertEquals( 1, result.getHits() );
    }


}
