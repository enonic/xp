package com.enonic.xp.query;

import java.util.ArrayList;

public final class QueryExplanation
{
    private final float value;

    private final String description;

    private final ArrayList<QueryExplanation> details;

    private QueryExplanation( final Builder builder )
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

    public ArrayList<QueryExplanation> getDetails()
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

        private final ArrayList<QueryExplanation> details = new ArrayList<>();

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

        public Builder addDetail( final QueryExplanation val )
        {
            details.add( val );
            return this;
        }

        public QueryExplanation build()
        {
            return new QueryExplanation( this );
        }
    }
}
