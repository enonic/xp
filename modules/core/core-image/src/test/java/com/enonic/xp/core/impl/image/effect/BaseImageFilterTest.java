/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.xp.core.impl.image.effect;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Objects;

import javax.imageio.ImageIO;

public abstract class BaseImageFilterTest
{
    protected final BufferedImage getOpaque()
    {
        return readImageFromResource( "source.jpg" ); // 400x300
    }

    protected final BufferedImage getTransparent()
    {
        return readImageFromResource( "transparent.png" ); // 500x154
    }

    private BufferedImage readImageFromResource( final String name )
    {
        try (InputStream resourceAsStream = BaseImageFilterTest.class.getResourceAsStream( name ))
        {
            return ImageIO.read( Objects.requireNonNull( resourceAsStream ) );
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
    }

    ImageScales newScaleFunctions()
    {
        return new ImageScales( 8000 );
    }

    ImageFilters newFilters()
    {
        return new ImageFilters();
    }
}
