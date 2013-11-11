package com.enonic.wem.api.content.page;

import com.enonic.wem.api.content.page.region.RegionPlaceableComponent;
import com.enonic.wem.api.data.RootDataSet;

public final class Layout
    extends Component<LayoutTemplateName>
    implements RegionPlaceableComponent
{
    private final RootDataSet config;

    public Layout( final Builder builder )
    {
        super( builder.layoutTemplateName );
        this.config = builder.config;
    }

    public RootDataSet getConfig()
    {
        return config;
    }

    public static Builder newLayout()
    {
        return new Builder();
    }

    public static class Builder
    {
        private RootDataSet config;

        private LayoutTemplateName layoutTemplateName;

        private Builder()
        {
            this.config = RootDataSet.newDataSet().build().toRootDataSet();
        }

        public Builder config( final RootDataSet config )
        {
            this.config = config;
            return this;
        }

        public Builder layoutTemplateName( final LayoutTemplateName layoutTemplateName )
        {
            this.layoutTemplateName = layoutTemplateName;
            return this;
        }

        public Layout build()
        {
            return new Layout( this );
        }
    }
}
