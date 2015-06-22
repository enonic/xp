package com.enonic.xp.lib.content.mapper;

import com.enonic.xp.aggregation.Aggregation;
import com.enonic.xp.aggregation.Aggregations;
import com.enonic.xp.aggregation.Bucket;
import com.enonic.xp.aggregation.BucketAggregation;
import com.enonic.xp.aggregation.Buckets;
import com.enonic.xp.aggregation.DateRangeBucket;
import com.enonic.xp.aggregation.NumericRangeBucket;
import com.enonic.xp.aggregation.StatsAggregation;
import com.enonic.xp.portal.script.serializer.MapGenerator;
import com.enonic.xp.portal.script.serializer.MapSerializable;

public final class AggregationMapper
    implements MapSerializable
{
    private final Aggregations value;

    public AggregationMapper( final Aggregations value )
    {
        this.value = value;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        serializeAggregations( gen, this.value );
    }

    private static void serializeAggregations( final MapGenerator gen, final Aggregations value )
    {
        for ( Aggregation aggregation : value )
        {
            gen.map( aggregation.getName() );
            if ( aggregation instanceof BucketAggregation )
            {
                final Buckets buckets = ( (BucketAggregation) aggregation ).getBuckets();
                serializeBuckets( gen, buckets );
            }
            else if ( aggregation instanceof StatsAggregation )
            {
                final StatsAggregation statsAggregation = ( (StatsAggregation) aggregation );
                serializeStatsAggregation( gen, statsAggregation );
            }
            gen.end();
        }
    }

    private static void serializeBuckets( final MapGenerator gen, final Buckets value )
    {
        gen.array( "buckets" );
        for ( Bucket bucket : value )
        {
            serializeBucket( gen, bucket );
        }
        gen.end();

    }

    private static void serializeBucket( final MapGenerator gen, final Bucket value )
    {
        gen.map();
        gen.value( "key", value.getKey() );
        gen.value( "doc_count", value.getDocCount() );
        if ( value instanceof DateRangeBucket )
        {
            serializeDateBucket( gen, (DateRangeBucket) value );
        }
        else if ( value instanceof NumericRangeBucket )
        {
            serializeNumericBucket( gen, (NumericRangeBucket) value );
        }

        final Aggregations subAggregations = value.getSubAggregations();
        if ( subAggregations != null )
        {
            serializeAggregations( gen, subAggregations );
        }
        gen.end();
    }

    private static void serializeNumericBucket( final MapGenerator gen, final NumericRangeBucket value )
    {
        gen.value( "from", value.getFrom() );
        gen.value( "to", value.getTo() );
    }

    private static void serializeDateBucket( final MapGenerator gen, final DateRangeBucket value )
    {
        gen.value( "from", value.getFrom() );
        gen.value( "to", value.getTo() );
    }

    private static void serializeStatsAggregation( final MapGenerator gen, final StatsAggregation value )
    {
        gen.value( "count", value.getCount() );
        gen.value( "min", value.getMin() );
        gen.value( "max", value.getMax() );
        gen.value( "avg", value.getAvg() );
        gen.value( "sum", value.getSum() );
    }
}
