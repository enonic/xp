package com.enonic.xp.aggregation;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


class BucketAggregationTest
{

    @Test
    void fromString()
    {
        final Buckets buckets = Buckets.create().add( Bucket.create().build() ).build();
        BucketAggregation.Builder builder = new BucketAggregation.Builder( "aaa" );
        builder.buckets( buckets );
        BucketAggregation aggregation = builder.build();

        assertEquals( 1, aggregation.getBuckets().getSize() );
        assertEquals( "aaa", aggregation.getName() );
    }
}
