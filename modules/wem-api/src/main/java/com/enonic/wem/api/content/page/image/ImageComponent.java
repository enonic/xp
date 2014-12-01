package com.enonic.wem.api.content.page.image;


import java.util.Objects;

import org.apache.commons.lang.StringUtils;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.page.AbstractPageComponent;
import com.enonic.wem.api.content.page.ComponentName;
import com.enonic.wem.api.content.page.PageComponentType;
import com.enonic.wem.api.content.page.region.RegionPlaceableComponent;
import com.enonic.wem.api.data2.PropertyTree;

public class ImageComponent
    extends AbstractPageComponent
    implements RegionPlaceableComponent
{
    private static final String CAPTION = "caption";

    private ContentId image;

    private PropertyTree config;

    public ImageComponent( final Builder builder )
    {
        super( builder );
        this.image = builder.image;
        this.config = builder.config != null ? builder.config : new PropertyTree();
    }

    public static Builder newImageComponent()
    {
        return new Builder();
    }

    @Override
    public PageComponentType getType()
    {
        return ImageComponentType.INSTANCE;
    }

    public ContentId getImage()
    {
        return image;
    }

    public boolean hasConfig()
    {
        return config != null;
    }

    public PropertyTree getConfig()
    {
        return this.config;
    }

    public boolean hasCaption()
    {
        return config.hasProperty( CAPTION ) && StringUtils.isNotBlank( config.getString( CAPTION ) );
    }

    /**
     * Returns value of property "caption" in config.
     */
    public String getCaption()
    {
        return config.getString( CAPTION );
    }


    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }
        if ( !super.equals( o ) )
        {
            return false;
        }

        final ImageComponent that = (ImageComponent) o;

        return Objects.equals( image, that.image ) && Objects.equals( config, that.config );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( super.hashCode(), image, config );
    }

    public static class Builder
        extends AbstractPageComponent.Builder
    {
        private ContentId image;

        private PropertyTree config;

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

        public Builder config( final PropertyTree config )
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
