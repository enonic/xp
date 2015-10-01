package com.enonic.xp.repo.impl.entity.query.aggregation;

import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.aggregation.Bucket;
import com.enonic.xp.aggregation.BucketAggregation;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.query.aggregation.HistogramAggregationQuery;
import com.enonic.xp.repo.impl.entity.AbstractNodeTest;

import static org.junit.Assert.*;

public class HistogramAggregationTest
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
        createNode( 10d, "n1", NodePath.ROOT );
        createNode( 20d, "n2", NodePath.ROOT );
        createNode( 30d, "n3", NodePath.ROOT );
        createNode( 40d, "n4", NodePath.ROOT );
        createNode( 50d, "n5", NodePath.ROOT );
        createNode( 60d, "n6", NodePath.ROOT );

        final NodeQuery query = NodeQuery.create().
            addAggregationQuery( HistogramAggregationQuery.create( "ten" ).
                fieldName( "value" ).
                interval( 10l ).
                build() ).
            addAggregationQuery( HistogramAggregationQuery.create( "twenty" ).
                fieldName( "value" ).
                interval( 20l ).
                build() ).
            addAggregationQuery( HistogramAggregationQuery.create( "fourty" ).
                fieldName( "value" ).
                interval( 40l ).
                build() ).
            build();

        FindNodesByQueryResult result = doFindByQuery( query );

        assertEquals( 3, result.getAggregations().getSize() );

        final BucketAggregation ten = (BucketAggregation) result.getAggregations().get( "ten" );
        final BucketAggregation twenty = (BucketAggregation) result.getAggregations().get( "twenty" );
        final BucketAggregation fourty = (BucketAggregation) result.getAggregations().get( "fourty" );

        assertEquals( 6, ten.getBuckets().getSize() );
        assertEquals( 4, twenty.getBuckets().getSize() );
        assertEquals( 2, fourty.getBuckets().getSize() );
    }

    @Test
    public void order()
        throws Exception
    {
        createNode( 1d, "n1", NodePath.ROOT );
        createNode( 2d, "n2", NodePath.ROOT );
        createNode( 3d, "n3", NodePath.ROOT );
        createNode( 10d, "n4", NodePath.ROOT );
        createNode( 11d, "n5", NodePath.ROOT );
        createNode( 20d, "n6", NodePath.ROOT );

        final NodeQuery query = NodeQuery.create().
            addAggregationQuery( HistogramAggregationQuery.create( "count_asc" ).
                fieldName( "value" ).
                interval( 10l ).
                order( HistogramAggregationQuery.Order.COUNT_ASC ).
                build() ).
            addAggregationQuery( HistogramAggregationQuery.create( "key_asc" ).
                fieldName( "value" ).
                interval( 20l ).
                order( HistogramAggregationQuery.Order.KEY_ASC ).
                build() ).
            build();

        FindNodesByQueryResult result = doFindByQuery( query );

        assertEquals( 2, result.getAggregations().getSize() );

        final BucketAggregation countAsc = (BucketAggregation) result.getAggregations().get( "count_asc" );
        final BucketAggregation keyAsc = (BucketAggregation) result.getAggregations().get( "key_asc" );

        final Iterator<Bucket> countAsIterator = countAsc.getBuckets().iterator();
        long previous = 0;
        while ( countAsIterator.hasNext() )
        {
            final long nextCount = countAsIterator.next().getDocCount();
            assertTrue( previous < nextCount );
            previous = nextCount;
        }

        final Iterator<Bucket> keyAscIterator = keyAsc.getBuckets().iterator();
        String key = null;
        while ( keyAscIterator.hasNext() )
        {
            final String next = keyAscIterator.next().getKey();
            assertTrue( key == null || key.compareTo( next ) < 0 );
            key = next;
        }
    }

    private Node createNode( final Double value, final String name, final NodePath parent )
    {
        final PropertyTree data = new PropertyTree();
        data.addDouble( "value", value );

        return createNode( CreateNodeParams.create().
            parent( parent ).
            name( name ).
            data( data ).
            build() );

    }


}
