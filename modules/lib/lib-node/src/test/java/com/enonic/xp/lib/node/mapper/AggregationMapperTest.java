package com.enonic.xp.lib.node.mapper;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;

import com.enonic.xp.aggregation.Aggregations;
import com.enonic.xp.aggregation.SingleValueMetricAggregation;
import com.enonic.xp.testing.serializer.JsonMapGenerator;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AggregationMapperTest
{
    @Test
    public void test()
    {
        Aggregations aggregations =
            Aggregations.create().add( SingleValueMetricAggregation.create( "testCountAgg" ).value( 3d ).build() ).build();

        JsonMapGenerator jsonGenerator = new JsonMapGenerator();

        AggregationMapper mapper = new AggregationMapper( aggregations );
        mapper.serialize( jsonGenerator );

        JsonNode actualJson = (JsonNode) jsonGenerator.getRoot();

        JsonNode agg = actualJson.get( "testCountAgg" );

        assertEquals( 3d, agg.get( "value" ).asDouble() );
    }
}
