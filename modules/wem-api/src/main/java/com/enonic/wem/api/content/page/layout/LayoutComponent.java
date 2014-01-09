package com.enonic.wem.api.content.page.layout;

import com.enonic.wem.api.content.page.ComponentName;
import com.enonic.wem.api.content.page.PageComponent;
import com.enonic.wem.api.content.page.region.RegionPlaceableComponent;
import com.enonic.wem.api.data.DataSet;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;

public final class LayoutComponent
    extends PageComponent<LayoutTemplateKey>
    implements RegionPlaceableComponent
{
    private ComponentName name;

    private final RootDataSet config;

    public LayoutComponent( final Builder builder )
    {
        super( builder );
        this.name = builder.name;
        this.config = builder.config;
    }

    public ComponentName getName()
    {
        return name;
    }

    public RootDataSet getConfig()
    {
        return config;
    }

    @Override
    public DataSet toDataSet()
    {
        final DataSet componentDataSet = super.toDataSet();
        componentDataSet.setProperty( "name", new Value.String( this.name.toString() ) );
        componentDataSet.setProperty( "config", new Value.Data( this.config ) );
        return componentDataSet;
    }

    public static Builder newLayoutComponent()
    {
        return new Builder();
    }

    public static class Builder
        extends PageComponent.Builder<LayoutTemplateKey>
    {
        private ComponentName name;

        private RootDataSet config;

        private Builder()
        {
            this.config = RootDataSet.newDataSet().build().toRootDataSet();
        }

        public Builder from( final DataSet dataSet )
        {
            final Builder builder = new Builder();
            builder.name( new ComponentName( dataSet.getProperty( "name" ).getString() ) );
            builder.template( LayoutTemplateKey.from( dataSet.getProperty( "template" ).getString() ) );
            builder.config( dataSet.getProperty( "config" ).getData() );
            return builder;
        }

        public Builder name( ComponentName value )
        {
            this.name = value;
            return this;
        }

        public Builder name( String value )
        {
            this.name = new ComponentName(value);
            return this;
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
