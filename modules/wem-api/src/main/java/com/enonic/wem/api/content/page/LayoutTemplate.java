package com.enonic.wem.api.content.page;


import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.api.schema.content.QualifiedContentTypeNames;

public class LayoutTemplate
    extends Template<LayoutTemplateId>
{

    /**
     * Template templateConfig.
     */
    private RootDataSet templateConfig;

    /**
     * Default layout templateConfig that can be overridden in layout (content).
     */
    private RootDataSet layoutConfig;

    QualifiedContentTypeNames canRender;

    private LayoutTemplate( final Builder builder )
    {
        super( builder.id, builder.displayName, builder.descriptor );
    }

    public static LayoutTemplate.Builder newLayoutTemplate()
    {
        return new Builder();
    }

    public static class Builder
    {
        private LayoutTemplateId id;

        private String displayName;

        private ModuleResourceKey descriptor;

        private Builder()
        {
        }

        public Builder id( final LayoutTemplateId value )
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

        public LayoutTemplate build()
        {
            return new LayoutTemplate( this );
        }
    }
}
