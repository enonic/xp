package com.enonic.xp.query.filter;

import java.util.Collection;
import java.util.Set;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import com.enonic.xp.support.AbstractImmutableEntitySet;

@Beta
public class Filters
    extends AbstractImmutableEntitySet<Filter>
{
    public Filters( final ImmutableSet<Filter> set )
    {
        super( set );
    }

    public static Filters from( final Filter... filter )
    {
        return new Filters( ImmutableSet.copyOf( filter ) );
    }

    public static Filters from( final ImmutableSet<Filter> filters )
    {
        return new Filters( filters );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private Set<Filter> filters = Sets.newLinkedHashSet();

        private Builder()
        {

        }

        public Builder add( final Filter filter )
        {
            this.filters.add( filter );
            return this;
        }

        public Builder addAll( final Collection<Filter> filters )
        {
            this.filters.addAll( filters );
            return this;
        }

        public Filters build()
        {
            return new Filters( ImmutableSet.copyOf( this.filters ) );
        }


    }

}
