package com.enonic.wem.api.content.page;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.api.module.ResourcePath;

public abstract class Descriptor<KEY extends DescriptorKey>
{
    private static final ResourcePath COMPONENT_FOLDER = ResourcePath.from( "component" );

    private final KEY key;

    private final ComponentDescriptorName name;

    private final String displayName;

    private final Form config;

    protected Descriptor( final BaseDescriptorBuilder builder )
    {
        Preconditions.checkNotNull( builder.key, "key cannot be null" );
        Preconditions.checkNotNull( builder.name, "name cannot be null" );
        Preconditions.checkNotNull( builder.config, "config cannot be null" );
        this.key = (KEY) builder.key;
        this.name = builder.name;
        this.displayName = builder.displayName;
        this.config = builder.config != null ? builder.config : Form.newForm().build();
    }

    public KEY getKey()
    {
        return key;
    }

    public ComponentDescriptorName getName()
    {
        return name;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public Form getConfigForm()
    {
        return config;
    }

    public ModuleResourceKey getComponentPath()
    {
        final ResourcePath path = COMPONENT_FOLDER.resolve( key.getName().toString() );
        return new ModuleResourceKey( key.getModuleKey(), path );
    }

    public abstract static class BaseDescriptorBuilder<T extends Descriptor.BaseDescriptorBuilder, KEY>
    {
        protected KEY key;

        protected ComponentDescriptorName name;

        protected String displayName;

        protected Form config;

        public T key( final KEY key )
        {
            this.key = key;
            return typecastToBuilder( this );
        }

        public T name( final ComponentDescriptorName name )
        {
            this.name = name;
            return typecastToBuilder( this );
        }

        public T name( final String name )
        {
            return this.name( new ComponentDescriptorName( name ) );
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
