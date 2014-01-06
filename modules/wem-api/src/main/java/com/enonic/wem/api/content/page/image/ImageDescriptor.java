package com.enonic.wem.api.content.page.image;


import com.enonic.wem.api.content.page.Descriptor;

public final class ImageDescriptor
    extends Descriptor<ImageDescriptorKey>
{
    private ImageDescriptor( final Builder builder )
    {
        super( builder );
    }

    public static ImageDescriptor.Builder newImageDescriptor()
    {
        return new Builder();
    }

    public static class Builder
        extends BaseDescriptorBuilder<Builder, ImageDescriptorKey>
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
