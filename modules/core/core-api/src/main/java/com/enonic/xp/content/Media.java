package com.enonic.xp.content;


import org.jspecify.annotations.Nullable;

import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.data.Property;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.image.Cropping;
import com.enonic.xp.image.FocalPoint;
import com.enonic.xp.media.ImageOrientation;
import com.enonic.xp.schema.content.ContentTypeName;


public final class Media
    extends Content
{
    private Media( final Builder builder )
    {
        super( builder );
    }

    public boolean isImage()
    {
        return getType().isImageMedia() || getType().isVectorMedia();
    }

    public Attachment getMediaAttachment()
    {
        final PropertySet mediaData = mediaData();
        if ( mediaData == null )
        {
            return null;
        }

        final String mediaAttachmentName = mediaData.getString( ContentPropertyNames.MEDIA_ATTACHMENT );
        if ( mediaAttachmentName == null )
        {
            return null;
        }

        return getAttachments().byName( mediaAttachmentName );
    }

    public @Nullable ImageOrientation getOrientation()
    {
        final PropertySet mediaData = mediaData();
        if ( mediaData == null )
        {
            return null;
        }

        final String orientationValue = mediaData.getString( ContentPropertyNames.ORIENTATION );
        if ( !ImageOrientation.isValid( orientationValue ) )
        {
            return null;
        }

        return ImageOrientation.from( orientationValue );
    }

    public @Nullable FocalPoint getFocalPoint()
    {
        final PropertySet mediaData = mediaData();
        if ( mediaData == null )
        {
            return null;
        }

        final PropertySet focalPointData = mediaData.getSet( ContentPropertyNames.MEDIA_FOCAL_POINT );
        if ( focalPointData == null )
        {
            return null;
        }

        final Double focalX = focalPointData.getDouble( ContentPropertyNames.MEDIA_FOCAL_POINT_X );
        final Double focalY = focalPointData.getDouble( ContentPropertyNames.MEDIA_FOCAL_POINT_Y );
        if ( focalX == null || focalY == null )
        {
            return null;
        }

        return new FocalPoint( focalX, focalY );
    }

    public @Nullable Cropping getCropping()
    {
        final PropertySet mediaData = mediaData();
        if ( mediaData == null )
        {
            return null;
        }

        final PropertySet croppingData = mediaData.getSet( ContentPropertyNames.MEDIA_CROPPING );
        if ( croppingData == null )
        {
            return null;
        }

        final Double top = croppingData.getDouble( ContentPropertyNames.MEDIA_CROPPING_TOP );
        final Double left = croppingData.getDouble( ContentPropertyNames.MEDIA_CROPPING_LEFT );
        final Double bottom = croppingData.getDouble( ContentPropertyNames.MEDIA_CROPPING_BOTTOM );
        final Double right = croppingData.getDouble( ContentPropertyNames.MEDIA_CROPPING_RIGHT );
        final Double zoom = croppingData.getDouble( ContentPropertyNames.MEDIA_CROPPING_ZOOM );
        if ( left == null || top == null || bottom == null || right == null )
        {
            return null;
        }

        return Cropping.create().zoom( zoom ).top( top / zoom ).left( left / zoom ).bottom( bottom / zoom ).right( right / zoom ).build();
    }

    public Attachment getSourceAttachment()
    {
        return getAttachments().byLabel( "source" );
    }

    private @Nullable PropertySet mediaData()
    {
        final Property mediaProperty = getData().getProperty( ContentPropertyNames.MEDIA );
        if ( mediaProperty == null || !mediaProperty.getType().equals( ValueTypes.PROPERTY_SET ) )
        {
            return null;
        }
        return mediaProperty.getSet();
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

        return super.equals( o );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final Media source )
    {
        return new Builder( source );
    }

    public static final class Builder
        extends Content.Builder<Builder>
    {

        public Builder( final Media source )
        {
            super( source );
        }

        public Builder()
        {
            super();
            this.type = ContentTypeName.unknownMedia();
        }

        @Override
        public Media build()
        {
            return new Media( this );
        }
    }
}
