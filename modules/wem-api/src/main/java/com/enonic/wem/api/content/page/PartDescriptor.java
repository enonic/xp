package com.enonic.wem.api.content.page;


import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.module.ModuleResourceKey;

public class PartDescriptor
    implements ComponentDescriptor
{
    private final String displayName;

    private final ModuleResourceKey controllerResource;

    private final Form config;

    private PartDescriptor( final Builder builder )
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

    public static PartDescriptor.Builder newPartDescriptor()
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

        public PartDescriptor build()
        {
            return new PartDescriptor( this );
        }
    }
}
