package com.enonic.xp.portal.impl.resource.image;

import java.util.Objects;

class ImageHandleResourceKey
{
    private String path;

    private String filterParam;

    private int quality;

    private String background;

    public ImageHandleResourceKey( final String path, final String filterParam, final int quality, final String background )
    {
        this.path = path;
        this.filterParam = filterParam;
        this.quality = quality;
        this.background = background;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof ImageHandleResourceKey ) )
        {
            return false;
        }
        final ImageHandleResourceKey that = (ImageHandleResourceKey) o;
        return Objects.equals( this.path, that.path ) &&
            Objects.equals( this.filterParam, that.filterParam ) &&
            Objects.equals( this.quality, that.quality ) &&
            Objects.equals( this.background, that.background );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( path, filterParam, quality, background );
    }

    public static ImageHandleResourceKey from( final String path, final String filterParam, final int quality, final String background )
    {
        return new ImageHandleResourceKey( path, filterParam, quality, background );
    }
}
