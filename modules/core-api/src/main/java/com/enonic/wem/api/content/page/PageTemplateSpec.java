package com.enonic.wem.api.content.page;

import com.enonic.wem.api.schema.content.ContentTypeName;

public class PageTemplateSpec
{

    private ContentTypeName canRender;

    private PageTemplateSpec( Builder builder ) {
        this.canRender = builder.canRender;
    }

    public boolean isSatisfiedBy(PageTemplate pageTemplate) {
        return pageTemplate.getCanRender().contains( canRender );
    }

    public static Builder newPageTemplateParams() {
        return new Builder();
    }


    public static class Builder {

        private ContentTypeName canRender;

        private Builder() {

        }

        public Builder canRender(ContentTypeName canRender) {
            this.canRender = canRender;
            return this;
        }

        public PageTemplateSpec build() {
            return new PageTemplateSpec( this );
        }

    }
}
