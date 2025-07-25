package com.enonic.xp.schema.mixin;

import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.schema.BaseSchema;
import com.enonic.xp.support.AbstractImmutableEntityList;

@PublicApi
public final class Mixins
    extends AbstractImmutableEntityList<Mixin>
{
    private static final Mixins EMPTY = new Mixins( ImmutableList.of() );

    private Mixins( final ImmutableList<Mixin> list )
    {
        super( list );
    }

    public Mixins add( final Mixin... mixins )
    {
        return add( Arrays.asList( mixins ) );
    }

    public Mixins add( final Iterable<Mixin> mixins )
    {
        return fromInternal( ImmutableList.<Mixin>builder().addAll( this.list ).addAll( mixins ).build() );
    }

    public MixinNames getNames()
    {
        return list.stream().map( BaseSchema::getName ).collect( MixinNames.collector() );
    }

    public Mixin getMixin( final MixinName mixinName )
    {
        return list.stream().filter( m -> mixinName.equals( m.getName() ) ).findFirst().orElse( null );
    }

    public Mixins filter( final Predicate<Mixin> filter )
    {
        return this.list.stream().filter( filter ).collect( collector() );
    }

    public static Mixins empty()
    {
        return EMPTY;
    }

    public static Mixins from( final Mixin... mixins )
    {
        return fromInternal( ImmutableList.copyOf( mixins ) );
    }

    public static Mixins from( final Iterable<? extends Mixin> mixins )
    {
        return fromInternal( ImmutableList.copyOf( mixins ) );
    }

    public static Mixins from( final Iterator<? extends Mixin> mixins )
    {
        return fromInternal( ImmutableList.copyOf( mixins ) );
    }

    public static Collector <Mixin, ?, Mixins> collector()
    {
        return Collectors.collectingAndThen( ImmutableList.toImmutableList(), Mixins::fromInternal );
    }

    private static Mixins fromInternal( final ImmutableList<Mixin> list )
    {
        return list.isEmpty() ? EMPTY : new Mixins( list );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private final ImmutableList.Builder<Mixin> builder = ImmutableList.builder();

        public Builder add( Mixin node )
        {
            builder.add( node );
            return this;
        }

        public Builder addAll( Iterable<? extends Mixin> nodes )
        {
            builder.addAll( nodes );
            return this;
        }

        public Mixins build()
        {
            return fromInternal( builder.build() );
        }
    }
}
