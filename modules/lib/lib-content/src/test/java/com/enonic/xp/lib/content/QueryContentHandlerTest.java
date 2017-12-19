package com.enonic.xp.lib.content;

import java.time.Instant;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import com.enonic.xp.aggregation.Aggregations;
import com.enonic.xp.aggregation.Bucket;
import com.enonic.xp.aggregation.BucketAggregation;
import com.enonic.xp.aggregation.Buckets;
import com.enonic.xp.aggregation.DateRangeBucket;
import com.enonic.xp.aggregation.NumericRangeBucket;
import com.enonic.xp.aggregation.StatsAggregation;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentQuery;
import com.enonic.xp.content.Contents;
import com.enonic.xp.content.FindContentIdsByQueryResult;
import com.enonic.xp.content.GetContentByIdsParams;
import com.enonic.xp.resource.ResourceProblemException;

public class QueryContentHandlerTest
    extends BaseContentHandlerTest
{
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testExample()
    {
        setupQuery( 2, true );
        runScript( "/site/lib/xp/examples/content/query.js" );
    }

    @Test
    public void invalidFilter()
    {
        thrown.expect( ResourceProblemException.class );
        thrown.expectMessage( "Filter not of type object" );
        runFunction( "/site/test/QueryContentHandlerTest_invalid_filter.js", "query" );
    }

    @Test
    public void query()
        throws Exception
    {
        setupQuery( 3, true );
        runFunction( "/site/test/QueryContentHandlerTest.js", "query" );
    }

    private void setupQuery( final int count, final boolean aggs )
    {
        final Contents contents = TestDataFixtures.newContents( count );

        final Instant t1 = Instant.parse( "2014-09-01T00:00:00.00Z" );
        final Instant t2 = Instant.parse( "2014-10-01T00:00:00.00Z" );
        final Instant t3 = Instant.parse( "2014-11-01T00:00:00.00Z" );

        final Buckets buckets1 = Buckets.create().
            add( Bucket.create().key( "male" ).docCount( 10 ).build() ).
            add( Bucket.create().key( "female" ).docCount( 12 ).build() ).
            build();
        final Buckets buckets2 = Buckets.create().
            add( Bucket.create().key( "2014-01" ).docCount( 8 ).build() ).
            add( Bucket.create().key( "2014-02" ).docCount( 10 ).build() ).
            add( Bucket.create().key( "2014-03" ).docCount( 12 ).build() ).
            build();
        final Buckets buckets3 = Buckets.create().
            add( NumericRangeBucket.create().key( "a" ).docCount( 2 ).to( 50 ).build() ).
            add( NumericRangeBucket.create().key( "b" ).docCount( 4 ).from( 50 ).to( 100 ).build() ).
            add( NumericRangeBucket.create().key( "c" ).docCount( 4 ).from( 100 ).build() ).
            build();
        final Buckets buckets4 = Buckets.create().
            add( DateRangeBucket.create().from( t1 ).docCount( 2 ).build() ).
            add( DateRangeBucket.create().to( t1 ).from( t2 ).docCount( 5 ).build() ).
            add( DateRangeBucket.create().to( t3 ).docCount( 7 ).build() ).
            build();
        final BucketAggregation aggr1 = BucketAggregation.bucketAggregation( "genders" ).buckets( buckets1 ).build();
        final BucketAggregation aggr2 = BucketAggregation.bucketAggregation( "by_month" ).buckets( buckets2 ).build();
        final BucketAggregation aggr3 = BucketAggregation.bucketAggregation( "price_ranges" ).buckets( buckets3 ).build();
        final BucketAggregation aggr4 = BucketAggregation.bucketAggregation( "my_date_range" ).buckets( buckets4 ).build();
        final StatsAggregation aggr5 = StatsAggregation.create( "item_count" ).avg( 3 ).max( 5 ).min( 1 ).sum( 15 ).count( 5 ).build();

        final Aggregations aggregations = Aggregations.from( aggr1, aggr2, aggr3, aggr4, aggr5 );
        final FindContentIdsByQueryResult findResult = FindContentIdsByQueryResult.create().
            hits( contents.getSize() ).
            totalHits( 20 ).
            contents( contents.getIds() ).
            aggregations( aggs ? aggregations : null ).
            build();
        Mockito.when( this.contentService.find( Mockito.isA( ContentQuery.class ) ) ).thenReturn( findResult );
        Mockito.when( this.contentService.getByIds( Mockito.isA( GetContentByIdsParams.class ) ) ).thenReturn( contents );
    }

    @Test
    public void queryEmpty()
        throws Exception
    {
        final FindContentIdsByQueryResult findResult = FindContentIdsByQueryResult.create().
            hits( 0 ).
            totalHits( 0 ).
            contents( ContentIds.empty() ).
            aggregations( Aggregations.empty() ).
            build();
        Mockito.when( this.contentService.find( Mockito.isA( ContentQuery.class ) ) ).thenReturn( findResult );
        Mockito.when( this.contentService.getByIds( Mockito.isA( GetContentByIdsParams.class ) ) ).thenReturn( Contents.empty() );

        runFunction( "/site/test/QueryContentHandlerTest.js", "queryEmpty" );
    }
}
