package com.enonic.wem.api.content.page;

import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.module.ModuleResourceKey;

public class LayoutDescriptor
    implements ComponentDescriptor
{
    private String displayName;

    private final ModuleResourceKey controllerResource;

    private Form config;

    private LayoutDescriptor( final Builder builder )
    {
        this.displayName = builder.displayName;
        this.controllerResource = builder.controllerResource;
        this.config = builder.config != null ? builder.config : Form.newForm().build();
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

    public static LayoutDescriptor.Builder newLayoutDescriptor()
    {
        return new Builder();
    }

    public static class Builder
    {
        private String displayName;

        private ModuleResourceKey controllerResource;

        private Form config;

        private Builder()
        {
        }

        public Builder displayName( final String displayName )
        {
            this.displayName = displayName;
            return this;
        }

        public Builder controllerResource( final ModuleResourceKey controllerResource )
        {
            this.controllerResource = controllerResource;
            return this;
        }

        public Builder config( final Form value )
        {
            this.config = value;
            return this;
        }

        public LayoutDescriptor build()
        {
            return new LayoutDescriptor( this );
        }
    }
}
