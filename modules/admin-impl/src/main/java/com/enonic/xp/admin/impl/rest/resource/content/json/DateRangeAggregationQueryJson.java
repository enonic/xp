package com.enonic.xp.admin.impl.rest.resource.content.json;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.query.aggregation.AggregationQuery;
import com.enonic.xp.query.aggregation.DateRangeAggregationQuery;

public class DateRangeAggregationQueryJson
    extends AggregationQueryJson
{
    private DateRangeAggregationQuery dateRangeAggregationQuery;


    @JsonCreator
    public DateRangeAggregationQueryJson( @JsonProperty("name") final String name, //
                                          @JsonProperty("fieldName") final String fieldName, //
                                          @JsonProperty("ranges") final List<DateRangeJson> dateRanges )
    {
        final DateRangeAggregationQuery.Builder builder = DateRangeAggregationQuery.create( name ).
            fieldName( fieldName );

        for ( final DateRangeJson dateRangeJson : dateRanges )
        {
            builder.addRange( dateRangeJson.getDateRange() );
        }

        this.dateRangeAggregationQuery = builder.build();
    }


    @Override
    public AggregationQuery getAggregationQuery()
    {
        return this.dateRangeAggregationQuery;
    }
}
