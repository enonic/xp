package com.enonic.xp.query.filter;

import com.google.common.base.MoreObjects;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.data.Value;

@PublicApi
public final class RangeFilter
    extends FieldFilter
{
    private final Value from;

    private final Value to;

    private final boolean includeLower;

    private final boolean includeUpper;

    public RangeFilter( final Builder builder )
    {
        super( builder );
        this.from = builder.from;
        this.to = builder.to;
        this.includeLower = builder.includeLower;
        this.includeUpper = builder.includeUpper;
    }

    public Value getFrom()
    {
        return from;
    }

    public Value getTo()
    {
        return to;
    }

    public boolean isIncludeLower()
    {
        return includeLower;
    }

    public boolean isIncludeUpper()
    {
        return includeUpper;
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper( this ).
            omitNullValues().
            add( "fieldName", fieldName ).
            add( "from", from ).
            add( "to", to ).
            add( "includeLower", includeLower ).
            add( "includeUpper", includeUpper ).
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

        private boolean includeLower;

        private boolean includeUpper;

        public Builder from( final Value from )
        {
            this.from = from;
            this.includeLower = true;
            return this;
        }

        public Builder to( final Value to )
        {
            this.to = to;
            this.includeUpper = true;
            return this;
        }

        public Builder gt( final Value from )
        {
            this.from = from;
            this.includeLower = false;
            return this;
        }

        public Builder lt( final Value to )
        {
            this.to = to;
            this.includeUpper = false;
            return this;
        }

        public RangeFilter build()
        {
            return new RangeFilter( this );
        }
    }


}
