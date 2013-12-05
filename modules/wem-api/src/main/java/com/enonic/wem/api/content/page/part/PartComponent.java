package com.enonic.wem.api.content.page.part;


import com.enonic.wem.api.content.page.BasePageComponent;
import com.enonic.wem.api.content.page.image.ImageTemplateKey;
import com.enonic.wem.api.content.page.region.RegionPlaceableComponent;
import com.enonic.wem.api.data.DataSet;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;

public final class PartComponent
    extends BasePageComponent<PartTemplateKey>
    implements RegionPlaceableComponent
{
    private final RootDataSet config;

    public PartComponent( final Builder builder )
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

    public static Builder newPartComponent()
    {
        return new Builder();
    }

    public static class Builder
        extends BasePageComponent.Builder<PartTemplateKey>
    {
        private RootDataSet config;

        private Builder()
        {
            this.config = RootDataSet.newDataSet().build().toRootDataSet();
        }

        public Builder from( final DataSet dataSet )
        {
            final Builder builder = new Builder();
            builder.template( PartTemplateKey.from( dataSet.getProperty( "template" ).getString() ) );
            builder.config( dataSet.getProperty( "config" ).getData() );
            return builder;
        }

        public Builder template( PartTemplateKey value )
        {
            this.template = value;
            return this;
        }

        public Builder config( final RootDataSet config )
        {
            this.config = config;
            return this;
        }

        public PartComponent build()
        {
            return new PartComponent( this );
        }
    }
}
