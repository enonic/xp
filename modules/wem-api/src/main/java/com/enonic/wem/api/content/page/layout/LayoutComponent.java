package com.enonic.wem.api.content.page.layout;

import com.enonic.wem.api.content.page.BasePageComponent;
import com.enonic.wem.api.content.page.region.RegionPlaceableComponent;
import com.enonic.wem.api.data.DataSet;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;

public final class LayoutComponent
    extends BasePageComponent<LayoutTemplateKey>
    implements RegionPlaceableComponent
{
    private final RootDataSet config;

    public LayoutComponent( final Builder builder )
    {
        super( builder );
        this.config = builder.config;
    }

    public RootDataSet getConfig()
    {
        return config;
    }

    @Override
    public DataSet toDataSet()
    {
        final DataSet componentDataSet = super.toDataSet();
        componentDataSet.setProperty( "class", new Value.String( this.getClass().getSimpleName() ) );
        componentDataSet.setProperty( "config", new Value.Data( this.config ) );
        return componentDataSet;
    }

    public static Builder newLayoutComponent()
    {
        return new Builder();
    }

    public static class Builder
        extends BasePageComponent.Builder<LayoutTemplateKey>
    {
        private RootDataSet config;

        private Builder()
        {
            this.config = RootDataSet.newDataSet().build().toRootDataSet();
        }

        public Builder from( final DataSet dataSet )
        {
            final Builder builder = new Builder();
            builder.template( LayoutTemplateKey.from( dataSet.getProperty( "template" ).getString() ) );
            builder.config( dataSet.getProperty( "config" ).getData() );
            return builder;
        }

        public Builder template( LayoutTemplateKey value )
        {
            this.template = value;
            return this;
        }

        public Builder config( final RootDataSet config )
        {
            this.config = config;
            return this;
        }

        public LayoutComponent build()
        {
            return new LayoutComponent( this );
        }
    }
}
