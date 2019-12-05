package com.enonic.xp.admin.impl.rest.resource.content.json.filter;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.query.filter.Filter;
import com.enonic.xp.query.filter.RangeFilter;

import static com.google.common.base.Strings.isNullOrEmpty;

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

        if ( !isNullOrEmpty( from ) )
        {
            builder.from( ValueFactory.newDateTime( Instant.parse( from ) ) );
        }

        if ( !isNullOrEmpty( to ) )
        {
            builder.from( ValueFactory.newDateTime( Instant.parse( to ) ) );
        }

        this.rangeFilter = builder.build();
    }

    @Override
    public Filter getFilter()
    {
        return this.rangeFilter;
    }
}
