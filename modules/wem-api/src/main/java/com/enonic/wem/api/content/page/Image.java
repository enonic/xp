package com.enonic.wem.api.content.page;


import com.enonic.wem.api.content.page.region.RegionPlaceableComponent;
import com.enonic.wem.api.data.RootDataSet;

public class Image
    extends Component<ImageTemplateId>
    implements RegionPlaceableComponent
{
    private final RootDataSet config;

    public Image( final Builder builder )
    {
        super( builder.imageTemplateId );
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

        private ImageTemplateId imageTemplateId;

        private Builder()
        {
            this.config = RootDataSet.newDataSet().build().toRootDataSet();
        }

        public Builder config( final RootDataSet config )
        {
            this.config = config;
            return this;
        }

        public Builder imageTemplateId( final ImageTemplateId imageTemplateId )
        {
            this.imageTemplateId = imageTemplateId;
            return this;
        }

        public Image build()
        {
            return new Image( this );
        }
    }

}
