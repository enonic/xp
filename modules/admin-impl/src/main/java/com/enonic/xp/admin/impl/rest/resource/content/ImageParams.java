package com.enonic.xp.admin.impl.rest.resource.content;

import com.enonic.xp.media.ImageOrientation;

public final class ImageParams
{
    private final int size;

    private final ImageOrientation orientation;

    private final boolean cropRequired;

    private final boolean scaleWidth;

    public ImageParams( Builder builder )
    {
        this.size = builder.size;
        this.orientation = builder.orientation != null ? builder.orientation : ImageOrientation.TopLeft;
        this.cropRequired = builder.cropRequired;
        this.scaleWidth = builder.scaleWidth;
    }

    public boolean isCropRequired()
    {
        return cropRequired;
    }

    public int getSize()
    {
        return size;
    }

    public ImageOrientation getOrientation()
    {
        return orientation;
    }

    public boolean isScaleWidth()
    {
        return scaleWidth;
    }

    public static Builder newImageParams()
    {
        return new Builder();
    }

    static class Builder
    {
        private int size;

        private ImageOrientation orientation;

        private boolean cropRequired;

        private boolean scaleWidth;

        private Builder()
        {
        }

        public Builder size( int size )
        {
            this.size = size;
            return this;
        }

        public Builder orientation( ImageOrientation orientation )
        {
            this.orientation = orientation;
            return this;
        }

        public Builder cropRequired( boolean cropRequired )
        {
            this.cropRequired = cropRequired;
            return this;
        }

        public Builder scaleWidth( boolean scaleWidth )
        {
            this.scaleWidth = scaleWidth;
            return this;
        }

        public ImageParams build()
        {
            return new ImageParams( this );
        }
    }
}
