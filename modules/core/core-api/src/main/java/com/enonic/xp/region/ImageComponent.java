package com.enonic.xp.region;

import java.util.Objects;

import com.google.common.annotations.Beta;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.data.PropertyTree;

import static com.google.common.base.Strings.nullToEmpty;

@Beta
public class ImageComponent
    extends Component
{
    private static final String CAPTION = "caption";

    private static final ComponentName NAME = ComponentName.from( "Image" );

    private ContentId image;

    private PropertyTree config;

    public ImageComponent( final Builder builder )
    {
        super( builder );
        this.image = builder.image;
        this.config = builder.config != null ? builder.config : new PropertyTree();
    }

    public static <T extends Builder<T>> Builder<T> create()
    {
        return new Builder();
    }

    public static <T extends Builder<T>> Builder<T> create( final ImageComponent source )
    {
        return new Builder( source );
    }

    @Override
    public ImageComponent copy()
    {
        return create( this ).build();
    }

    @Override
    public ComponentType getType()
    {
        return ImageComponentType.INSTANCE;
    }

    @Override
    public ComponentName getName()
    {
        return NAME;
    }

    public ContentId getImage()
    {
        return image;
    }

    public boolean hasImage()
    {
        return this.image != null;
    }

    public boolean hasConfig()
    {
        return config != null && config.getTotalSize() > 0;
    }

    public PropertyTree getConfig()
    {
        return this.config;
    }

    public boolean hasCaption()
    {
        return config.hasProperty( CAPTION ) && !nullToEmpty( config.getString( CAPTION ) ).isBlank();
    }

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

    public static class Builder<T extends Builder<T>>
        extends Component.Builder<T>
    {
        private ContentId image;

        private PropertyTree config;

        private Builder()
        {
            // Default
        }

        private Builder( final ImageComponent source )
        {
            super( source );
            image = source.image;
            config = source.config != null ? source.config.copy() : null;
        }

        public T image( final ContentId value )
        {
            this.image = value;
            return (T) this;
        }

        public T config( final PropertyTree config )
        {
            this.config = config;
            return (T) this;
        }

        public ImageComponent build()
        {
            return new ImageComponent( this );
        }
    }
}
