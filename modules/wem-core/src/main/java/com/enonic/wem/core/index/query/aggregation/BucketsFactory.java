package com.enonic.wem.core.index.query.aggregation;

import java.util.Collection;

import org.elasticsearch.search.aggregations.bucket.terms.Terms;

import com.enonic.wem.api.query.aggregation.Bucket;
import com.enonic.wem.api.query.aggregation.Buckets;

public class BucketsFactory
{

    public static Buckets create( final Collection<Terms.Bucket> buckets )
    {
        final Buckets.Builder bucketsBuilder = new Buckets.Builder();

        for ( final Terms.Bucket bucket : buckets )
        {
            bucketsBuilder.addBucket( new Bucket( bucket.getKey().toString(), bucket.getDocCount() ) );
        }

        return bucketsBuilder.build();
    }


}
