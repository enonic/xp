package com.enonic.wem.api.content.page;


import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.schema.content.QualifiedContentTypeNames;

public class LayoutTemplate
    extends Template<LayoutTemplate, LayoutTemplateId>
{

    /**
     * Template templateConfig.
     */
    RootDataSet templateConfig;

    /**
     * Default layout templateConfig that can be overridden in layout (content).
     */
    RootDataSet layoutConfig;

    QualifiedContentTypeNames canRender;

    private LayoutTemplate( final Builder builder )
    {

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
