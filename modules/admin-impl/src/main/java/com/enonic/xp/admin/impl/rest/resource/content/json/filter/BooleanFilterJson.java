package com.enonic.xp.admin.impl.rest.resource.content.json.filter;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.query.filter.BooleanFilter;

public class BooleanFilterJson
    extends FilterJson
{
    private final BooleanFilter booleanFilter;

    @JsonCreator
    public BooleanFilterJson( @JsonProperty("must") final List<FilterJson> must, //
                              @JsonProperty("mustNot") final List<FilterJson> mustNot, //
                              @JsonProperty("should") final List<FilterJson> should )
    {

        final BooleanFilter.Builder builder = BooleanFilter.create();

        for ( final FilterJson filter : must )
        {
            builder.must( filter.getFilter() );
        }

        for ( final FilterJson filter : mustNot )
        {
            builder.mustNot( filter.getFilter() );
        }

        for ( final FilterJson filter : should )
        {
            builder.should( filter.getFilter() );
        }

        this.booleanFilter = builder.build();
    }

    @Override
    public BooleanFilter getFilter()
    {
        return booleanFilter;
    }
}