package com.enonic.xp.repo.impl.search.result;

import java.util.ArrayList;

import com.google.common.collect.Lists;

public class SearchExplanation
{
    private float value;

    private String description;

    private ArrayList<SearchExplanation> details;

    private SearchExplanation( final Builder builder )
    {
        value = builder.value;
        description = builder.description;
        details = builder.details;
    }

    public float getValue()
    {
        return value;
    }

    public String getDescription()
    {
        return description;
    }

    public ArrayList<SearchExplanation> getDetails()
    {
        return details;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private float value;

        private String description;

        private ArrayList<SearchExplanation> details = Lists.newArrayList();

        private Builder()
        {
        }

        public Builder value( final float val )
        {
            value = val;
            return this;
        }

        public Builder description( final String val )
        {
            description = val;
            return this;
        }

        public Builder addDetail( final SearchExplanation val )
        {
            details.add( val );
            return this;
        }

        public SearchExplanation build()
        {
            return new SearchExplanation( this );
        }
    }
}
