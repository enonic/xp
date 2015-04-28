package com.enonic.xp.admin.impl.rest.resource.content;

public class ImageParams
{
    private int size;

    private int orientation;

    private boolean cropRequired;

    public ImageParams( Builder builder )
    {
        this.size = builder.size;
        this.orientation = builder.orientation;
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

    public int getOrientation()
    {
        return orientation;
    }

    public static Builder newImageParams()
    {
        return new Builder();
    }

    protected static class Builder
    {
        private int size;

        private int orientation;

        private boolean cropRequired;

        private Builder()
        {

        }

        ;

        public Builder size( int size )
        {
            this.size = size;
            return this;
        }

        public Builder orientation( int orientation )
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
