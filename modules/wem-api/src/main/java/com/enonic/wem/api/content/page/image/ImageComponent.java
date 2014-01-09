package com.enonic.wem.api.content.page.image;


import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.page.ComponentName;
import com.enonic.wem.api.content.page.PageComponent;
import com.enonic.wem.api.content.page.region.RegionPlaceableComponent;
import com.enonic.wem.api.data.DataSet;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;

public class ImageComponent
    extends PageComponent<ImageTemplateKey>
    implements RegionPlaceableComponent
{
    private ComponentName name;

    private ContentId image;

    private final RootDataSet config;

    public ImageComponent( final Builder builder )
    {
        super( builder );
        this.name = builder.name;
        this.image = builder.image;
        this.config = builder.config;
    }

    public ComponentName getName()
    {
        return name;
    }

    public ContentId getImage()
    {
        return image;
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
        componentDataSet.setProperty( "image", new Value.ContentId( this.image ) );
        componentDataSet.setProperty( "config", new Value.Data( this.config ) );
        return componentDataSet;
    }

    public static Builder newImageComponent()
    {
        return new Builder();
    }

    public static class Builder
        extends PageComponent.Builder<ImageTemplateKey>
    {
        private ComponentName name;

        private ContentId image;

        private RootDataSet config;

        private Builder()
        {
            this.config = RootDataSet.newDataSet().build().toRootDataSet();
        }

        public Builder from( final DataSet dataSet )
        {
            final Builder builder = new Builder();
            builder.name( new ComponentName( dataSet.getProperty( "name" ).getString() ) );
            builder.template( ImageTemplateKey.from( dataSet.getProperty( "template" ).getString() ) );
            builder.image( ContentId.from( dataSet.getProperty( "image" ).getString() ) );
            builder.config( dataSet.getProperty( "config" ).getData() );
            return builder;
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
            this.name = new ComponentName(value);
            return this;
        }

        public Builder template( ImageTemplateKey value )
        {
            this.template = value;
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
