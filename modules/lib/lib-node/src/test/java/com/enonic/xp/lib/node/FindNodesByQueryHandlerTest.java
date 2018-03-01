package com.enonic.xp.lib.node;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.aggregation.Aggregation;
import com.enonic.xp.aggregation.Aggregations;
import com.enonic.xp.aggregation.Bucket;
import com.enonic.xp.aggregation.BucketAggregation;
import com.enonic.xp.aggregation.Buckets;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.NodeHit;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeQuery;

public class FindNodesByQueryHandlerTest
    extends BaseNodeHandlerTest
{
    @Test
    public void testExample()
    {
        final BucketAggregation duration = Aggregation.bucketAggregation( "duration" ).
            buckets( Buckets.create().
                add( Bucket.create().
                    key( "1600" ).
                    docCount( 2 ).
                    build() ).
                add( Bucket.create().
                    key( "1400" ).
                    docCount( 1 ).
                    build() ).
                add( Bucket.create().
                    key( "1300" ).
                    docCount( 5 ).
                    build() ).
                build() ).
            build();

        final Aggregations agg = Aggregations.create().
            add( Aggregation.bucketAggregation( "urls" ).
                buckets( Buckets.create().
                    add( Bucket.create().
                        key( "/portal/draft/superhero/search" ).
                        docCount( 6762L ).
                        addAggregations( Aggregations.from( duration ) ).
                        build() ).
                    add( Bucket.create().
                        key( "/portal/draft/superhero" ).
                        docCount( 1245 ).
                        addAggregations( Aggregations.from( duration ) ).
                        build() ).
                    build() ).
                build() ).
            build();

        Mockito.when( this.nodeService.findByQuery( Mockito.isA( NodeQuery.class ) ) ).
            thenReturn( FindNodesByQueryResult.create().
                totalHits( 12902 ).
                addNodeHit( NodeHit.create().
                    nodeId( NodeId.from( "b186d24f-ac38-42ca-a6db-1c1bda6c6c26" ) ).
                    score( 1.23f ).
                    build() ).
                addNodeHit( NodeHit.create().
                    nodeId( NodeId.from( "350ba4a6-589c-498b-8af0-f183850e1120" ) ).
                    score( 1.40f ).
                    build() ).
                aggregations( agg ).
                build() );

        runScript( "/site/lib/xp/examples/node/query.js" );
    }

    @Test
    public void testExample2()
    {
        Mockito.when( this.nodeService.findByQuery( Mockito.isA( NodeQuery.class ) ) ).
            thenReturn( FindNodesByQueryResult.create().
                totalHits( 12902 ).
                addNodeHit( NodeHit.create().
                    nodeId( NodeId.from( "b186d24f-ac38-42ca-a6db-1c1bda6c6c26" ) ).
                    score( 1.23f ).
                    build() ).
                addNodeHit( NodeHit.create().
                    nodeId( NodeId.from( "350ba4a6-589c-498b-8af0-f183850e1120" ) ).
                    score( 1.40f ).
                    build() ).
                build() );

        runScript( "/site/lib/xp/examples/node/query-filter-array-on-root.js" );
    }

}