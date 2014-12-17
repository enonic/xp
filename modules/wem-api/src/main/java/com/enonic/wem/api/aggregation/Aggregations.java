package com.enonic.wem.api.aggregation;

import java.util.Iterator;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import com.enonic.wem.api.support.AbstractImmutableEntitySet;

public class Aggregations
    extends AbstractImmutableEntitySet<Aggregation>
{
    private Aggregations( final ImmutableSet<Aggregation> set )
    {
        super( set );
    }

    public static Aggregations empty()
    {
        final ImmutableSet<Aggregation> empty = ImmutableSet.of();
        return new Aggregations( empty );
    }

    public static Aggregations from( final ImmutableSet<Aggregation> aggregations )
    {
        return new Aggregations( aggregations );
    }

    public Aggregation get( final String name )
    {
        final Iterator<Aggregation> aggregations = this.iterator();

        while ( aggregations.hasNext() )
        {
            final Aggregation next = aggregations.next();

            if ( name.equals( next.getName() ) )
            {
                return next;
            }
        }

        return null;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private final Set<Aggregation> aggregations = Sets.newLinkedHashSet();

        public Builder add( final Aggregation aggregation )
        {
            this.aggregations.add( aggregation );
            return this;
        }

        public Aggregations build()
        {
            return new Aggregations( ImmutableSet.copyOf( aggregations ) );
        }
    }

}
