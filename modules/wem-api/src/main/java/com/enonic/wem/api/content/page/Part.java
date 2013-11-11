package com.enonic.wem.api.content.page;


import com.enonic.wem.api.content.page.region.RegionPlaceableComponent;
import com.enonic.wem.api.data.RootDataSet;

public final class Part
    extends Component<PartTemplateName>
    implements RegionPlaceableComponent
{
    private final RootDataSet config;

    public Part( final Builder builder )
    {
        super( builder.partTemplateName );
        this.config = builder.config;
    }

    public RootDataSet getConfig()
    {
        return config;
    }

    public static Builder newPart()
    {
        return new Builder();
    }

    public static class Builder
    {
        private RootDataSet config;

        private PartTemplateName partTemplateName;

        private Builder()
        {
            this.config = RootDataSet.newDataSet().build().toRootDataSet();
        }

        public Builder config( final RootDataSet config )
        {
            this.config = config;
            return this;
        }

        public Builder partTemplateName( final PartTemplateName partTemplateName )
        {
            this.partTemplateName = partTemplateName;
            return this;
        }

        public Part build()
        {
            return new Part( this );
        }
    }
}
