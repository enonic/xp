package com.enonic.xp.schema.mixin;

import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.schema.BaseSchema;
import com.enonic.xp.support.AbstractImmutableEntityList;

@PublicApi
public final class Mixins
    extends AbstractImmutableEntityList<FormFragmentDescriptor>
{
    private static final Mixins EMPTY = new Mixins( ImmutableList.of() );

    private Mixins( final ImmutableList<FormFragmentDescriptor> list )
    {
        super( list );
    }

    public MixinNames getNames()
    {
        return stream().map( BaseSchema::getName ).collect( MixinNames.collector() );
    }

    public FormFragmentDescriptor getMixin( final FormFragmentName mixinName )
    {
        return stream().filter( m -> mixinName.equals( m.getName() ) ).findFirst().orElse( null );
    }

    public static Mixins empty()
    {
        return EMPTY;
    }

    public static Mixins from( final FormFragmentDescriptor... mixins )
    {
        return fromInternal( ImmutableList.copyOf( mixins ) );
    }

    public static Mixins from( final Iterable<FormFragmentDescriptor> mixins )
    {
        return mixins instanceof Mixins m ? m : fromInternal( ImmutableList.copyOf( mixins ) );
    }

    public static Collector <FormFragmentDescriptor, ?, Mixins> collector()
    {
        return Collectors.collectingAndThen( ImmutableList.toImmutableList(), Mixins::fromInternal );
    }

    private static Mixins fromInternal( final ImmutableList<FormFragmentDescriptor> list )
    {
        return list.isEmpty() ? EMPTY : new Mixins( list );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private final ImmutableList.Builder<FormFragmentDescriptor> builder = ImmutableList.builder();

        private Builder()
        {
        }

        public Builder add( FormFragmentDescriptor node )
        {
            builder.add( node );
            return this;
        }

        public Builder addAll( Iterable<FormFragmentDescriptor> nodes )
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
