package com.enonic.wem.api.content.page;


public final class PartTemplate
    extends Template<PartTemplateId, PartTemplateName>
{
    private PartTemplate( final Builder builder )
    {
        super( builder.name, builder.id, builder.displayName, builder.descriptor, builder.config );
    }

    public static Builder newPartTemplate()
    {
        return new Builder();
    }

    public static class Builder
        extends BaseTemplateBuilder<Builder, PartTemplateId, PartTemplateName>
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
