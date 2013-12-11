package com.enonic.wem.admin.rest.resource;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.blob.Blob;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.icon.Icon;
import com.enonic.wem.core.image.filter.effect.ScaleSquareFilter;

public abstract class BaseImageHelper
{
    protected final Client client;

    protected BaseImageHelper( final Client client )
    {
        this.client = client;
    }

    protected BufferedImage toBufferedImage( final byte[] data )
    {
        try
        {
            return ImageIO.read( new ByteArrayInputStream( data ) );
        }
        catch ( IOException e )
        {
            throw new RuntimeException( "Failed to read BufferedImage from byte array", e );
        }
    }

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
        return new ScaleSquareFilter( size ).filter( image );
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

    public BufferedImage resizeImage( final Blob blob, final int size )
    {
        return resizeImage( toBufferedImage( blob.getStream() ), size );
    }
}
