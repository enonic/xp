package com.enonic.xp.core.util;


import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import com.google.common.io.ByteSource;

public class ImageHelper
{
    public static BufferedImage toBufferedImage( final InputStream inputStream )
    {
        try
        {
            return ImageIO.read( inputStream );
        }
        catch ( IOException e )
        {
            throw Exceptions.newRutime( "Failed to read BufferedImage from InputStream" ).withCause( e );
        }
    }

    public static ByteSource toByteSource( final BufferedImage image, final String format )
    {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        try
        {
            ImageIO.write( image, format, output );
        }
        catch ( IOException e )
        {
            throw Exceptions.newRutime( "Failed to write BufferedImage to InputStream" ).withCause( e );
        }
        return ByteSource.wrap( output.toByteArray() );
    }
}
