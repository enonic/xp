package com.enonic.wem.api.content.page;


import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.api.schema.content.QualifiedContentTypeNames;

public class PageTemplate
{
    PageTemplateId id;

    ModuleResourceKey descriptor;

    String displayName;

    RootDataSet liveEdit;

    RootDataSet config;

    QualifiedContentTypeNames canRender;

    RootDataSet page;

    private PageTemplate( final Builder builder )
    {
        this.id = builder.id;
    }

    PageTemplateId id()
    {
        return id;
    }

    public static class Builder
    {
        private PageTemplateId id;

        public Builder id( final PageTemplateId value )
        {
            this.id = value;
            return this;
        }
    }
}
