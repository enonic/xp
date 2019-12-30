package com.enonic.xp.query.filter;

import java.util.Collection;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntityList;

@PublicApi
public class Filters
    extends AbstractImmutableEntityList<Filter>
{
    private Filters( final ImmutableList<Filter> set )
    {
        super( set );
    }

    public static Filters empty()
    {
        return new Filters( ImmutableList.of() );
    }

    public static Filters from( final Filter... filters )
    {
        if ( filters == null || filters.length == 0 )
        {
            return empty();
        }

        return new Filters( ImmutableList.copyOf( filters ) );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final Filters filters )
    {
        return new Builder().
            addAll( filters.getList() );
    }


    public static class Builder
    {
        private ImmutableList.Builder<Filter> filters = ImmutableList.builder();

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

        public Builder addAll( final Collection<Filter> filters )
        {
            this.filters.addAll( filters );
            return this;
        }

        public Filters build()
        {
            return new Filters( this.filters.build() );
        }
    }
}
