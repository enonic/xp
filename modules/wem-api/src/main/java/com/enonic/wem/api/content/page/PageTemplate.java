package com.enonic.wem.api.content.page;


import com.enonic.wem.api.schema.content.QualifiedContentTypeNames;

public final class PageTemplate
    extends Template<PageTemplateId>
{
    private final QualifiedContentTypeNames canRender;

    private PageTemplate( final Builder builder )
    {
        super( builder.id, builder.displayName, builder.descriptor, builder.config );
        this.canRender = builder.canRender;
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
        extends BaseTemplateBuilder<Builder, PageTemplateId>
    {
        private QualifiedContentTypeNames canRender;

        private Builder()
        {
        }

        public Builder canRender( final QualifiedContentTypeNames canRender )
        {
            this.canRender = canRender;
            return this;
        }

        public PageTemplate build()
        {
            return new PageTemplate( this );
        }
    }
}
