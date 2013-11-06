package com.enonic.wem.api.content.page;

import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.module.ModuleResourceKey;

abstract class BaseDescriptor
    implements ComponentDescriptor
{
    private final ComponentDescriptorName name;

    private final String displayName;

    private final ModuleResourceKey controllerResource;

    private final Form config;

    protected BaseDescriptor( final ComponentDescriptorName name, final String displayName, final ModuleResourceKey controllerResource,
                              final Form config )
    {
        this.name = name;
        this.displayName = displayName;
        this.controllerResource = controllerResource;
        this.config = config != null ? config : Form.newForm().build();
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

    protected abstract static class BaseDescriptorBuilder<T extends BaseDescriptorBuilder>
    {
        protected ComponentDescriptorName name;

        protected String displayName;

        protected ModuleResourceKey controllerResource;

        protected Form config;

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
