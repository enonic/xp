package com.enonic.wem.repo.internal.entity;

import org.junit.Test;

import com.enonic.wem.api.data.PropertyTree;
import com.enonic.wem.api.index.IndexPath;
import com.enonic.wem.api.node.CreateNodeParams;
import com.enonic.wem.api.node.FindNodesByQueryResult;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodeIndexPath;
import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.node.NodeQuery;
import com.enonic.wem.api.query.expr.CompareExpr;
import com.enonic.wem.api.query.expr.DynamicConstraintExpr;
import com.enonic.wem.api.query.expr.FieldExpr;
import com.enonic.wem.api.query.expr.FunctionExpr;
import com.enonic.wem.api.query.expr.QueryExpr;
import com.enonic.wem.api.query.expr.ValueExpr;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.*;

public class FindNodesByQueryCommandTest
    extends AbstractNodeTest
{

    @Test
    public void get_by_parent()
        throws Exception
    {
        final Node node1 = createNode( CreateNodeParams.create().
            name( "my-node-1" ).
            parent( NodePath.ROOT ).
            build() );

        final Node node2 = createNode( CreateNodeParams.create().
            name( "my-node-2" ).
            parent( NodePath.ROOT ).
            build() );

        final Node childNode1 = createNode( CreateNodeParams.create().
            name( "child-node" ).
            parent( node1.path() ).
            build() );

        refresh();

        printContentRepoIndex();

        final NodeQuery query = NodeQuery.create().parent( NodePath.ROOT ).build();
        FindNodesByQueryResult result = doFindByQuery( query );
        assertEquals( 2, result.getNodes().getSize() );
        assertNotNull( result.getNodes().getNodeById( node1.id() ) );
        assertNotNull( result.getNodes().getNodeById( node2.id() ) );

        final NodeQuery childQuery = NodeQuery.create().parent( node1.path() ).build();
        result = doFindByQuery( childQuery );
        assertEquals( 1, result.getNodes().getSize() );
        assertNotNull( result.getNodes().getNodeById( childNode1.id() ) );
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
    public void compare_gt()
        throws Exception
    {
        final PropertyTree data = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
        data.setDouble( "my-value", 5.5 );

        createNode( CreateNodeParams.create().
            name( "my-node-1" ).
            parent( NodePath.ROOT ).
            data( data ).
            build() );

        final PropertyTree data2 = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
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
    public void fulltext()
        throws Exception
    {
        final Node node = createNode( CreateNodeParams.create().
            name( "my-node-1" ).
            parent( NodePath.ROOT ).
            build() );

        refresh();

        final NodeQuery query = NodeQuery.create().
            query( QueryExpr.from( new DynamicConstraintExpr(
                FunctionExpr.from( "fulltext", ValueExpr.string( NodeIndexPath.NAME.getPath() ),
                                   ValueExpr.string( "My node name is my-node-1" ), ValueExpr.string( "OR" ) ) ) ) ).
            build();

        final FindNodesByQueryResult result = doFindByQuery( query );

        assertEquals( 1, result.getNodes().getSize() );
        assertNotNull( result.getNodes().getNodeById( node.id() ) );
    }

}
