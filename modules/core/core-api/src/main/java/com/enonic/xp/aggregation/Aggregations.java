package com.enonic.xp.aggregation;

import java.util.LinkedHashSet;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntitySet;

@PublicApi
public class Aggregations
    extends AbstractImmutableEntitySet<Aggregation>
{
    private static final Aggregations EMPTY = new Aggregations( ImmutableSet.of() );

    private Aggregations( final ImmutableSet<Aggregation> set )
    {
        super( set );
    }

    public static Aggregations empty()
    {
        return EMPTY;
    }

    @Deprecated
    public static Aggregations from( final ImmutableSet<Aggregation> aggregations )
    {
        return fromInternal( aggregations );
    }

    public static Aggregations from( final Iterable<Aggregation> aggregations )
    {
        return fromInternal( ImmutableSet.copyOf( aggregations ) );
    }

    public static Aggregations from( final Aggregation... aggregations )
    {
        return fromInternal( ImmutableSet.copyOf( aggregations ) );
    }

    public Aggregation get( final String name )
    {
        return this.stream().filter( ( agg ) -> name.equals( agg.getName() ) ).findFirst().orElse( null );
    }

    private static Aggregations fromInternal( final ImmutableSet<Aggregation> set )
    {
        return set.isEmpty() ? EMPTY : new Aggregations( set );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private final Set<Aggregation> aggregations = new LinkedHashSet<>();

        public Builder add( final Aggregation aggregation )
        {
            this.aggregations.add( aggregation );
            return this;
        }

        public Aggregations build()
        {
            return Aggregations.fromInternal( ImmutableSet.copyOf( aggregations ) );
        }
    }
}
