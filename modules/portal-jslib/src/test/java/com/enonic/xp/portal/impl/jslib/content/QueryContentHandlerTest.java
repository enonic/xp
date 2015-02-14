package com.enonic.xp.portal.impl.jslib.content;

import java.time.Instant;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.core.aggregation.Aggregations;
import com.enonic.xp.core.aggregation.Bucket;
import com.enonic.xp.core.aggregation.BucketAggregation;
import com.enonic.xp.core.aggregation.Buckets;
import com.enonic.xp.core.aggregation.DateRangeBucket;
import com.enonic.xp.core.aggregation.NumericRangeBucket;
import com.enonic.xp.core.aggregation.StatsAggregation;
import com.enonic.xp.core.content.ContentService;
import com.enonic.xp.core.content.Contents;
import com.enonic.xp.core.content.FindContentByQueryParams;
import com.enonic.xp.core.content.FindContentByQueryResult;
import com.enonic.xp.portal.impl.jslib.ContentFixtures;
import com.enonic.xp.portal.script.command.CommandHandler;
import com.enonic.xp.portal.impl.jslib.AbstractHandlerTest;

public class QueryContentHandlerTest
    extends AbstractHandlerTest
{
    private ContentService contentService;

    @Override
    protected CommandHandler createHandler()
        throws Exception
    {
        this.contentService = Mockito.mock( ContentService.class );

        final QueryContentHandler handler = new QueryContentHandler();
        handler.setContentService( this.contentService );

        return handler;
    }

    @Test
    public void query()
        throws Exception
    {
        final Contents contents = ContentFixtures.newContents();

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
        final FindContentByQueryResult findResult = FindContentByQueryResult.create().
            hits( contents.getSize() ).
            totalHits( 20 ).
            contents( contents ).
            aggregations( aggregations ).
            build();
        Mockito.when( this.contentService.find( Mockito.isA( FindContentByQueryParams.class ) ) ).thenReturn( findResult );

        execute( "query" );
    }

    @Test
    public void queryEmpty()
        throws Exception
    {
        final FindContentByQueryResult findResult = FindContentByQueryResult.create().
            hits( 0 ).
            totalHits( 0 ).
            contents( Contents.empty() ).
            aggregations( Aggregations.empty() ).
            build();
        Mockito.when( this.contentService.find( Mockito.isA( FindContentByQueryParams.class ) ) ).thenReturn( findResult );

        execute( "queryEmpty" );
    }
}
