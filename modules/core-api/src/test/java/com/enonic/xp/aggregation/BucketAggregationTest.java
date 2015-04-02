package com.enonic.xp.aggregation;


import org.junit.Test;

import static junit.framework.Assert.assertEquals;


public class BucketAggregationTest
{

    @Test
    public void fromString()
    {
        Buckets buckets = Buckets.create().add( Bucket.create().build() ).build();
        BucketAggregation.Builder builder = new BucketAggregation.Builder( "aaa" );
        builder.buckets( buckets );
        BucketAggregation aggregation = builder.build();

        assertEquals( 1, aggregation.getBuckets().getSize() );
        assertEquals( "aaa", aggregation.getName() );
    }
}
