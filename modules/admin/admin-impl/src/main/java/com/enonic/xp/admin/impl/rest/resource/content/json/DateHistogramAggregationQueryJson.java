package com.enonic.xp.admin.impl.rest.resource.content.json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Strings;

import com.enonic.xp.query.aggregation.AggregationQuery;
import com.enonic.xp.query.aggregation.DateHistogramAggregationQuery;

public class DateHistogramAggregationQueryJson
    extends AggregationQueryJson
{

    private final DateHistogramAggregationQuery dateHistogramAggregationQuery;

    @JsonCreator
    public DateHistogramAggregationQueryJson( @JsonProperty(value = "name", required = true) final String name, //
                                              @JsonProperty(value = "fieldName", required = true) final String fieldName, //
                                              @JsonProperty(value = "interval", required = true) final String interval, //
                                              @JsonProperty("format") final String format, //
                                              @JsonProperty("minDocCount") final Integer minDocCount )
    {
        final DateHistogramAggregationQuery.Builder builder = DateHistogramAggregationQuery.create( name ).
            fieldName( fieldName ).
            interval( interval );

        if ( !Strings.isNullOrEmpty( format ) )
        {
            builder.format( format );
        }

        if ( minDocCount != null )
        {
            builder.minDocCount( Integer.toUnsignedLong( minDocCount ) );
        }

        this.dateHistogramAggregationQuery = builder.build();
    }

    @Override
    public AggregationQuery getAggregationQuery()
    {
        return dateHistogramAggregationQuery;
    }
}
