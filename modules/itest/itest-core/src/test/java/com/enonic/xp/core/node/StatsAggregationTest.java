package com.enonic.xp.core.node;

import java.util.Iterator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.aggregation.Aggregation;
import com.enonic.xp.aggregation.Bucket;
import com.enonic.xp.aggregation.BucketAggregation;
import com.enonic.xp.aggregation.StatsAggregation;
import com.enonic.xp.core.AbstractNodeTest;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.query.aggregation.TermsAggregationQuery;
import com.enonic.xp.query.aggregation.metric.StatsAggregationQuery;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StatsAggregationTest
    extends AbstractNodeTest
{
    @BeforeEach
    void setUp()
    {
        this.createDefaultRootNode();
    }

    @Test
    void terms_stats_aggregation()
    {
        createNode( "c1", 2.0, "n1", NodePath.ROOT );
        createNode( "c1", 4.0, "n2", NodePath.ROOT );
        createNode( "c1", 6.0, "n3", NodePath.ROOT );
        createNode( "c1", 8.0, "n4", NodePath.ROOT );

        createNode( "c2", 2.0, "n5", NodePath.ROOT );
        createNode( "c2", 4.0, "n6", NodePath.ROOT );

        createNode( "c3", 2.0, "n7", NodePath.ROOT );
        refresh();

        final NodeQuery query = NodeQuery.create().
            addAggregationQuery( TermsAggregationQuery.create( "category" ).
                fieldName( "category" ).
                orderDirection( TermsAggregationQuery.Direction.ASC ).
                orderType( TermsAggregationQuery.Type.TERM ).
                addSubQuery( StatsAggregationQuery.create( "subquery" ).
                    fieldName( "other" ).
                    build() ).
                build() ).
            build();

        FindNodesByQueryResult result = doFindByQuery( query );

        assertEquals( 1, result.getAggregations().getSize() );

        final Aggregation agg = result.getAggregations().get( "category" );
        assertTrue( agg instanceof BucketAggregation );
        final BucketAggregation categoryAgg = (BucketAggregation) agg;

        final Iterator<Bucket> bucketIterator = categoryAgg.getBuckets().iterator();

        verifyStatsAggregation( bucketIterator.next(), "c1", 2d, 8d, 5d, 20d, 4d );
        verifyStatsAggregation( bucketIterator.next(), "c2", 2d, 4d, 3d, 6d, 2d );
        verifyStatsAggregation( bucketIterator.next(), "c3", 2d, 2d, 2d, 2d, 1d );
    }

    private void verifyStatsAggregation( final Bucket parentBucket, String parentBucketKey, double min, double max, double avg, double sum,
                                         double count )
    {
        assertEquals( parentBucketKey, parentBucket.getKey(), "Wrong parent bucket key" );

        assertEquals( 1, parentBucket.getSubAggregations().getSize() );
        final Aggregation subAgg = parentBucket.getSubAggregations().get( "subquery" );
        assertTrue( subAgg instanceof StatsAggregation );
        final StatsAggregation stats = (StatsAggregation) subAgg;

        assertEquals( stats.getAvg(), avg, 0 );
        assertEquals( stats.getMin(), min, 0 );
        assertEquals( stats.getMax(), max, 0 );
        assertEquals( stats.getSum(), sum, 0 );
        assertEquals( stats.getCount(), count, 0 );
    }

    private Node createNode( final String categoryValue, final Double otherValue, final String name, final NodePath parent )
    {
        final PropertyTree data = new PropertyTree();
        data.addString( "category", categoryValue );
        data.addDouble( "other", otherValue );

        return createNode( CreateNodeParams.create().
            parent( parent ).
            name( name ).
            data( data ).
            build() );

    }

}
