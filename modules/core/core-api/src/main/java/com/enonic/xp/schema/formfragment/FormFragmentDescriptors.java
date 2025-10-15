package com.enonic.xp.schema.formfragment;

import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.schema.BaseSchema;
import com.enonic.xp.support.AbstractImmutableEntityList;

@PublicApi
public final class FormFragmentDescriptors
    extends AbstractImmutableEntityList<FormFragmentDescriptor>
{
    private static final FormFragmentDescriptors EMPTY = new FormFragmentDescriptors( ImmutableList.of() );

    private FormFragmentDescriptors( final ImmutableList<FormFragmentDescriptor> list )
    {
        super( list );
    }

    public FormFragmentNames getNames()
    {
        return stream().map( BaseSchema::getName ).collect( FormFragmentNames.collector() );
    }

    public FormFragmentDescriptor getMixin( final FormFragmentName mixinName )
    {
        return stream().filter( m -> mixinName.equals( m.getName() ) ).findFirst().orElse( null );
    }

    public static FormFragmentDescriptors empty()
    {
        return EMPTY;
    }

    public static FormFragmentDescriptors from( final FormFragmentDescriptor... mixins )
    {
        return fromInternal( ImmutableList.copyOf( mixins ) );
    }

    public static FormFragmentDescriptors from( final Iterable<FormFragmentDescriptor> mixins )
    {
        return mixins instanceof FormFragmentDescriptors m ? m : fromInternal( ImmutableList.copyOf( mixins ) );
    }

    public static Collector <FormFragmentDescriptor, ?, FormFragmentDescriptors> collector()
    {
        return Collectors.collectingAndThen( ImmutableList.toImmutableList(), FormFragmentDescriptors::fromInternal );
    }

    private static FormFragmentDescriptors fromInternal( final ImmutableList<FormFragmentDescriptor> list )
    {
        return list.isEmpty() ? EMPTY : new FormFragmentDescriptors( list );
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

        public FormFragmentDescriptors build()
        {
            return fromInternal( builder.build() );
        }
    }
}
