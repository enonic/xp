package com.enonic.xp.repo.impl.entity.query.aggregation;

import java.util.Iterator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.aggregation.Aggregation;
import com.enonic.xp.aggregation.Bucket;
import com.enonic.xp.aggregation.BucketAggregation;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.query.aggregation.TermsAggregationQuery;
import com.enonic.xp.repo.impl.entity.AbstractNodeTest;

import static org.junit.Assert.*;

public class TermsAggregationsTest
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
    public void order_doccount_desc()
        throws Exception
    {
        create_c1_c2_c3_with_2_3_1_category_hits();

        final NodeQuery query = NodeQuery.create().
            addAggregationQuery( TermsAggregationQuery.create( "category" ).
                fieldName( "category" ).
                orderDirection( TermsAggregationQuery.Direction.DESC ).
                orderType( TermsAggregationQuery.Type.DOC_COUNT ).
                build() ).
            build();

        FindNodesByQueryResult result = doFindByQuery( query );

        assertEquals( 1, result.getAggregations().getSize() );
        final Aggregation agg = result.getAggregations().get( "category" );
        assertTrue( agg instanceof BucketAggregation );
        final BucketAggregation categoryAgg = (BucketAggregation) agg;
        assertEquals( 3, categoryAgg.getBuckets().getSize() );

        final Iterator<Bucket> iterator = categoryAgg.getBuckets().iterator();

        Bucket next = iterator.next();
        assertEquals( "c2", next.getKey() );
        assertEquals( 3, next.getDocCount() );

        next = iterator.next();
        assertEquals( "c1", next.getKey() );
        assertEquals( 2, next.getDocCount() );

        next = iterator.next();
        assertEquals( "c3", next.getKey() );
        assertEquals( 1, next.getDocCount() );
    }

    @Test
    public void order_doccount_asc()
        throws Exception
    {
        create_c1_c2_c3_with_2_3_1_category_hits();

        final NodeQuery query = NodeQuery.create().
            addAggregationQuery( TermsAggregationQuery.create( "category" ).
                fieldName( "category" ).
                orderDirection( TermsAggregationQuery.Direction.ASC ).
                orderType( TermsAggregationQuery.Type.DOC_COUNT ).
                build() ).
            build();

        FindNodesByQueryResult result = doFindByQuery( query );

        assertEquals( 1, result.getAggregations().getSize() );
        final Aggregation agg = result.getAggregations().get( "category" );
        assertTrue( agg instanceof BucketAggregation );
        final BucketAggregation categoryAgg = (BucketAggregation) agg;
        assertEquals( 3, categoryAgg.getBuckets().getSize() );

        final Iterator<Bucket> iterator = categoryAgg.getBuckets().iterator();

        Bucket next = iterator.next();
        assertEquals( "c3", next.getKey() );
        assertEquals( 1, next.getDocCount() );

        next = iterator.next();
        assertEquals( "c1", next.getKey() );
        assertEquals( 2, next.getDocCount() );

        next = iterator.next();
        assertEquals( "c2", next.getKey() );
        assertEquals( 3, next.getDocCount() );
    }


    @Test
    public void size()
        throws Exception
    {
        create_c1_c2_c3_with_2_3_1_category_hits();

        final NodeQuery query = NodeQuery.create().
            addAggregationQuery( TermsAggregationQuery.create( "category" ).
                fieldName( "category" ).
                orderDirection( TermsAggregationQuery.Direction.DESC ).
                orderType( TermsAggregationQuery.Type.DOC_COUNT ).
                size( 2 ).
                build() ).
            build();

        FindNodesByQueryResult result = doFindByQuery( query );

        assertEquals( 1, result.getAggregations().getSize() );
        final Aggregation agg = result.getAggregations().get( "category" );
        assertTrue( agg instanceof BucketAggregation );
        final BucketAggregation categoryAgg = (BucketAggregation) agg;
        assertEquals( 2, categoryAgg.getBuckets().getSize() );

        final Iterator<Bucket> iterator = categoryAgg.getBuckets().iterator();

        Bucket next = iterator.next();
        assertEquals( "c2", next.getKey() );
        assertEquals( 3, next.getDocCount() );

        next = iterator.next();
        assertEquals( "c1", next.getKey() );
        assertEquals( 2, next.getDocCount() );
    }

    @Test
    public void order_by_term()
        throws Exception
    {
        create_c1_c2_c3_with_2_3_1_category_hits();

        final NodeQuery query = NodeQuery.create().
            addAggregationQuery( TermsAggregationQuery.create( "category" ).
                fieldName( "category" ).
                orderDirection( TermsAggregationQuery.Direction.ASC ).
                orderType( TermsAggregationQuery.Type.TERM ).
                build() ).
            build();

        FindNodesByQueryResult result = doFindByQuery( query );

        assertEquals( 1, result.getAggregations().getSize() );
        final Aggregation agg = result.getAggregations().get( "category" );
        assertTrue( agg instanceof BucketAggregation );
        final BucketAggregation categoryAgg = (BucketAggregation) agg;
        assertEquals( 3, categoryAgg.getBuckets().getSize() );

        final Iterator<Bucket> iterator = categoryAgg.getBuckets().iterator();

        Bucket next = iterator.next();
        assertEquals( "c1", next.getKey() );
        assertEquals( 2, next.getDocCount() );

        next = iterator.next();
        assertEquals( "c2", next.getKey() );
        assertEquals( 3, next.getDocCount() );

        next = iterator.next();
        assertEquals( "c3", next.getKey() );
        assertEquals( 1, next.getDocCount() );
    }

    @Test
    public void nested_term_aggregation()
        throws Exception
    {
        createNode( "c1", "d1", "node1", NodePath.ROOT );
        createNode( "c1", "d1", "node2", NodePath.ROOT );
        createNode( "c1", "d2", "node3", NodePath.ROOT );
        createNode( "c1", "d3", "node4", NodePath.ROOT );

        createNode( "c2", "d1", "node5", NodePath.ROOT );
        createNode( "c2", "d2", "node6", NodePath.ROOT );
        createNode( "c2", "d2", "node7", NodePath.ROOT );
        createNode( "c2", "d3", "node8", NodePath.ROOT );

        createNode( "c3", "d1", "node9", NodePath.ROOT );
        createNode( "c3", "d2", "node10", NodePath.ROOT );
        createNode( "c3", "d3", "node11", NodePath.ROOT );
        createNode( "c3", "d3", "node12", NodePath.ROOT );

        final NodeQuery query = NodeQuery.create().
            addAggregationQuery( TermsAggregationQuery.create( "category" ).
                fieldName( "category" ).
                orderDirection( TermsAggregationQuery.Direction.ASC ).
                orderType( TermsAggregationQuery.Type.TERM ).
                addSubQuery( TermsAggregationQuery.create( "subquery" ).
                    fieldName( "other" ).
                    orderDirection( TermsAggregationQuery.Direction.ASC ).
                    orderType( TermsAggregationQuery.Type.TERM ).
                    build() ).
                build() ).
            build();

        FindNodesByQueryResult result = doFindByQuery( query );

        assertEquals( 1, result.getAggregations().getSize() );

        final Aggregation agg = result.getAggregations().get( "category" );
        assertTrue( agg instanceof BucketAggregation );
        final BucketAggregation categoryAgg = (BucketAggregation) agg;

        final Iterator<Bucket> bucketIterator = categoryAgg.getBuckets().iterator();

        // Verify the number of values for each category, e.g in category
        // c1, there are 2 'd1's, 1 'd2' and 1 'd3'
        verifySubAggregation( bucketIterator.next(), "c1", 2, 1, 1 );
        verifySubAggregation( bucketIterator.next(), "c2", 1, 2, 1 );
        verifySubAggregation( bucketIterator.next(), "c3", 1, 1, 2 );
    }

    private void verifySubAggregation( final Bucket parentBucket, String parentBucketKey, int first, int second, int third )
    {
        Assert.assertEquals( "Wrong parent bucket key", parentBucketKey, parentBucket.getKey() );

        assertEquals( 1, parentBucket.getSubAggregations().getSize() );
        final Aggregation subAgg = parentBucket.getSubAggregations().get( "subquery" );
        assertTrue( subAgg instanceof BucketAggregation );
        final BucketAggregation subBucketAgg = (BucketAggregation) subAgg;

        assertEquals( 3, subBucketAgg.getBuckets().getSize() );

        final Iterator<Bucket> subQueryIterator = subBucketAgg.getBuckets().iterator();

        Bucket nextSub = subQueryIterator.next();
        assertEquals( "d1", nextSub.getKey() );
        assertEquals( first, nextSub.getDocCount() );

        nextSub = subQueryIterator.next();
        assertEquals( "d2", nextSub.getKey() );
        assertEquals( second, nextSub.getDocCount() );

        nextSub = subQueryIterator.next();
        assertEquals( "d3", nextSub.getKey() );
        assertEquals( third, nextSub.getDocCount() );
    }


    private void create_c1_c2_c3_with_2_3_1_category_hits()
    {
        createNode( "c1", "n1", NodePath.ROOT );
        createNode( "c1", "n2", NodePath.ROOT );

        createNode( "c2", "n3", NodePath.ROOT );
        createNode( "c2", "n4", NodePath.ROOT );
        createNode( "c2", "n5", NodePath.ROOT );

        createNode( "c3", "n6", NodePath.ROOT );
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

    private Node createNode( final String categoryValue, String otherValue, final String name, final NodePath parent )
    {
        final PropertyTree data = new PropertyTree();
        data.addString( "category", categoryValue );
        data.addString( "other", otherValue );

        return createNode( CreateNodeParams.create().
            parent( parent ).
            name( name ).
            data( data ).
            build() );

    }


}
