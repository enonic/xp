package com.enonic.wem.api.content.page;


import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.api.schema.content.QualifiedContentTypeNames;

public final class PageTemplate
    extends Template<PageTemplateId>
{
    private final RootDataSet config;

    private final QualifiedContentTypeNames canRender;

    private PageTemplate( final Builder builder )
    {
        super( builder.id, builder.displayName, builder.descriptor );
        this.canRender  =builder.canRender;
        this.config = null;
    }

    public RootDataSet getConfig()
    {
        return config;
    }

    public QualifiedContentTypeNames getCanRender()
    {
        return canRender;
    }

    public static PageTemplate.Builder newPageTemplate()
    {
        return new Builder();
    }

    public static class Builder
    {
        private PageTemplateId id;

        private String displayName;

        private ModuleResourceKey descriptor;

        private QualifiedContentTypeNames canRender;

        private Builder()
        {
        }

        public Builder id( final PageTemplateId value )
        {
            this.id = value;
            return this;
        }

        public Builder displayName( final String displayName )
        {
            this.displayName = displayName; return this;
        }

        public Builder descriptor( final ModuleResourceKey descriptor )
        {
            this.descriptor = descriptor; return this;
        }

        public Builder canRender( final QualifiedContentTypeNames canRender )
        {
            this.canRender = canRender; return this;
        }

        public PageTemplate build()
        {
            return new PageTemplate( this );
        }
    }
}
