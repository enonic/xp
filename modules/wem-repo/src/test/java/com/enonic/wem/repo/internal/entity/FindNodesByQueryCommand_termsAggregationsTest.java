package com.enonic.wem.repo.internal.entity;

import java.util.Iterator;

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
import com.enonic.wem.api.query.aggregation.AggregationQuery;
import com.enonic.wem.api.query.aggregation.TermsAggregationQuery;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.*;

public class FindNodesByQueryCommand_termsAggregationsTest
    extends AbstractNodeTest
{
    @Test
    public void aggregation_terms_asc()
        throws Exception
    {
        create_c1_c2_c3_with_2_3_1_category_hits();

        final NodeQuery query = NodeQuery.create().
            addAggregationQuery( AggregationQuery.newTermsAggregation( "category" ).
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
    public void aggregation_terms_desc()
        throws Exception
    {
        create_c1_c2_c3_with_2_3_1_category_hits();

        final NodeQuery query = NodeQuery.create().
            addAggregationQuery( AggregationQuery.newTermsAggregation( "category" ).
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
    public void aggregation_terms_size()
        throws Exception
    {
        create_c1_c2_c3_with_2_3_1_category_hits();

        final NodeQuery query = NodeQuery.create().
            addAggregationQuery( AggregationQuery.newTermsAggregation( "category" ).
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
    public void aggregation_terms_order_by_term()
        throws Exception
    {
        create_c1_c2_c3_with_2_3_1_category_hits();

        final NodeQuery query = NodeQuery.create().
            addAggregationQuery( AggregationQuery.newTermsAggregation( "category" ).
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


}
