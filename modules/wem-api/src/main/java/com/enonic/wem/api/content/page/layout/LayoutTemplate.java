package com.enonic.wem.api.content.page.layout;


import com.enonic.wem.api.content.page.Template;

public class LayoutTemplate
    extends Template<LayoutTemplateName>
{
    private LayoutTemplate( final Builder builder )
    {
        super( builder );
    }

    public static LayoutTemplate.Builder newLayoutTemplate()
    {
        return new Builder();
    }

    public static class Builder
        extends BaseTemplateBuilder<Builder, LayoutTemplateName>
    {
        private Builder()
        {
        }

        public LayoutTemplate build()
        {
            return new LayoutTemplate( this );
        }
    }
}
