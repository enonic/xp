package com.enonic.xp.style;

import com.enonic.xp.exception.NotFoundException;

public final class ImageStyleNotFoundException
    extends NotFoundException
{
    public ImageStyleNotFoundException( final String key )
    {
        super( "Image style [" + key + "] not found" );
    }
}
