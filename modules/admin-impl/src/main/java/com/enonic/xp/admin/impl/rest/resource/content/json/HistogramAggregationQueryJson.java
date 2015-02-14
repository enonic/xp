package com.enonic.xp.admin.impl.rest.resource.content.json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Strings;

import com.enonic.xp.core.query.aggregation.AggregationQuery;
import com.enonic.xp.core.query.aggregation.HistogramAggregationQuery;

public class HistogramAggregationQueryJson
    extends AggregationQueryJson
{
    private final HistogramAggregationQuery histogramAggregationQuery;

    @JsonCreator
    public HistogramAggregationQueryJson( @JsonProperty(value = "name", required = true) final String name, //
                                          @JsonProperty(value = "fieldName", required = true) final String fieldName, //
                                          @JsonProperty(value = "interval", required = true) final Integer interval,
                                          @JsonProperty("extendedBoundMin") final Integer extendedBoundMin, //
                                          @JsonProperty("extendedBoundMax") final Integer extendedBoundMax, //
                                          @JsonProperty("minDocCount") final Integer minDocCount,  //
                                          @JsonProperty("order") final String order ) //

    {
        final HistogramAggregationQuery.Builder builder = HistogramAggregationQuery.create( name ).
            fieldName( fieldName ).
            interval( Integer.toUnsignedLong( interval ) );

        if ( minDocCount != null )
        {
            builder.minDocCount( Integer.toUnsignedLong( minDocCount ) );
        }

        if ( extendedBoundMax != null )
        {
            builder.extendedBoundMax( extendedBoundMax );
        }

        if ( extendedBoundMin != null )
        {
            builder.extendedBoundMin( extendedBoundMin );
        }

        if ( Strings.isNullOrEmpty( order ) )
        {
            HistogramAggregationQuery.Order.valueOf( order );
        }

        this.histogramAggregationQuery = builder.build();
    }

    @Override
    public AggregationQuery getAggregationQuery()
    {
        return histogramAggregationQuery;
    }
}