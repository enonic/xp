package com.enonic.xp.page;

import com.google.common.annotations.Beta;

import com.enonic.xp.schema.content.ContentTypeName;

@Beta
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
