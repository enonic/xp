package com.enonic.wem.api.content.page.image;


import com.enonic.wem.api.content.page.BasePageComponent;
import com.enonic.wem.api.content.page.region.RegionPlaceableComponent;
import com.enonic.wem.api.data.RootDataSet;

public class ImageComponent
    extends BasePageComponent<ImageTemplateName>
    implements RegionPlaceableComponent
{
    private final RootDataSet config;

    public ImageComponent( final Builder builder )
    {
        super( builder.imageTemplateName );
        this.config = builder.config;
    }

    public RootDataSet getConfig()
    {
        return config;
    }

    public static Builder newImage()
    {
        return new Builder();
    }

    public static class Builder
    {
        private RootDataSet config;

        private ImageTemplateName imageTemplateName;

        private Builder()
        {
            this.config = RootDataSet.newDataSet().build().toRootDataSet();
        }

        public Builder config( final RootDataSet config )
        {
            this.config = config;
            return this;
        }

        public Builder imageTemplateName( final ImageTemplateName imageTemplateName )
        {
            this.imageTemplateName = imageTemplateName;
            return this;
        }

        public ImageComponent build()
        {
            return new ImageComponent( this );
        }
    }

}
