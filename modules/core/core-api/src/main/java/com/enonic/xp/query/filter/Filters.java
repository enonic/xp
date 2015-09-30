package com.enonic.xp.query.filter;

import java.util.Collection;
import java.util.List;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import com.enonic.xp.support.AbstractImmutableEntityList;

@Beta
public class Filters
    extends AbstractImmutableEntityList<Filter>
{
    private Filters( final ImmutableList<Filter> set )
    {
        super( set );
    }

    public static Filters from( final Filter... filter )
    {
        return new Filters( ImmutableList.copyOf( filter ) );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private List<Filter> filters = Lists.newLinkedList();

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
            return new Filters( ImmutableList.copyOf( this.filters ) );
        }
    }
}
