package com.enonic.wem.api.content.page.part;


import com.enonic.wem.api.content.page.BasePageComponent;
import com.enonic.wem.api.content.page.region.RegionPlaceableComponent;
import com.enonic.wem.api.data.RootDataSet;

public final class PartComponent
    extends BasePageComponent<PartTemplateKey>
    implements RegionPlaceableComponent
{
    private final RootDataSet config;

    public PartComponent( final Builder builder )
    {
        super( builder.partTemplateKey );
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

        private PartTemplateKey partTemplateKey;

        private Builder()
        {
            this.config = RootDataSet.newDataSet().build().toRootDataSet();
        }

        public Builder config( final RootDataSet config )
        {
            this.config = config;
            return this;
        }

        public Builder template( final PartTemplateKey partTemplateKey )
        {
            this.partTemplateKey = partTemplateKey;
            return this;
        }

        public PartComponent build()
        {
            return new PartComponent( this );
        }
    }
}
