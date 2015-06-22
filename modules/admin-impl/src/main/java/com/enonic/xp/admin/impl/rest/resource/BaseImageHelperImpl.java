package com.enonic.xp.admin.impl.rest.resource;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.osgi.service.component.annotations.Component;

import com.enonic.xp.image.filter.ScaleSquareFunction;

@Component
public abstract class BaseImageHelperImpl
    implements BaseImageHelper
{
    protected BufferedImage toBufferedImage( final InputStream dataStream )
    {
        try
        {
            return ImageIO.read( dataStream );
        }
        catch ( IOException e )
        {
            throw new RuntimeException( "Failed to read BufferedImage from InputStream", e );
        }
    }

    protected BufferedImage resizeImage( final BufferedImage image, final int size )
    {
        return new ScaleSquareFunction( size ).scale( image );
    }

    protected BufferedImage loadDefaultImage( final String imageName )
    {
        final InputStream in = getClass().getResourceAsStream( imageName + ".png" );
        if ( in == null )
        {
            throw new IllegalArgumentException( "Image [" + imageName + "] not found" );
        }

        try
        {
            return ImageIO.read( in );
        }
        catch ( IOException e )
        {
            throw new RuntimeException( "Failed to load default image: " + imageName, e );
        }
    }

    public BufferedImage resizeImage( final InputStream is, final int size )
    {
        return resizeImage( toBufferedImage( is ), size );
    }
}
