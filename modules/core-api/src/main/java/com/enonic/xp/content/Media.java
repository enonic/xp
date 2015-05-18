package com.enonic.xp.content;


import com.google.common.annotations.Beta;

import com.enonic.xp.content.attachment.Attachment;
import com.enonic.xp.content.attachment.image.ImageAttachmentScale;
import com.enonic.xp.data.Property;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.ValueType;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.image.FocalPoint;

@Beta
public class Media
    extends Content
{
    protected Media( final Builder builder )
    {
        super( builder );
    }

    public boolean isImage()
    {
        return getType().isImageMedia();
    }

    public Attachment getMediaAttachment()
    {
        final PropertyTree contentData = getData();
        final Property mediaProperty = contentData.getProperty( ContentPropertyNames.MEDIA );
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

    public Attachment getBestFitImageAttachment( int requiredImageSize )
    {
        if ( isImage() )
        {
            switch ( getAttachments().getSize() )
            {
                case 0:
                    return null;
                case 1:
                    return getSourceAttachment();
                default:
                    for ( ImageAttachmentScale scale : ImageAttachmentScale.getScalesOrderedBySizeAsc() )
                    {
                        if ( requiredImageSize <= scale.getSize() )
                        {
                            if ( getAttachments().byLabel( scale.getLabel() ) != null )
                            {
                                return getAttachments().byLabel( scale.getLabel() );
                            }
                            else
                            {
                                return getSourceAttachment();
                            }
                        }
                    }
                    return getSourceAttachment();
            }
        }
        else
        {
            return null;
        }
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
        extends Content.Builder<Builder, Media>
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
