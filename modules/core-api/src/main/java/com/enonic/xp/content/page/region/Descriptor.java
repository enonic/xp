package com.enonic.xp.content.page.region;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;

import com.enonic.xp.content.page.DescriptorKey;
import com.enonic.xp.form.Form;
import com.enonic.xp.resource.ResourceKey;

@Beta
public abstract class Descriptor<KEY extends DescriptorKey>
{
    private final KEY key;

    private final String name;

    private final String displayName;

    private final Form config;

    protected Descriptor( final BaseDescriptorBuilder builder )
    {
        Preconditions.checkNotNull( builder.key, "key cannot be null" );
        Preconditions.checkNotNull( builder.name, "name cannot be null" );
        Preconditions.checkNotNull( builder.config, "config cannot be null" );
        //noinspection unchecked
        this.key = (KEY) builder.key;
        this.name = builder.name;
        this.displayName = builder.displayName;
        this.config = builder.config;
    }

    public KEY getKey()
    {
        return key;
    }

    public String getName()
    {
        return name;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public Form getConfig()
    {
        return config;
    }

    public abstract ResourceKey getComponentPath();

    public abstract static class BaseDescriptorBuilder<T extends Descriptor.BaseDescriptorBuilder, KEY extends DescriptorKey>
    {
        protected KEY key;

        protected String name;

        protected String displayName;

        protected Form config;

        protected BaseDescriptorBuilder()
        {
        }

        protected BaseDescriptorBuilder( final Descriptor<KEY> descriptor )
        {
            this.key = descriptor.getKey();
            this.name = descriptor.getName();
            this.displayName = descriptor.getDisplayName();
            this.config = descriptor.getConfig();
        }

        public T key( final KEY key )
        {
            this.key = key;
            return typecastToBuilder( this );
        }

        public T name( final String name )
        {
            this.name = name;
            return typecastToBuilder( this );
        }

        public T displayName( final String displayName )
        {
            this.displayName = displayName;
            return typecastToBuilder( this );
        }

        public T config( final Form value )
        {
            this.config = value;
            return typecastToBuilder( this );
        }

        @SuppressWarnings("unchecked")
        private T typecastToBuilder( final BaseDescriptorBuilder object )
        {
            return (T) object;
        }

    }
}
