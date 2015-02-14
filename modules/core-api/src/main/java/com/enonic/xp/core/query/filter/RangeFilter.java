package com.enonic.xp.core.query.filter;

import com.enonic.xp.core.data.Value;

public class RangeFilter
    extends FieldFilter
{
    private final Value from;

    private final Value to;

    public RangeFilter( final Builder builder )
    {
        super( builder );
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

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
        extends FieldFilter.Builder<Builder>
    {
        private Value from;

        private Value to;

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
