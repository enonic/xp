package com.enonic.xp.core.aggregation;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import com.enonic.xp.core.support.AbstractImmutableEntitySet;

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

    public static Aggregations from( final Iterable<Aggregation> aggregations )
    {
        return from( ImmutableSet.copyOf( aggregations ) );
    }

    public static Aggregations from( final Aggregation... aggregations )
    {
        return from( ImmutableSet.copyOf( aggregations ) );
    }

    public Aggregation get( final String name )
    {
        return this.stream().
            filter( ( agg ) -> name.equals( agg.getName() ) ).
            findFirst().orElse( null );
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
