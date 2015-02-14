package com.enonic.xp.admin.impl.rest.resource.content.json.filter;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Strings;

import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.query.filter.Filter;
import com.enonic.wem.api.query.filter.RangeFilter;

public class RangeFilterJson
    extends FilterJson
{
    private final RangeFilter rangeFilter;

    @JsonCreator
    public RangeFilterJson( @JsonProperty("from") final String from, //
                            @JsonProperty("fieldName") final String fieldName, //
                            @JsonProperty("to") final String to )
    {
        final RangeFilter.Builder builder = RangeFilter.create().
            fieldName( fieldName );

        if ( !Strings.isNullOrEmpty( from ) )
        {
            builder.from( Value.newInstant( Instant.parse( from ) ) );
        }

        if ( !Strings.isNullOrEmpty( to ) )
        {
            builder.from( Value.newInstant( Instant.parse( to ) ) );
        }

        this.rangeFilter = builder.build();
    }

    @Override
    public Filter getFilter()
    {
        return this.rangeFilter;
    }
}
