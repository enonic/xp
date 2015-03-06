package com.enonic.xp.content;


import com.google.common.base.Preconditions;

import com.enonic.xp.schema.content.ContentTypeName;

public class ThumbnailParams
{
    private int size;

    private boolean crop;

    public static final String DEFAULT_ICON_SIZE = "128";

    public ThumbnailParams size(int value) {
        this.size = value;
        return this;
    }

    public ThumbnailParams crop(boolean value) {

        this.crop = value;
        return this;
    }

    public int getSize()
    {
        return size;
    }

    public boolean getCrop() { return crop; }

    public void validate()
    {
        Preconditions.checkArgument( this.size > 0, "size must be more than zero: " + this.size );
    }

    public void setSize( final int size )
    {
        this.size = size;
    }

    public static ThumbnailParams create( ContentTypeName contentTypeName )
    {
        if(contentTypeName.isImageMedia())
        {
            ThumbnailParams thumbnailParams = new ThumbnailParams();
            thumbnailParams.crop( false ).size( Integer.valueOf( DEFAULT_ICON_SIZE ) );
            return thumbnailParams;
        }

        return null;
    }

    public static ThumbnailParams create()
    {
        ThumbnailParams thumbnailParams = new ThumbnailParams();
        thumbnailParams.crop( true ).size( Integer.valueOf( DEFAULT_ICON_SIZE ) );
        return thumbnailParams;
    }
}
