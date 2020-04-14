package com.enonic.xp.image;

import com.google.common.base.Preconditions;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.media.ImageOrientation;
import com.enonic.xp.util.BinaryReference;

public class ReadImageParams
{
    private final ContentId contentId;

    private final BinaryReference binaryReference;

    private final Cropping cropping;

    private final ScaleParams scaleParams;

    private final FocalPoint focalPoint;

    private final int scaleSize;

    private final boolean scaleSquare;

    private final boolean scaleWidth;

    private final String filterParam;

    private final int backgroundColor;

    private final String format;

    private final String mimeType;

    private final int quality;

    private final ImageOrientation orientation;

    public ReadImageParams( Builder builder )
    {
        this.contentId = builder.contentId;
        this.binaryReference = builder.binaryReference;
        this.cropping = builder.cropping;
        this.scaleParams = builder.scaleParams;
        this.focalPoint = builder.focalPoint != null ? builder.focalPoint : FocalPoint.DEFAULT;
        this.scaleSize = builder.scaleSize;
        this.scaleSquare = builder.scaleSquare;
        this.scaleWidth = builder.scaleWidth;
        this.filterParam = builder.filterParam;
        this.backgroundColor = builder.backgroundColor;
        this.format = builder.format;
        this.quality = builder.quality;
        this.mimeType = builder.mimeType;
        this.orientation = builder.orientation != null ? builder.orientation : ImageOrientation.TopLeft;
    }

    public ContentId getContentId()
    {
        return contentId;
    }

    public BinaryReference getBinaryReference()
    {
        return binaryReference;
    }

    public Cropping getCropping()
    {
        return cropping;
    }

    public ScaleParams getScaleParams()
    {
        return scaleParams;
    }

    public FocalPoint getFocalPoint()
    {
        return focalPoint;
    }

    public int getScaleSize()
    {
        return scaleSize;
    }

    public boolean isScaleSquare()
    {
        return scaleSquare;
    }

    public boolean isScaleWidth()
    {
        return scaleWidth;
    }

    public String getFilterParam()
    {
        return filterParam;
    }

    public int getBackgroundColor()
    {
        return backgroundColor;
    }

    public String getMimeType()
    {
        return mimeType;
    }

    @Deprecated
    public String getFormat()
    {
        return format;
    }

    public int getQuality()
    {
        return quality;
    }

    public ImageOrientation getOrientation()
    {
        return orientation;
    }

    public static Builder newImageParams()
    {
        return new Builder();
    }

    public static class Builder
    {
        private ContentId contentId;

        private BinaryReference binaryReference;

        private Cropping cropping;

        private ScaleParams scaleParams;

        private FocalPoint focalPoint;

        private int scaleSize;

        private boolean scaleSquare;

        private boolean scaleWidth;

        private String filterParam;

        private int backgroundColor;

        private String format;

        private String mimeType;

        private ImageOrientation orientation;

        public int quality;

        private Builder()
        {
        }

        public Builder contentId( ContentId contentId )
        {
            this.contentId = contentId;
            return this;
        }

        public Builder binaryReference( BinaryReference binaryReference )
        {
            this.binaryReference = binaryReference;
            return this;
        }

        public Builder cropping( Cropping cropping )
        {
            this.cropping = cropping;
            return this;
        }

        public Builder scaleParams( ScaleParams scaleParams )
        {
            this.scaleParams = scaleParams;
            return this;
        }

        public Builder focalPoint( FocalPoint focalPoint )
        {
            this.focalPoint = focalPoint;
            return this;
        }

        public Builder scaleSize( int scaleSize )
        {
            this.scaleSize = scaleSize;
            return this;
        }

        public Builder scaleSquare( boolean scaleSquare )
        {
            this.scaleSquare = scaleSquare;
            return this;
        }

        public Builder scaleWidth( boolean scaleWidth )
        {
            this.scaleWidth = scaleWidth;
            return this;
        }

        public Builder filterParam( String filterParam )
        {
            this.filterParam = filterParam;
            return this;
        }

        public Builder backgroundColor( int backgroundColor )
        {
            this.backgroundColor = backgroundColor;
            return this;
        }

        @Deprecated
        public Builder format( String format )
        {
            this.format = format;
            return this;
        }

        public Builder mimeType( final String mimeType )
        {
            this.mimeType = mimeType;
            return this;
        }

        public Builder quality( int quality )
        {
            this.quality = quality;
            return this;
        }

        public Builder orientation( ImageOrientation orientation )
        {
            this.orientation = orientation;
            return this;
        }

        public ReadImageParams build()
        {
            Preconditions.checkNotNull( contentId, "contentId cannot be null" );
            Preconditions.checkNotNull( binaryReference, "binaryReference cannot be null" );
            Preconditions.checkNotNull( mimeType, "mimeType cannot be null" );
            return new ReadImageParams( this );
        }
    }
}
