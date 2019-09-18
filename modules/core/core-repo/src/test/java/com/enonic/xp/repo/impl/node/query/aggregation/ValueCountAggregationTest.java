package com.enonic.xp.repo.impl.node.query.aggregation;

import java.util.Iterator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.aggregation.Aggregation;
import com.enonic.xp.aggregation.Bucket;
import com.enonic.xp.aggregation.BucketAggregation;
import com.enonic.xp.aggregation.SingleValueMetricAggregation;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.query.aggregation.TermsAggregationQuery;
import com.enonic.xp.query.aggregation.metric.ValueCountAggregationQuery;
import com.enonic.xp.repo.impl.node.AbstractNodeTest;

import static org.junit.jupiter.api.Assertions.*;

public class ValueCountAggregationTest
    extends AbstractNodeTest
{
    @BeforeEach
    public void setUp()
        throws Exception
    {
        this.createDefaultRootNode();
    }

    @Test
    public void terms_stats_aggregation()
        throws Exception
    {
        createNode( "c1", "n1", NodePath.ROOT );
        createNode( "c1", "n2", NodePath.ROOT );
        createNode( "c1", "n3", NodePath.ROOT );
        createNode( "c1", "n4", NodePath.ROOT );

        createNode( "c2", "n5", NodePath.ROOT );
        createNode( "c2", "n6", NodePath.ROOT );

        createNode( "c3", "n7", NodePath.ROOT );

        final NodeQuery query = NodeQuery.create().
            addAggregationQuery( TermsAggregationQuery.create( "category" ).
                fieldName( "category" ).
                orderDirection( TermsAggregationQuery.Direction.ASC ).
                orderType( TermsAggregationQuery.Type.TERM ).
                addSubQuery( ValueCountAggregationQuery.create( "subquery" ).
                    fieldName( "category" ).
                    build() ).
                build() ).
            build();

        FindNodesByQueryResult result = doFindByQuery( query );

        assertEquals( 1, result.getAggregations().getSize() );

        final Aggregation agg = result.getAggregations().get( "category" );
        assertTrue( agg instanceof BucketAggregation );
        final BucketAggregation categoryAgg = (BucketAggregation) agg;

        final Iterator<Bucket> bucketIterator = categoryAgg.getBuckets().iterator();

        verifyValueCount( bucketIterator.next(), "c1", 4d );
        verifyValueCount( bucketIterator.next(), "c2", 2d );
        verifyValueCount( bucketIterator.next(), "c3", 1d );
    }

    private void verifyValueCount( final Bucket parentBucket, String parentBucketKey, double value )
    {
        assertEquals( parentBucketKey, parentBucket.getKey(), "Wrong parent bucket key" );

        assertEquals( 1, parentBucket.getSubAggregations().getSize() );
        final Aggregation subAgg = parentBucket.getSubAggregations().get( "subquery" );
        assertTrue( subAgg instanceof SingleValueMetricAggregation );
        final SingleValueMetricAggregation stats = (SingleValueMetricAggregation) subAgg;
        assertEquals( value, stats.getValue(), 0 );
    }

    private Node createNode( final String categoryValue, final String name, final NodePath parent )
    {
        final PropertyTree data = new PropertyTree();
        data.addString( "category", categoryValue );

        return createNode( CreateNodeParams.create().
            parent( parent ).
            name( name ).
            data( data ).
            build() );

    }

}



