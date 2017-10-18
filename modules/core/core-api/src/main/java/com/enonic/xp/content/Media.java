package com.enonic.xp.content;


import com.google.common.annotations.Beta;

import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.data.Property;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.ValueType;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.image.Cropping;
import com.enonic.xp.image.FocalPoint;
import com.enonic.xp.media.ImageOrientation;
import com.enonic.xp.media.MediaInfo;

@Beta
public class Media
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
        final PropertyTree contentData = getData();
        final Property mediaProperty = contentData.getProperty( ContentPropertyNames.MEDIA );
        if ( mediaProperty == null )
        {
            return null;
        }
        final ValueType mediaPropertyType = mediaProperty.getType();

        final String mediaAttachmentName;
        if ( mediaPropertyType.equals( ValueTypes.STRING ) )
        {
            // backwards compatibility
            mediaAttachmentName = getData().getString( ContentPropertyNames.MEDIA );
        }
        else if ( mediaPropertyType.equals( ValueTypes.PROPERTY_SET ) )
        {
            final PropertySet mediaData = getData().getSet( ContentPropertyNames.MEDIA );
            mediaAttachmentName = mediaData.getString( ContentPropertyNames.MEDIA_ATTACHMENT );
        }
        else
        {
            return null;
        }

        if ( mediaAttachmentName == null )
        {
            return null;
        }

        return getAttachments().byName( mediaAttachmentName );
    }

    public ImageOrientation getOrientation()
    {
        final ExtraData cameraInfo = getAllExtraData().getMetadata( MediaInfo.CAMERA_INFO_METADATA_NAME );
        if ( cameraInfo != null && cameraInfo.getData().hasProperty( ContentPropertyNames.ORIENTATION ) )
        {
            return ImageOrientation.from( cameraInfo.getData().getString( ContentPropertyNames.ORIENTATION ) );
        }
        return null;
    }

    public FocalPoint getFocalPoint()
    {
        final PropertyTree contentData = getData();
        final Property mediaProperty = contentData.getProperty( ContentPropertyNames.MEDIA );
        if ( mediaProperty == null )
        {
            return FocalPoint.DEFAULT;
        }

        final ValueType mediaPropertyType = mediaProperty.getType();
        if ( !mediaPropertyType.equals( ValueTypes.PROPERTY_SET ) )
        {
            return FocalPoint.DEFAULT;
        }

        final PropertySet mediaData = getData().getSet( ContentPropertyNames.MEDIA );
        final PropertySet focalPointData = mediaData.getSet( ContentPropertyNames.MEDIA_FOCAL_POINT );
        if ( focalPointData == null )
        {
            return FocalPoint.DEFAULT;
        }

        final Double focalX = focalPointData.getDouble( ContentPropertyNames.MEDIA_FOCAL_POINT_X );
        final Double focalY = focalPointData.getDouble( ContentPropertyNames.MEDIA_FOCAL_POINT_Y );
        if ( focalX == null || focalY == null )
        {
            return FocalPoint.DEFAULT;
        }

        return new FocalPoint( focalX, focalY );
    }

    public Cropping getCropping()
    {
        final PropertyTree contentData = getData();
        final Property mediaProperty = contentData.getProperty( ContentPropertyNames.MEDIA );
        if ( mediaProperty == null )
        {
            return null;
        }

        final ValueType mediaPropertyType = mediaProperty.getType();
        if ( !mediaPropertyType.equals( ValueTypes.PROPERTY_SET ) )
        {
            return null;
        }

        final PropertySet mediaData = getData().getSet( ContentPropertyNames.MEDIA );
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

        //TODO The values stored in top, left, bottom and right are not the correct values
        final Double fixedTop = top / zoom;
        final Double fixedLeft = left / zoom;
        final Double fixedBottom = bottom / zoom;
        final Double fixedRight = right / zoom;

        return Cropping.create().
            zoom( zoom ).
            top( fixedTop ).
            left( fixedLeft ).
            bottom( fixedBottom ).
            right( fixedRight ).
            build();
    }

    public Attachment getSourceAttachment()
    {
        return getAttachments().byLabel( "source" );
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

    public static class Builder
        extends Content.Builder<Builder>
    {

        public Builder( final Media source )
        {
            super( source );
        }

        public Builder()
        {
            super();
        }

        @Override
        public Media build()
        {
            return new Media( this );
        }
    }
}
