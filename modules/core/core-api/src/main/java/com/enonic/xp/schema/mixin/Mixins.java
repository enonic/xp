package com.enonic.xp.schema.mixin;

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

    public MixinNames getNames()
    {
        return stream().map( BaseSchema::getName ).collect( MixinNames.collector() );
    }

    public Mixin getMixin( final MixinName mixinName )
    {
        return stream().filter( m -> mixinName.equals( m.getName() ) ).findFirst().orElse( null );
    }

    public static Mixins empty()
    {
        return EMPTY;
    }

    public static Mixins from( final Mixin... mixins )
    {
        return fromInternal( ImmutableList.copyOf( mixins ) );
    }

    public static Mixins from( final Iterable<Mixin> mixins )
    {
        return mixins instanceof Mixins m ? m : fromInternal( ImmutableList.copyOf( mixins ) );
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

        private Builder()
        {
        }

        public Builder add( Mixin node )
        {
            builder.add( node );
            return this;
        }

        public Builder addAll( Iterable<Mixin> nodes )
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
