package com.enonic.xp.schema.mixin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntityList;

@PublicApi
public final class Mixins
    extends AbstractImmutableEntityList<Mixin>
{
    private final ImmutableMap<MixinName, Mixin> map;

    private Mixins( final ImmutableList<Mixin> list )
    {
        super( list );
        this.map = list.stream().collect( ImmutableMap.toImmutableMap( Mixin::getName, Function.identity() ) );
    }

    public Mixins add( final Mixin... mixins )
    {
        return add( ImmutableList.copyOf( mixins ) );
    }

    public Mixins add( final Iterable<Mixin> mixins )
    {
        return add( ImmutableList.copyOf( mixins ) );
    }

    private Mixins add( final ImmutableList<Mixin> mixins )
    {
        final List<Mixin> tmp = new ArrayList<>();
        tmp.addAll( this.list );
        tmp.addAll( mixins );

        return new Mixins( ImmutableList.copyOf( tmp ) );
    }

    public Set<MixinName> getNames()
    {
        return map.keySet();
    }

    public Mixin getMixin( final MixinName mixinName )
    {
        return map.get( mixinName );
    }

    public Mixins filter( final Predicate<Mixin> filter )
    {
        return from( this.map.values().stream().filter( filter ).iterator() );
    }

    public static Mixins empty()
    {
        return new Mixins( ImmutableList.of() );
    }

    public static Mixins from( final Mixin... mixins )
    {
        return new Mixins( ImmutableList.copyOf( mixins ) );
    }

    public static Mixins from( final Iterable<? extends Mixin> mixins )
    {
        return new Mixins( ImmutableList.copyOf( mixins ) );
    }

    public static Mixins from( final Iterator<? extends Mixin> mixins )
    {
        return new Mixins( ImmutableList.copyOf( mixins ) );
    }

    public static Mixins from( final Stream<? extends Mixin> mixins )
    {
        return new Mixins( ImmutableList.copyOf( mixins.collect( Collectors.toList() ) ) );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private ImmutableList.Builder<Mixin> builder = ImmutableList.builder();

        public Builder add( Mixin node )
        {
            builder.add( node );
            return this;
        }

        public Builder addAll( Mixins nodes )
        {
            builder.addAll( nodes );
            return this;
        }

        public Builder addAll( Collection<Mixin> nodes )
        {
            builder.addAll( nodes );
            return this;
        }

        public Mixins build()
        {
            return new Mixins( builder.build() );
        }
    }
}
