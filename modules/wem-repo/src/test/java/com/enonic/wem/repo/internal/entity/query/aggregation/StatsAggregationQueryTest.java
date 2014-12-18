package com.enonic.wem.repo.internal.entity.query.aggregation;

import java.util.Iterator;

import org.junit.Assert;
import org.junit.Test;

import com.enonic.wem.api.aggregation.Aggregation;
import com.enonic.wem.api.aggregation.Bucket;
import com.enonic.wem.api.aggregation.BucketAggregation;
import com.enonic.wem.api.aggregation.StatsAggregation;
import com.enonic.wem.api.data.PropertyTree;
import com.enonic.wem.api.node.CreateNodeParams;
import com.enonic.wem.api.node.FindNodesByQueryResult;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.node.NodeQuery;
import com.enonic.wem.api.query.aggregation.StatsAggregationQuery;
import com.enonic.wem.api.query.aggregation.TermsAggregationQuery;
import com.enonic.wem.repo.internal.entity.AbstractNodeTest;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class StatsAggregationQueryTest
    extends AbstractNodeTest
{
    @Test
    public void nested_term_aggregation()
        throws Exception
    {
        createNode( "c1", 2d, "n1", NodePath.ROOT );
        createNode( "c1", 4d, "n2", NodePath.ROOT );
        createNode( "c1", 6d, "n3", NodePath.ROOT );
        createNode( "c1", 8d, "n4", NodePath.ROOT );

        createNode( "c2", 2d, "n5", NodePath.ROOT );
        createNode( "c2", 4d, "n6", NodePath.ROOT );

        createNode( "c3", 2d, "n7", NodePath.ROOT );

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
        Assert.assertEquals( "Wrong parent bucket key", parentBucketKey, parentBucket.getKey() );

        assertEquals( 1, parentBucket.getSubAggregations().getSize() );
        final Aggregation subAgg = parentBucket.getSubAggregations().get( "subquery" );
        assertTrue( subAgg instanceof StatsAggregation );
        final StatsAggregation stats = (StatsAggregation) subAgg;

        assertEquals( stats.getAvg(), avg );
        assertEquals( stats.getMin(), min );
        assertEquals( stats.getMax(), max );
        assertEquals( stats.getSum(), sum );
        assertEquals( stats.getCount(), count );
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
