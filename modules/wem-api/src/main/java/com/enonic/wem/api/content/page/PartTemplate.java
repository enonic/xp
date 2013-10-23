package com.enonic.wem.api.content.page;


import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.module.ModuleResourceKey;

public final class PartTemplate
    extends Template<PartTemplateId>
{
    /**
     * Template templateConfig.
     */
    private RootDataSet templateConfig;

    /**
     * Default part templateConfig that can be overridden in part (content).
     */
    private RootDataSet partConfig;

    private PartTemplate( final Builder builder )
    {
        super( builder.id, builder.displayName, builder.descriptor );
    }

    public RootDataSet getTemplateConfig()
    {
        return templateConfig;
    }

    public RootDataSet getPartConfig()
    {
        return partConfig;
    }

    public static Builder newPartTemplate()
    {
        return new Builder();
    }

    public static class Builder
    {
        private PartTemplateId id;

        private String displayName;

        private ModuleResourceKey descriptor;

        private Builder()
        {
        }

        public Builder id( final PartTemplateId value )
        {
            this.id = value;
            return this;
        }

        public Builder displayName( final String displayName )
        {
            this.displayName = displayName;
            return this;
        }

        public Builder descriptor( final ModuleResourceKey descriptor )
        {
            this.descriptor = descriptor;
            return this;
        }


        public PartTemplate build()
        {
            return new PartTemplate( this );
        }
    }
}
