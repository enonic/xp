package com.enonic.wem.api.content.page;

import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.module.ModuleResourceKey;

public abstract class BaseDescriptor
    implements ComponentDescriptor
{
    private final ComponentDescriptorName name;

    private final String displayName;

    private final ModuleResourceKey controllerResource;

    private final Form config;

    protected BaseDescriptor( final BaseDescriptorBuilder builder )
    {
        this.name = builder.name;
        this.displayName = builder.displayName;
        this.controllerResource = builder.controllerResource;
        this.config = builder.config != null ? builder.config : Form.newForm().build();
    }

    @Override
    public ComponentDescriptorName getName()
    {
        return name;
    }

    @Override
    public String getDisplayName()
    {
        return displayName;
    }

    @Override
    public ModuleResourceKey getControllerResource()
    {
        return controllerResource;
    }

    @Override
    public Form getConfig()
    {
        return config;
    }

    public abstract static class BaseDescriptorBuilder<T extends BaseDescriptorBuilder>
    {
        protected ComponentDescriptorName name;

        protected String displayName;

        protected ModuleResourceKey controllerResource;

        protected Form config;

        public T name( final ComponentDescriptorName name )
        {
            this.name = name;
            return (T) this;
        }

        public T name( final String name )
        {
            return this.name( new ComponentDescriptorName( name ) );
        }

        public T displayName( final String displayName )
        {
            this.displayName = displayName;
            return (T) this;
        }

        public T controllerResource( final ModuleResourceKey controllerResource )
        {
            this.controllerResource = controllerResource;
            return (T) this;
        }

        public T config( final Form value )
        {
            this.config = value;
            return (T) this;
        }

    }
}
