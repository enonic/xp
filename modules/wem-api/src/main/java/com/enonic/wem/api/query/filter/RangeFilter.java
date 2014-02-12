package com.enonic.wem.api.query.filter;

import com.enonic.wem.api.data.Value;

public class RangeFilter
    extends FieldFilter
{
    private final Value from;

    private final Value to;

    public RangeFilter( final Builder builder )
    {
        super( builder.fieldName );
        this.from = builder.from;
        this.to = builder.to;
    }

    public Value getFrom()
    {
        return from;
    }

    public Value getTo()
    {
        return to;
    }

    public static class Builder
    {
        private Value from;

        private Value to;

        private String fieldName;

        public Builder( final String fieldName )
        {
            this.fieldName = fieldName;
        }

        public Builder from( final Value from )
        {
            this.from = from;
            return this;
        }

        public Builder to( final Value to )
        {
            this.to = to;
            return this;
        }

        public RangeFilter build()
        {
            return new RangeFilter( this );
        }
    }


}
