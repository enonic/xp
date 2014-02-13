package com.enonic.wem.api.content.page.image;


import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.page.ComponentName;
import com.enonic.wem.api.content.page.PageComponent;
import com.enonic.wem.api.content.page.region.RegionPlaceableComponent;
import com.enonic.wem.api.data.RootDataSet;

public class ImageComponent
    extends PageComponent<ImageDescriptorKey>
    implements RegionPlaceableComponent
{
    private ContentId image;

    public ImageComponent( final Builder builder )
    {
        super( builder );
        this.image = builder.image;
    }

    @Override
    public Type getType()
    {
        return Type.IMAGE;
    }

    public ContentId getImage()
    {
        return image;
    }

    public static Builder newImageComponent()
    {
        return new Builder();
    }

    public static class Builder
        extends PageComponent.Builder<ImageDescriptorKey>
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
