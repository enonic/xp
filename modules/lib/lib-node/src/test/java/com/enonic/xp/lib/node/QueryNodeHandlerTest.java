package com.enonic.xp.lib.node;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.aggregation.Aggregation;
import com.enonic.xp.aggregation.Aggregations;
import com.enonic.xp.aggregation.Bucket;
import com.enonic.xp.aggregation.BucketAggregation;
import com.enonic.xp.aggregation.Buckets;
import com.enonic.xp.aggregation.StatsAggregation;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeQuery;

public class QueryNodeHandlerTest
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

        final StatsAggregation durationStats = StatsAggregation.create( "durationStats" ).
            avg( 286.59 ).
            count( 6762.0 ).
            max( 1649.0 ).
            min( 12.0 ).
            sum( 1937941.0 ).
            build();

        final Aggregations agg = Aggregations.create().
            add( Aggregation.bucketAggregation( "urls" ).
                buckets( Buckets.create().
                    add( Bucket.create().
                        key( "/portal/draft/superhero/search" ).
                        docCount( 6762L ).
                        addAggregations( Aggregations.from( duration, durationStats ) ).
                        build() ).
                    add( Bucket.create().
                        key( "/portal/draft/superhero" ).
                        docCount( 1245 ).
                        addAggregations( Aggregations.from( duration, durationStats ) ).
                        build() ).
                    build() ).
                build() ).
            build();

        Mockito.when( this.nodeService.findByQuery( Mockito.isA( NodeQuery.class ) ) ).
            thenReturn( FindNodesByQueryResult.create().
                totalHits( 12902 ).
                addNodeId( NodeId.from( "b186d24f-ac38-42ca-a6db-1c1bda6c6c26" ) ).
                addNodeId( NodeId.from( "350ba4a6-589c-498b-8af0-f183850e1120" ) ).
                aggregations( agg ).
                build() );

        runScript( "/site/lib/xp/examples/node/query.js" );
    }


}