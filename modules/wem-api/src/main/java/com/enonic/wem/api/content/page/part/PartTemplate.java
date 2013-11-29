package com.enonic.wem.api.content.page.part;


import com.enonic.wem.api.content.page.Template;

public final class PartTemplate
    extends Template<PartTemplateName>
{
    private PartTemplate( final Builder builder )
    {
        super( builder );
    }

    public static Builder newPartTemplate()
    {
        return new Builder();
    }

    public static class Builder
        extends BaseTemplateBuilder<Builder, PartTemplateName>
    {
        private Builder()
        {
        }

        public PartTemplate build()
        {
            return new PartTemplate( this );
        }
    }
}
