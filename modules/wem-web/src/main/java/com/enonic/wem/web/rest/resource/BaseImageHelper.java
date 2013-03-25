package com.enonic.wem.web.rest.resource;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import com.enonic.wem.api.Icon;
import com.enonic.wem.core.image.filter.effect.ScaleSquareFilter;

public abstract class BaseImageHelper
{
    protected BufferedImage toBufferedImage( final byte[] data )
        throws Exception
    {
        return ImageIO.read( new ByteArrayInputStream( data ) );
    }

    protected BufferedImage resizeImage( final BufferedImage image, final int size )
        throws Exception
    {
        return new ScaleSquareFilter( size ).filter( image );
    }

    protected BufferedImage loadDefaultImage( final String imageName )
        throws Exception
    {
        final InputStream in = getClass().getResourceAsStream( imageName + ".png" );
        if ( in == null )
        {
            throw new IOException( "Image [" + imageName + "] not found" );
        }

        return ImageIO.read( in );
    }

    public BufferedImage getIconImage( final Icon icon, final int size )
        throws Exception
    {
        if ( icon == null )
        {
            return null;
        }

        final BufferedImage image = toBufferedImage( icon.getData() );
        return resizeImage( image, size );
    }
}
