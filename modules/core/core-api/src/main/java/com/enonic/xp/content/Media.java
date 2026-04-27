package com.enonic.xp.content;


import org.jspecify.annotations.Nullable;

import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.data.PropertySet;
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

    @Deprecated
    public boolean isImage()
    {
        return getType().isImageMedia() || getType().isVectorMedia();
    }

    @Deprecated
    public Attachment getMediaAttachment()
    {
        final PropertySet mediaData = getData().getSet( ContentPropertyNames.MEDIA );
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

    @Deprecated
    public @Nullable ImageOrientation getOrientation()
    {
        return MediaUtils.readOrientation( getData().getSet( ContentPropertyNames.MEDIA ) );
    }

    @Deprecated
    public @Nullable FocalPoint getFocalPoint()
    {
        return MediaUtils.readFocalPoint( getData().getSet( ContentPropertyNames.MEDIA ) );
    }

    @Deprecated
    public @Nullable Cropping getCropping()
    {
        return MediaUtils.readCropping( getData().getSet( ContentPropertyNames.MEDIA ) );
    }

    @Deprecated
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
