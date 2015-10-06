package com.enonic.xp.repo.impl.node.query.aggregation;

import java.time.Instant;

import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.aggregation.BucketAggregation;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.query.aggregation.DateHistogramAggregationQuery;
import com.enonic.xp.repo.impl.node.AbstractNodeTest;

import static org.junit.Assert.*;

public class DateHistogramAggregationsTest
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
    public void intervals()
        throws Exception
    {
        createNode( Instant.parse( "2014-12-10T10:00:00Z" ), "n1", NodePath.ROOT );
        createNode( Instant.parse( "2014-12-10T10:30:00Z" ), "n2", NodePath.ROOT );
        createNode( Instant.parse( "2014-12-10T11:30:00Z" ), "n3", NodePath.ROOT );
        createNode( Instant.parse( "2014-12-10T12:45:00Z" ), "n4", NodePath.ROOT );
        createNode( Instant.parse( "2014-12-10T13:59:59Z" ), "n5", NodePath.ROOT );
        createNode( Instant.parse( "2014-12-10T14:01:00Z" ), "n6", NodePath.ROOT );

        final NodeQuery query = NodeQuery.create().
            addAggregationQuery( DateHistogramAggregationQuery.create( "hour" ).
                format( "Y-MM-DD" ).
                fieldName( "instant" ).
                interval( "1h" ).
                build() ).
            addAggregationQuery( DateHistogramAggregationQuery.create( "day" ).
                fieldName( "instant" ).
                interval( "1d" ).
                build() ).
            addAggregationQuery( DateHistogramAggregationQuery.create( "minute" ).
                fieldName( "instant" ).
                interval( "1m" ).
                build() ).
            build();

        FindNodesByQueryResult result = doFindByQuery( query );

        assertEquals( 3, result.getAggregations().getSize() );

        final BucketAggregation hour = (BucketAggregation) result.getAggregations().get( "hour" );
        final BucketAggregation day = (BucketAggregation) result.getAggregations().get( "day" );
        final BucketAggregation minute = (BucketAggregation) result.getAggregations().get( "minute" );

        assertEquals( 5, hour.getBuckets().getSize() );
        assertEquals( 1, day.getBuckets().getSize() );
        assertEquals( 6, minute.getBuckets().getSize() );
    }

    @Test
    public void min_doc_count()
        throws Exception
    {
        // Create 5 nodes with two hours between
        createNode( Instant.parse( "2014-12-10T10:00:00Z" ), "n1", NodePath.ROOT );
        createNode( Instant.parse( "2014-12-10T12:00:00Z" ), "n2", NodePath.ROOT );
        createNode( Instant.parse( "2014-12-10T14:00:00Z" ), "n3", NodePath.ROOT );
        createNode( Instant.parse( "2014-12-10T16:00:59Z" ), "n4", NodePath.ROOT );
        createNode( Instant.parse( "2014-12-10T18:00:00Z" ), "n5", NodePath.ROOT );

        final NodeQuery query = NodeQuery.create().
            addAggregationQuery( DateHistogramAggregationQuery.create( "dateHistogramWithZero" ).
                fieldName( "instant" ).
                interval( "1h" ).
                minDocCount( 0l ).
                build() ).
            addAggregationQuery( DateHistogramAggregationQuery.create( "dateHistogramWithDefault" ).
                fieldName( "instant" ).
                interval( "1h" ).
                build() ).
            build();

        FindNodesByQueryResult result = doFindByQuery( query );

        assertEquals( 2, result.getAggregations().getSize() );

        // The min-doc-count=0 will also return the empty fill-ins
        final BucketAggregation zeroDocCount = (BucketAggregation) result.getAggregations().get( "dateHistogramWithZero" );
        final BucketAggregation defaultDocCount = (BucketAggregation) result.getAggregations().get( "dateHistogramWithDefault" );

        assertEquals( 9, zeroDocCount.getBuckets().getSize() );
        assertEquals( 5, defaultDocCount.getBuckets().getSize() );
    }


    @Test
    public void format()
        throws Exception
    {
        // Create 5 nodes with two hours between
        createNode( Instant.parse( "2014-12-10T10:30:00Z" ), "n1", NodePath.ROOT );
        createNode( Instant.parse( "2014-12-10T10:40:00Z" ), "n2", NodePath.ROOT );

        final NodeQuery query = NodeQuery.create().
            addAggregationQuery( DateHistogramAggregationQuery.create( "hhMM" ).
                fieldName( "instant" ).
                interval( "1h" ).
                format( "HH:mm" ).
                build() ).
            addAggregationQuery( DateHistogramAggregationQuery.create( "mm" ).
                fieldName( "instant" ).
                interval( "1h" ).
                format( "mm" ).
                build() ).
            build();

        FindNodesByQueryResult result = doFindByQuery( query );

        assertEquals( 2, result.getAggregations().getSize() );

        // The min-doc-count=0 will also return the empty fill-ins
        final BucketAggregation hhMM = (BucketAggregation) result.getAggregations().get( "hhMM" );
        final BucketAggregation mm = (BucketAggregation) result.getAggregations().get( "mm" );

        assertEquals( 1, hhMM.getBuckets().getSize() );
        assertEquals( 1, mm.getBuckets().getSize() );

        assertEquals( "10:00", hhMM.getBuckets().first().getKey() );
        assertEquals( "00", mm.getBuckets().first().getKey() );
    }


    private Node createNode( final Instant instantValue, final String name, final NodePath parent )
    {
        final PropertyTree data = new PropertyTree();
        data.addInstant( "instant", instantValue );

        return createNode( CreateNodeParams.create().
            parent( parent ).
            name( name ).
            data( data ).
            build() );

    }

}
