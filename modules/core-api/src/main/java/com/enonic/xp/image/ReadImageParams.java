package com.enonic.xp.image;

import com.enonic.xp.media.ImageOrientation;

public class ReadImageParams
{
    private final int size;

    private final ImageOrientation orientation;

    private final Cropping cropping;

    private final boolean scaleSquare;

    private final boolean scaleWidth;

    public ReadImageParams( Builder builder )
    {
        this.size = builder.size;
        this.orientation = builder.orientation != null ? builder.orientation : ImageOrientation.TopLeft;
        this.cropping = builder.cropping;
        this.scaleSquare = builder.scaleSquare;
        this.scaleWidth = builder.scaleWidth;
    }

    public int getSize()
    {
        return size;
    }

    public ImageOrientation getOrientation()
    {
        return orientation;
    }

    public Cropping getCropping()
    {
        return cropping;
    }

    public boolean isScaleSquare()
    {
        return scaleSquare;
    }

    public boolean isScaleWidth()
    {
        return scaleWidth;
    }

    public static Builder newImageParams()
    {
        return new Builder();
    }

    public static class Builder
    {
        private int size;

        private ImageOrientation orientation;

        private Cropping cropping;

        private boolean scaleSquare;

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

        public Builder cropping( Cropping cropping )
        {
            this.cropping = cropping;
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

        public ReadImageParams build()
        {
            return new ReadImageParams( this );
        }
    }

}
