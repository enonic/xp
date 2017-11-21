package com.enonic.xp.query.filter;

import com.google.common.annotations.Beta;
import com.google.common.base.MoreObjects;

import com.enonic.xp.data.Value;

@Beta
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

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper( this ).
            omitNullValues().
            add( "fieldName", fieldName ).
            add( "from", from ).
            add( "to", to ).
            toString();
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
