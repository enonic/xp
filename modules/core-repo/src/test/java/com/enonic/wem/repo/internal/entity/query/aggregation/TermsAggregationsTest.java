package com.enonic.wem.repo.internal.entity.query.aggregation;

import java.util.Iterator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.core.aggregation.Aggregation;
import com.enonic.xp.core.aggregation.Bucket;
import com.enonic.xp.core.aggregation.BucketAggregation;
import com.enonic.xp.core.data.PropertyTree;
import com.enonic.xp.core.node.CreateNodeParams;
import com.enonic.xp.core.node.FindNodesByQueryResult;
import com.enonic.xp.core.node.Node;
import com.enonic.xp.core.node.NodePath;
import com.enonic.xp.core.node.NodeQuery;
import com.enonic.xp.core.query.aggregation.TermsAggregationQuery;
import com.enonic.wem.repo.internal.entity.AbstractNodeTest;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

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
        createNode( "c1", "d1", "n1", NodePath.ROOT );
        createNode( "c1", "d1", "n2", NodePath.ROOT );
        createNode( "c1", "d2", "n3", NodePath.ROOT );
        createNode( "c1", "d3", "n4", NodePath.ROOT );

        createNode( "c2", "d1", "n5", NodePath.ROOT );
        createNode( "c2", "d2", "n6", NodePath.ROOT );

        createNode( "c3", "d1", "n7", NodePath.ROOT );

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

        verifySubAggregation( bucketIterator.next(), "c1", 2, 1, 1 );
        verifySubAggregation( bucketIterator.next(), "c2", 1, 1, 0 );
        verifySubAggregation( bucketIterator.next(), "c3", 1, 0, 0 );
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
