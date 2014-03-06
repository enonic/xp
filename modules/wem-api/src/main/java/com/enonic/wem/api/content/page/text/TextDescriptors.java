package com.enonic.wem.api.content.page.text;

import java.util.Collection;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import com.enonic.wem.api.content.page.ComponentDescriptorName;
import com.enonic.wem.api.support.AbstractImmutableEntityList;

public class TextDescriptors
    extends AbstractImmutableEntityList<TextDescriptor>
{
    private final ImmutableMap<ComponentDescriptorName, TextDescriptor> descriptorsByName;

    private final ImmutableMap<TextDescriptorKey, TextDescriptor> descriptorsByKey;

    private TextDescriptors( final ImmutableList<TextDescriptor> list )
    {
        super( list );
        this.descriptorsByName = Maps.uniqueIndex( list, new ToNameFunction() );
        this.descriptorsByKey = Maps.uniqueIndex( list, new ToKeyFunction() );
    }

    public TextDescriptor getDescriptor( final TextDescriptorKey key )
    {
        return this.descriptorsByKey.get( key );
    }

    public TextDescriptor getDescriptor( final ComponentDescriptorName name )
    {
        return this.descriptorsByName.get( name );
    }

    public static TextDescriptors empty()
    {
        final ImmutableList<TextDescriptor> list = ImmutableList.of();
        return new TextDescriptors( list );
    }

    public static TextDescriptors from( final TextDescriptor... descriptors )
    {
        return new TextDescriptors( ImmutableList.copyOf( descriptors ) );
    }

    public static TextDescriptors from( final Iterable<? extends TextDescriptor> descriptors )
    {
        return new TextDescriptors( ImmutableList.copyOf( descriptors ) );
    }

    public static TextDescriptors from( final Collection<? extends TextDescriptor> descriptors )
    {
        return new TextDescriptors( ImmutableList.copyOf( descriptors ) );
    }

    private final static class ToNameFunction
        implements Function<TextDescriptor, ComponentDescriptorName>
    {
        @Override
        public ComponentDescriptorName apply( final TextDescriptor value )
        {
            return value.getName();
        }
    }

    private final static class ToKeyFunction
        implements Function<TextDescriptor, TextDescriptorKey>
    {
        @Override
        public TextDescriptorKey apply( final TextDescriptor value )
        {
            return value.getKey();
        }
    }

    public static Builder newTextDescriptors()
    {
        return new Builder();
    }

    public static class Builder
    {
        private ImmutableList.Builder<TextDescriptor> list = new ImmutableList.Builder<>();

        private Builder()
        {
        }

        public Builder add( TextDescriptor descriptor )
        {
            this.list.add( descriptor );
            return this;
        }

        public TextDescriptors build()
        {
            return new TextDescriptors( this.list.build() );
        }
    }

}
