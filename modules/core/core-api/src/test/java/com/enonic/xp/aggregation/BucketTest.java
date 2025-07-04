package com.enonic.xp.aggregation;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class BucketTest
{

    @Test
    public void builder()
    {
        Bucket.Builder builder = Bucket.create();

        builder.key( "aaa" );

        builder.docCount( 3 );

        Aggregations aggregations = Aggregations.create().
            add( Aggregation.bucketAggregation( "aaa" ).build() ).
            add( Aggregation.bucketAggregation( "bbb" ).build() ).
            add( Aggregation.bucketAggregation( "ccc" ).build() ).
            build();

        assertEquals( "aaa", aggregations.first().getName() );

        builder.addAggregations( aggregations );

        Bucket bucket = builder.build();

        assertEquals( "aaa", bucket.getKey() );
        assertEquals( 3, bucket.getDocCount() );
        assertEquals( 3, bucket.getSubAggregations().getSize() );
        assertNotNull( bucket.getSubAggregations().get( "aaa" ) );
        assertNotNull( bucket.getSubAggregations().get( "bbb" ) );
        assertNotNull( bucket.getSubAggregations().get( "ccc" ) );
    }
}
