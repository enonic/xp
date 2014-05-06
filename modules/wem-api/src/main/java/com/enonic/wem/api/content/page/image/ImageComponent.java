package com.enonic.wem.api.content.page.image;


import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.page.AbstractDescriptorBasedPageComponent;
import com.enonic.wem.api.content.page.ComponentName;
import com.enonic.wem.api.content.page.PageComponentType;
import com.enonic.wem.api.content.page.region.RegionPlaceableComponent;
import com.enonic.wem.api.data.RootDataSet;

public class ImageComponent
    extends AbstractDescriptorBasedPageComponent<ImageDescriptorKey>
    implements RegionPlaceableComponent
{
    private final static ImageComponentType type = new ImageComponentType();

    private ContentId image;

    public ImageComponent( final Builder builder )
    {
        super( builder );
        this.image = builder.image;
    }

    public static Builder newImageComponent()
    {
        return new Builder();
    }

    @Override
    public PageComponentType getType()
    {
        return ImageComponent.type;
    }

    public ContentId getImage()
    {
        return image;
    }

    public static class Builder
        extends AbstractDescriptorBasedPageComponent.Builder<ImageDescriptorKey>
    {
        private ContentId image;

        private Builder()
        {

        }

        public Builder image( final ContentId value )
        {
            this.image = value;
            return this;
        }

        public Builder name( ComponentName value )
        {
            this.name = value;
            return this;
        }

        public Builder name( String value )
        {
            this.name = new ComponentName( value );
            return this;
        }

        public Builder descriptor( ImageDescriptorKey value )
        {
            this.descrpitor = value;
            return this;
        }

        public Builder config( final RootDataSet config )
        {
            this.config = config;
            return this;
        }

        public ImageComponent build()
        {
            return new ImageComponent( this );
        }
    }
}
