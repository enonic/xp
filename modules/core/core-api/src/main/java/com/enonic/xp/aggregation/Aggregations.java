package com.enonic.xp.aggregation;

import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.page.PageTemplate;
import com.enonic.xp.page.PageTemplates;
import com.enonic.xp.support.AbstractImmutableEntitySet;

@PublicApi
public final class Aggregations
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

    public static Aggregations from( final Iterable<Aggregation> aggregations )
    {
        return fromInternal( ImmutableSet.copyOf( aggregations ) );
    }

    public static Aggregations from( final Aggregation... aggregations )
    {
        return fromInternal( ImmutableSet.copyOf( aggregations ) );
    }

    public static Collector<Aggregation, ?, Aggregations> collecting()
    {
        return Collectors.collectingAndThen( ImmutableSet.toImmutableSet(), Aggregations::fromInternal );
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
        private final ImmutableSet.Builder<Aggregation> aggregations = ImmutableSet.builder();

        public Builder add( final Aggregation aggregation )
        {
            this.aggregations.add( aggregation );
            return this;
        }

        public Aggregations build()
        {
            return Aggregations.fromInternal( aggregations.build() );
        }
    }
}
