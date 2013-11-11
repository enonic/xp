package com.enonic.wem.api.content.page;


import com.enonic.wem.api.schema.content.ContentTypeNames;

public final class PageTemplate
    extends Template<PageTemplateName>
{
    private final ContentTypeNames canRender;

    private PageTemplate( final Builder builder )
    {
        super( builder.name, builder.displayName, builder.descriptor, builder.config );
        this.canRender = builder.canRender;
    }

    public ContentTypeNames getCanRender()
    {
        return canRender;
    }

    public static PageTemplate.Builder newPageTemplate()
    {
        return new Builder();
    }

    public static class Builder
        extends BaseTemplateBuilder<Builder, PageTemplateName>
    {
        private ContentTypeNames canRender;

        private Builder()
        {
        }

        public Builder canRender( final ContentTypeNames canRender )
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
