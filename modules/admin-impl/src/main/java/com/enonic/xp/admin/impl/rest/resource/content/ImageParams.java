package com.enonic.xp.admin.impl.rest.resource.content;

import com.enonic.xp.media.ImageOrientation;

public final class ImageParams
{
    private final int size;

    private final ImageOrientation orientation;

    private final boolean cropRequired;

    public ImageParams( Builder builder )
    {
        this.size = builder.size;
        this.orientation = builder.orientation != null ? builder.orientation : ImageOrientation.TopLeft;
        this.cropRequired = builder.cropRequired;
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

    public static Builder newImageParams()
    {
        return new Builder();
    }

    static class Builder
    {
        private int size;

        private ImageOrientation orientation;

        private boolean cropRequired;

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

        public ImageParams build()
        {
            return new ImageParams( this );
        }
    }
}
