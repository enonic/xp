package com.enonic.xp.query.filter;

import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntityList;

@PublicApi
public final class Filters
    extends AbstractImmutableEntityList<Filter>
{
    private static final Filters EMPTY = new Filters( ImmutableList.of() );

    private Filters( final ImmutableList<Filter> set )
    {
        super( set );
    }

    public static Filters empty()
    {
        return EMPTY;
    }

    public static Filters from( final Filter... filters )
    {
        return fromInternal( ImmutableList.copyOf( filters ) );
    }

    public static Collector<Filter, ?, Filters> collector()
    {
        return Collectors.collectingAndThen( ImmutableList.toImmutableList(), Filters::fromInternal );
    }

    private static Filters fromInternal( final ImmutableList<Filter> filters )
    {
        return filters.isEmpty() ? EMPTY : new Filters( filters );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final Filters filters )
    {
        return new Builder().addAll( filters );
    }

    public static final class Builder
    {
        private final ImmutableList.Builder<Filter> filters = ImmutableList.builder();

        private Builder()
        {

        }

        public Builder add( final Filter filter )
        {
            if ( filter != null )
            {
                this.filters.add( filter );
            }

            return this;
        }

        public Builder addAll( final Iterable<? extends Filter> filters )
        {
            this.filters.addAll( filters );
            return this;
        }

        public Filters build()
        {
            return fromInternal( this.filters.build() );
        }
    }
}
