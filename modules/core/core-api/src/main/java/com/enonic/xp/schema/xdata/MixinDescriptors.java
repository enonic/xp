package com.enonic.xp.schema.xdata;

import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntityList;

@PublicApi
public final class MixinDescriptors
    extends AbstractImmutableEntityList<MixinDescriptor>
{
    private static final MixinDescriptors EMPTY = new MixinDescriptors( ImmutableList.of() );

    private MixinDescriptors( final ImmutableList<MixinDescriptor> list )
    {
        super( list );
    }

    public MixinDescriptor getMixinDescriptor( final MixinName mixinName )
    {
        return stream().filter( mixin -> mixinName.equals( mixin.getName() ) ).findFirst().orElse( null );
    }

    public static MixinDescriptors empty()
    {
        return EMPTY;
    }

    public static MixinDescriptors from( final MixinDescriptor... descriptors )
    {
        return fromInternal( ImmutableList.copyOf( descriptors ) );
    }

    public static MixinDescriptors from( final Iterable<MixinDescriptor> descriptors )
    {
        return descriptors instanceof MixinDescriptors descriptor ? descriptor : fromInternal( ImmutableList.copyOf( descriptors ) );
    }

    public static Collector<MixinDescriptor, ?, MixinDescriptors> collector()
    {
        return Collectors.collectingAndThen( ImmutableList.toImmutableList(), MixinDescriptors::fromInternal );
    }

    private static MixinDescriptors fromInternal( final ImmutableList<MixinDescriptor> list )
    {
        return list.isEmpty() ? EMPTY : new MixinDescriptors( list );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private final ImmutableList.Builder<MixinDescriptor> builder = ImmutableList.builder();

        private Builder()
        {
        }

        public Builder add( MixinDescriptor node )
        {
            builder.add( node );
            return this;
        }

        public Builder addAll( Iterable<MixinDescriptor> nodes )
        {
            builder.addAll( nodes );
            return this;
        }

        public MixinDescriptors build()
        {
            return fromInternal( builder.build() );
        }
    }
}
