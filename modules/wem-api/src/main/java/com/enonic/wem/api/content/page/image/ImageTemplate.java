package com.enonic.wem.api.content.page.image;


import com.enonic.wem.api.content.page.Template;

public final class ImageTemplate
    extends Template<ImageTemplateName>
{
    private ImageTemplate( final Builder builder )
    {
        super( builder );
    }

    public static Builder newImageTemplate()
    {
        return new Builder();
    }

    public static class Builder
        extends BaseTemplateBuilder<Builder, ImageTemplateName>
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
