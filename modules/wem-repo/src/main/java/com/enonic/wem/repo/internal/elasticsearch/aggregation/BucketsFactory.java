package com.enonic.wem.repo.internal.elasticsearch.aggregation;

import java.util.Collection;

import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogram;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.range.date.DateRange;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;

import com.enonic.wem.api.aggregation.Bucket;
import com.enonic.wem.api.aggregation.Buckets;
import com.enonic.wem.api.aggregation.DateRangeBucket;

class BucketsFactory
{

    static Buckets createFromTerms( final Collection<Terms.Bucket> buckets )
    {
        final Buckets.Builder bucketsBuilder = new Buckets.Builder();

        for ( final Terms.Bucket bucket : buckets )
        {
            bucketsBuilder.add( new Bucket( bucket.getKey(), bucket.getDocCount() ) );
        }

        return bucketsBuilder.build();
    }


    static Buckets createFromDateRange( final Collection<? extends DateRange.Bucket> buckets )
    {
        final Buckets.Builder bucketsBuilder = new Buckets.Builder();

        for ( final DateRange.Bucket bucket : buckets )
        {
            final DateRangeBucket dateRangeBucket = new DateRangeBucket( bucket.getKey(), bucket.getDocCount() );
            dateRangeBucket.setFrom( bucket.getFromAsDate() != null ? bucket.getFromAsDate().toDate().toInstant() : null );
            dateRangeBucket.setTo( bucket.getToAsDate() != null ? bucket.getToAsDate().toDate().toInstant() : null );
            bucketsBuilder.add( dateRangeBucket );
        }

        return bucketsBuilder.build();
    }

    static Buckets createFromDateHistogram( final Collection<? extends DateHistogram.Bucket> buckets )
    {
        final Buckets.Builder bucketsBuilder = new Buckets.Builder();

        for ( final DateHistogram.Bucket bucket : buckets )
        {
            bucketsBuilder.add( new Bucket( bucket.getKey(), bucket.getDocCount() ) );
        }

        return bucketsBuilder.build();
    }

    static Buckets createFromHistogram( final Collection<? extends Histogram.Bucket> buckets )
    {
        final Buckets.Builder bucketsBuilder = new Buckets.Builder();

        for ( final Histogram.Bucket bucket : buckets )
        {
            bucketsBuilder.add( new Bucket( bucket.getKey(), bucket.getDocCount() ) );
        }

        return bucketsBuilder.build();
    }


}
