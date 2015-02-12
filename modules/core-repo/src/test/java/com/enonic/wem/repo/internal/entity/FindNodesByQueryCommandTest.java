package com.enonic.wem.repo.internal.entity;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.junit.Test;

import com.enonic.wem.api.data.PropertySet;
import com.enonic.wem.api.data.PropertyTree;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.index.IndexConfig;
import com.enonic.wem.api.index.IndexPath;
import com.enonic.wem.api.index.PatternIndexConfigDocument;
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
import com.enonic.wem.api.query.filter.RangeFilter;

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


    @Test
    public void norwegian_characters()
        throws Exception
    {
        final PropertyTree data = new PropertyTree();
        data.addString( "myProperty", "æ" );
        final PropertySet userdata = data.addSet( "data" );
        userdata.addString( "displayName", "ø å" );

        final Node node = createNode( CreateNodeParams.create().
            name( "my-node-1" ).
            parent( NodePath.ROOT ).
            data( data ).
            indexConfigDocument( PatternIndexConfigDocument.create().
                analyzer( "content_default" ).
                defaultConfig( IndexConfig.BY_TYPE ).
                build() ).
            build() );

        refresh();

        printContentRepoIndex();

        final NodeQuery query = NodeQuery.create().
            query( QueryExpr.from( new DynamicConstraintExpr(
                FunctionExpr.from( "fulltext", ValueExpr.string( NodeIndexPath.ALL_TEXT.getPath() ), ValueExpr.string( "æ" ),
                                   ValueExpr.string( "OR" ) ) ) ) ).
            build();

        final FindNodesByQueryResult result = doFindByQuery( query );

        assertEquals( 1, result.getNodes().getSize() );
        assertNotNull( result.getNodes().getNodeById( node.id() ) );

        final NodeQuery query2 = NodeQuery.create().
            query( QueryExpr.from( new DynamicConstraintExpr(
                FunctionExpr.from( "fulltext", ValueExpr.string( "data.displayName" ), ValueExpr.string( "ø å" ),
                                   ValueExpr.string( "OR" ) ) ) ) ).
            build();

        final FindNodesByQueryResult result2 = doFindByQuery( query2 );

        assertEquals( 1, result2.getNodes().getSize() );
        assertNotNull( result2.getNodes().getNodeById( node.id() ) );
    }

    @Test
    public void norwegian_characters_check_asciifolding()
        throws Exception
    {
        final PropertyTree data = new PropertyTree();
        data.addString( "myProperty", "æ" );
        final PropertySet userdata = data.addSet( "data" );
        userdata.addString( "displayName", "ø å" );

        final Node node = createNode( CreateNodeParams.create().
            name( "my-node-1" ).
            parent( NodePath.ROOT ).
            data( data ).
            indexConfigDocument( PatternIndexConfigDocument.create().
                analyzer( "content_default" ).
                defaultConfig( IndexConfig.BY_TYPE ).
                build() ).
            build() );

        refresh();

        printContentRepoIndex();

        final NodeQuery query = NodeQuery.create().
            query( QueryExpr.from( new DynamicConstraintExpr(
                FunctionExpr.from( "fulltext", ValueExpr.string( NodeIndexPath.ALL_TEXT.getPath() ), ValueExpr.string( "ae" ),
                                   ValueExpr.string( "OR" ) ) ) ) ).
            build();

        final FindNodesByQueryResult result = doFindByQuery( query );

        assertEquals( 1, result.getNodes().getSize() );
        assertNotNull( result.getNodes().getNodeById( node.id() ) );

        final NodeQuery query2 = NodeQuery.create().
            query( QueryExpr.from( new DynamicConstraintExpr(
                FunctionExpr.from( "fulltext", ValueExpr.string( "data.displayName" ), ValueExpr.string( "o a" ),
                                   ValueExpr.string( "OR" ) ) ) ) ).
            build();

        final FindNodesByQueryResult result2 = doFindByQuery( query2 );

        assertEquals( 1, result2.getNodes().getSize() );
        assertNotNull( result2.getNodes().getNodeById( node.id() ) );
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

}
