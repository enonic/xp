package com.enonic.wem.repo.internal.entity;

import java.time.Instant;

import org.junit.Ignore;
import org.junit.Test;

import com.enonic.wem.api.aggregation.Aggregation;
import com.enonic.wem.api.aggregation.Bucket;
import com.enonic.wem.api.aggregation.BucketAggregation;
import com.enonic.wem.api.data.PropertyTree;
import com.enonic.wem.api.node.CreateNodeParams;
import com.enonic.wem.api.node.FindNodesByQueryResult;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.node.NodeQuery;
import com.enonic.wem.api.query.aggregation.DateHistogramAggregationsQuery;
import com.enonic.wem.api.query.aggregation.DateInterval;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.*;

public class FindContentByQueryCommand_histogramAggregationsTest
    extends AbstractNodeTest
{

    @Test
    public void hours()
        throws Exception
    {
        createNode( Instant.parse( "2014-12-10T10:00:00Z" ), "n1", NodePath.ROOT );
        createNode( Instant.parse( "2014-12-10T11:30:00Z" ), "n2", NodePath.ROOT );
        createNode( Instant.parse( "2014-12-10T12:45:00Z" ), "n3", NodePath.ROOT );
        createNode( Instant.parse( "2014-12-10T13:59:59Z" ), "n4", NodePath.ROOT );
        createNode( Instant.parse( "2014-12-10T14:01:00Z" ), "n5", NodePath.ROOT );

        final NodeQuery query = NodeQuery.create().
            addAggregationQuery( DateHistogramAggregationsQuery.create( "dateHistogram" ).
                fieldName( "instant" ).
                interval( DateInterval.from( "1h" ) ).
                build() ).
            build();

        FindNodesByQueryResult result = doFindByQuery( query );

        assertEquals( 1, result.getAggregations().getSize() );

        final Aggregation dateHistogram = result.getAggregations().get( "dateHistogram" );

        assertTrue( dateHistogram instanceof BucketAggregation );

        final BucketAggregation dateBuckets = (BucketAggregation) dateHistogram;

        assertEquals( 5, dateBuckets.getBuckets().getSize() );

        for ( final Bucket bucket : dateBuckets.getBuckets() )
        {
            assertEquals( 1, bucket.getDocCount() );
        }
    }


    @Ignore
    @Test
    public void test()
        throws Exception
    {
        createNode( 6.0, "n1", NodePath.ROOT );
        createNode( 8.0, "n2", NodePath.ROOT );
        createNode( 11.0, "n3", NodePath.ROOT );
        createNode( 15.0, "n4", NodePath.ROOT );
        createNode( 17.0, "n5", NodePath.ROOT );
        createNode( 2.0, "n6", NodePath.ROOT );
        createNode( 24.0, "n7", NodePath.ROOT );

        final NodeQuery query = NodeQuery.create().
            addAggregationQuery( DateHistogramAggregationsQuery.create( "ofesfsefst" ).
                fieldName( "instant" ).
                interval( DateInterval.from( "1m" ) ).
                build() ).
            build();

        printContentRepoIndex();

        FindNodesByQueryResult result = doFindByQuery( query );

        assertEquals( 1, result.getAggregations().getSize() );

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


    private Node createNode( final Double value, final String name, final NodePath parent )
    {
        final PropertyTree data = new PropertyTree();
        data.addDouble( "instant", value );

        return createNode( CreateNodeParams.create().
            parent( parent ).
            name( name ).
            data( data ).
            build() );

    }

}
