package com.enonic.wem.api.content.page;


public final class ImageDescriptor
    extends BaseDescriptor
{
    private ImageDescriptor( final Builder builder )
    {
        super( builder.name, builder.displayName, builder.controllerResource, builder.config );
    }

    public static ImageDescriptor.Builder newImageDescriptor()
    {
        return new Builder();
    }

    public static class Builder
        extends BaseDescriptorBuilder<Builder>
    {
        private Builder()
        {
        }

        public ImageDescriptor build()
        {
            return new ImageDescriptor( this );
        }
    }
}
