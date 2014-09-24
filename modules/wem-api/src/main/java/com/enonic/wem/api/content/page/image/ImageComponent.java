package com.enonic.wem.api.content.page.image;


import org.apache.commons.lang.StringUtils;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.page.AbstractPageComponent;
import com.enonic.wem.api.content.page.ComponentName;
import com.enonic.wem.api.content.page.PageComponentType;
import com.enonic.wem.api.content.page.region.RegionPlaceableComponent;
import com.enonic.wem.api.data.RootDataSet;

public class ImageComponent
    extends AbstractPageComponent
    implements RegionPlaceableComponent
{

    private static final String CAPTION = "caption";

    private static final String PHOTOGRAPHER = "photographer";

    private static final String COPYRIGHT = "copyright";

    private final static ImageComponentType type = new ImageComponentType();

    private ContentId image;

    private RootDataSet config;

    public ImageComponent( final Builder builder )
    {
        super( builder );
        this.image = builder.image;
        this.config = builder.config;
    }

    public static Builder newImageComponent()
    {
        return new Builder();
    }

    @Override
    public PageComponentType getType()
    {
        return ImageComponent.type;
    }

    public ContentId getImage()
    {
        return image;
    }

    public boolean hasConfig()
    {
        return config != null;
    }

    public RootDataSet getConfig()
    {
        return this.config;
    }

    public boolean hasCaption()
    {
        return config.hasData( CAPTION ) && StringUtils.isNotBlank( config.getProperty( CAPTION ).getString() );
    }

    /**
     * Returns value of property "caption" in config.
     */
    public String getCaption()
    {
        return config.getProperty( CAPTION ).getString();
    }

    public boolean hasPhotographer()
    {
        return config.hasData( PHOTOGRAPHER ) && StringUtils.isNotBlank( config.getProperty( PHOTOGRAPHER ).getString() );
    }

    /**
     * Returns value of property "photographer" in config.
     */
    public String getPhotographer()
    {
        return config.getProperty( PHOTOGRAPHER ).getString();
    }

    public boolean hasCopyright()
    {
        return config.hasData( COPYRIGHT ) && StringUtils.isNotBlank( config.getProperty( COPYRIGHT ).getString() );
    }

    /**
     * Returns value of property "copyright" in config.
     */
    public String getCopyright()
    {
        return config.getProperty( COPYRIGHT ).getString();
    }

    public static class Builder
        extends AbstractPageComponent.Builder
    {
        private ContentId image;

        private RootDataSet config;

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
