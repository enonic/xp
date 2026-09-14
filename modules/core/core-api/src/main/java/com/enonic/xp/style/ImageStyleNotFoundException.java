package com.enonic.xp.style;

import org.jspecify.annotations.NullMarked;

import com.enonic.xp.exception.NotFoundException;

/**
 * Indicates that a requested predefined image style could not be resolved.
 */
@NullMarked
public final class ImageStyleNotFoundException
    extends NotFoundException
{
    /**
     * Creates an exception identifying the missing image style.
     *
     * @param key the requested {@code application:name} style key
     */
    public ImageStyleNotFoundException( final String key )
    {
        super( "Image style [" + key + "] not found" );
    }
}
