package com.enonic.wem.api.content.page.image;


import java.util.Collection;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import com.enonic.wem.api.content.page.ComponentDescriptorName;
import com.enonic.wem.api.support.AbstractImmutableEntityList;

public final class ImageDescriptors
    extends AbstractImmutableEntityList<ImageDescriptor>
{
    private final ImmutableMap<ComponentDescriptorName, ImageDescriptor> descriptorsByName;

    private final ImmutableMap<ImageDescriptorKey, ImageDescriptor> descriptorsByKey;

    private ImageDescriptors( final ImmutableList<ImageDescriptor> list )
    {
        super( list );
        this.descriptorsByName = Maps.uniqueIndex( list, new ToNameFunction() );
        this.descriptorsByKey = Maps.uniqueIndex( list, new ToKeyFunction() );
    }

    public ImageDescriptor getDescriptor( final ImageDescriptorKey key )
    {
        return this.descriptorsByKey.get( key );
    }

    public ImageDescriptor getDescriptor( final ComponentDescriptorName name )
    {
        return this.descriptorsByName.get( name );
    }

    public static ImageDescriptors empty()
    {
        final ImmutableList<ImageDescriptor> list = ImmutableList.of();
        return new ImageDescriptors( list );
    }

    public static ImageDescriptors from( final ImageDescriptor... descriptors )
    {
        return new ImageDescriptors( ImmutableList.copyOf( descriptors ) );
    }

    public static ImageDescriptors from( final Iterable<? extends ImageDescriptor> descriptors )
    {
        return new ImageDescriptors( ImmutableList.copyOf( descriptors ) );
    }

    public static ImageDescriptors from( final Collection<? extends ImageDescriptor> descriptors )
    {
        return new ImageDescriptors( ImmutableList.copyOf( descriptors ) );
    }

    private final static class ToNameFunction
        implements Function<ImageDescriptor, ComponentDescriptorName>
    {
        @Override
        public ComponentDescriptorName apply( final ImageDescriptor value )
        {
            return value.getName();
        }
    }

    private final static class ToKeyFunction
        implements Function<ImageDescriptor, ImageDescriptorKey>
    {
        @Override
        public ImageDescriptorKey apply( final ImageDescriptor value )
        {
            return value.getKey();
        }
    }

    public static Builder newImageDescriptors()
    {
        return new Builder();
    }

    public static class Builder
    {
        private ImmutableList.Builder<ImageDescriptor> list = new ImmutableList.Builder<>();

        private Builder()
        {
        }

        public Builder add( ImageDescriptor descriptor )
        {
            this.list.add( descriptor );
            return this;
        }

        public ImageDescriptors build()
        {
            return new ImageDescriptors( this.list.build() );
        }
    }

}
