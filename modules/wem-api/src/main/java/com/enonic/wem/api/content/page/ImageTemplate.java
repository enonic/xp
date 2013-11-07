package com.enonic.wem.api.content.page;


public final class ImageTemplate
    extends Template<ImageTemplateId, ImageTemplateName>
{
    private ImageTemplate( final Builder builder )
    {
        super( builder.name, builder.id, builder.displayName, builder.descriptor, builder.config );
    }

    public static Builder newImageTemplate()
    {
        return new Builder();
    }

    public static class Builder
        extends BaseTemplateBuilder<Builder, ImageTemplateId, ImageTemplateName>
    {
        private Builder()
        {
        }

        public ImageTemplate build()
        {
            return new ImageTemplate( this );
        }
    }
}
