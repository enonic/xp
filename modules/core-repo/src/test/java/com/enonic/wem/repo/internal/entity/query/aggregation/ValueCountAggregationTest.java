package com.enonic.wem.repo.internal.entity.query.aggregation;

import java.util.Iterator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.api.aggregation.Aggregation;
import com.enonic.wem.api.aggregation.Bucket;
import com.enonic.wem.api.aggregation.BucketAggregation;
import com.enonic.wem.api.aggregation.SingleValueMetricAggregation;
import com.enonic.wem.api.data.PropertyTree;
import com.enonic.wem.api.node.CreateNodeParams;
import com.enonic.wem.api.node.FindNodesByQueryResult;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.node.NodeQuery;
import com.enonic.wem.api.query.aggregation.TermsAggregationQuery;
import com.enonic.wem.api.query.aggregation.metric.ValueCountAggregationQuery;
import com.enonic.wem.repo.internal.entity.AbstractNodeTest;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class ValueCountAggregationTest
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
        Assert.assertEquals( "Wrong parent bucket key", parentBucketKey, parentBucket.getKey() );

        assertEquals( 1, parentBucket.getSubAggregations().getSize() );
        final Aggregation subAgg = parentBucket.getSubAggregations().get( "subquery" );
        assertTrue( subAgg instanceof SingleValueMetricAggregation );
        final SingleValueMetricAggregation stats = (SingleValueMetricAggregation) subAgg;
        assertEquals( value, stats.getValue() );
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



