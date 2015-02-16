package com.enonic.wem.repo.internal.entity;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.Value;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.query.expr.CompareExpr;
import com.enonic.xp.query.expr.FieldExpr;
import com.enonic.xp.query.expr.QueryExpr;
import com.enonic.xp.query.expr.ValueExpr;
import com.enonic.xp.query.filter.RangeFilter;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.*;

public class FindNodesByQueryCommandTest
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
    public void query_number_different_field_name_case()
        throws Exception
    {
        final PropertyTree data13 = new PropertyTree();
        data13.addLong( "myProperty", 10l );

        final PropertyTree data2 = new PropertyTree();
        data2.addLong( "myProperty", 20l );

        final Node node1 = createNode( CreateNodeParams.create().
            name( "my-node-1" ).
            parent( NodePath.ROOT ).
            data( data13 ).
            build() );

        final Node node2 = createNode( CreateNodeParams.create().
            name( "my-node-2" ).
            parent( NodePath.ROOT ).
            data( data2 ).
            build() );

        final Node node3 = createNode( CreateNodeParams.create().
            name( "child-node" ).
            parent( node1.path() ).
            data( data13 ).
            build() );

        final NodeQuery query = NodeQuery.create().
            query( QueryExpr.from( CompareExpr.eq( FieldExpr.from( "MYProperty" ), ValueExpr.number( 10l ) ) ) ).
            addPostFilter( RangeFilter.create().fieldName( "MYProperty" ).from( Value.newLong( 0l ) ).to( Value.newLong( 15l ) ).build() ).
            build();

        final FindNodesByQueryResult result = doFindByQuery( query );

        assertEquals( 2, result.getNodes().getSize() );
        assertNotNull( result.getNodes().getNodeById( node1.id() ) );
        assertNotNull( result.getNodes().getNodeById( node3.id() ) );
        assertNull( result.getNodes().getNodeById( node2.id() ) );
    }

    @Test
    public void query_instant_different_field_name_case()
        throws Exception
    {
        final Instant BASE_INSTANT = Instant.parse( "2000-01-01T00:00:00.00Z" );
        final PropertyTree data13 = new PropertyTree();
        data13.addInstant( "myProperty", BASE_INSTANT.plus( 10, ChronoUnit.DAYS ) );

        final PropertyTree data2 = new PropertyTree();
        data2.addInstant( "myProperty", BASE_INSTANT.plus( 20, ChronoUnit.DAYS ) );

        final Node node1 = createNode( CreateNodeParams.create().
            name( "my-node-1" ).
            parent( NodePath.ROOT ).
            data( data13 ).
            build() );

        final Node node2 = createNode( CreateNodeParams.create().
            name( "my-node-2" ).
            parent( NodePath.ROOT ).
            data( data2 ).
            build() );

        final Node node3 = createNode( CreateNodeParams.create().
            name( "child-node" ).
            parent( node1.path() ).
            data( data13 ).
            build() );

        final Value from = Value.newInstant( BASE_INSTANT );
        final Value to = Value.newInstant( BASE_INSTANT.plus( 15, ChronoUnit.DAYS ) );
        final QueryExpr queryExpr = QueryExpr.from(
            CompareExpr.eq( FieldExpr.from( "MYProperty" ), ValueExpr.instant( BASE_INSTANT.plus( 10, ChronoUnit.DAYS ).toString() ) ) );
        final NodeQuery query = NodeQuery.create().
            query( queryExpr ).
            addPostFilter( RangeFilter.create().fieldName( "MYProperty" ).from( from ).to( to ).build() ).
            build();

        final FindNodesByQueryResult result = doFindByQuery( query );

        assertEquals( 2, result.getNodes().getSize() );
        assertNotNull( result.getNodes().getNodeById( node1.id() ) );
        assertNotNull( result.getNodes().getNodeById( node3.id() ) );
        assertNull( result.getNodes().getNodeById( node2.id() ) );
    }

    @Test
    public void nested_paths()
        throws Exception
    {
        final PropertyTree data = new PropertyTree();

        final String path1 = "test.string.with.path";
        final String value1 = "myValue";

        data.setString( path1, value1 );
        final String path2 = "test.string.with.path2";
        final String value2 = "myValue2";

        data.setString( path2, value2 );

        final Node node1 = createNode( CreateNodeParams.create().
            name( "my-node-1" ).
            parent( NodePath.ROOT ).
            data( data ).
            build() );

        createNode( CreateNodeParams.create().
            name( "my-node-2" ).
            parent( NodePath.ROOT ).
            build() );

        createNode( CreateNodeParams.create().
            name( "child-node" ).
            parent( node1.path() ).
            build() );

        queryAndAssert( path1, value1, node1 );
        queryAndAssert( path2, value2, node1 );
    }

    private void queryAndAssert( final String path1, final String value1, final Node node1 )
    {
        final NodeQuery query = NodeQuery.create().
            query( QueryExpr.from( CompareExpr.eq( FieldExpr.from( path1 ), ValueExpr.string( value1 ) ) ) ).
            build();

        final FindNodesByQueryResult result = doFindByQuery( query );

        assertEquals( 1, result.getNodes().getSize() );
        assertNotNull( result.getNodes().getNodeById( node1.id() ) );
    }

}
