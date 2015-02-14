package com.enonic.xp.admin.impl.rest.resource.content.json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.core.query.aggregation.DateRange;

public class DateRangeJson
{
    private final DateRange dateRange;

    @JsonCreator
    public DateRangeJson( @JsonProperty("from") final String from, //
                          @JsonProperty("to") final String to, //
                          @JsonProperty("key") final String key )
    {

        this.dateRange = DateRange.create().from( from ).to( to ).key( key ).build();
    }


    public DateRange getDateRange()
    {
        return dateRange;
    }
}
