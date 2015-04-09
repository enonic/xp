package com.enonic.xp.content.page.region;

import java.util.Collection;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import com.enonic.xp.content.page.DescriptorKey;
import com.enonic.xp.support.AbstractImmutableEntityList;

@Beta
public final class LayoutDescriptors
    extends AbstractImmutableEntityList<LayoutDescriptor>
{
    private final ImmutableMap<String, LayoutDescriptor> descriptorsByName;

    private final ImmutableMap<DescriptorKey, LayoutDescriptor> descriptorsByKey;

    private LayoutDescriptors( final ImmutableList<LayoutDescriptor> list )
    {
        super( list );
        this.descriptorsByName = Maps.uniqueIndex( list, new ToNameFunction() );
        this.descriptorsByKey = Maps.uniqueIndex( list, new ToKeyFunction() );
    }

    public LayoutDescriptor getDescriptor( final DescriptorKey key )
    {
        return this.descriptorsByKey.get( key );
    }

    public LayoutDescriptor getDescriptor( final String name )
    {
        return this.descriptorsByName.get( name );
    }

    public static LayoutDescriptors empty()
    {
        final ImmutableList<LayoutDescriptor> list = ImmutableList.of();
        return new LayoutDescriptors( list );
    }

    public static LayoutDescriptors from( final LayoutDescriptor... descriptors )
    {
        return new LayoutDescriptors( ImmutableList.copyOf( descriptors ) );
    }

    public static LayoutDescriptors from( final Iterable<? extends LayoutDescriptor> descriptors )
    {
        return new LayoutDescriptors( ImmutableList.copyOf( descriptors ) );
    }

    public static LayoutDescriptors from( final Collection<? extends LayoutDescriptor> descriptors )
    {
        return new LayoutDescriptors( ImmutableList.copyOf( descriptors ) );
    }

    private final static class ToNameFunction
        implements Function<LayoutDescriptor, String>
    {
        @Override
        public String apply( final LayoutDescriptor value )
        {
            return value.getName();
        }
    }

    private final static class ToKeyFunction
        implements Function<LayoutDescriptor, DescriptorKey>
    {
        @Override
        public DescriptorKey apply( final LayoutDescriptor value )
        {
            return value.getKey();
        }
    }

    public static Builder newLayoutDescriptors()
    {
        return new Builder();
    }

    public static class Builder
    {
        private ImmutableList.Builder<LayoutDescriptor> list = new ImmutableList.Builder<>();

        private Builder()
        {
        }

        public Builder add( LayoutDescriptor descriptor )
        {
            this.list.add( descriptor );
            return this;
        }

        public LayoutDescriptors build()
        {
            return new LayoutDescriptors( this.list.build() );
        }
    }

}
