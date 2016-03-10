package com.enonic.xp.admin.impl.rest.resource;

import com.enonic.xp.icon.Icon;
import com.enonic.xp.image.ImageHelper;
import com.google.common.io.ByteStreams;
import com.google.common.net.MediaType;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public abstract class BaseImageHelper
{

    private static final MediaType IMAGE_SVG = MediaType.SVG_UTF_8.withoutParameters();

    public final BufferedImage toBufferedImage( final InputStream dataStream )
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

    protected final BufferedImage resizeImage( final BufferedImage image, final int size )
    {
        return ImageHelper.scaleSquare( image, size );
    }

    protected final byte[] loadDefaultImage( final String imageName )
    {
        try( final InputStream in = getClass().getResourceAsStream( imageName + ".svg" ) )
        {
            if ( in == null )
            {
                throw new IllegalArgumentException( "Image [" + imageName + "] not found" );
            }

            return ByteStreams.toByteArray( in );
        }
        catch ( IOException e )
        {
            throw new RuntimeException( "Failed to load default image: " + imageName, e );
        }
    }

    public final BufferedImage resizeImage( final InputStream is, final int size )
    {
        return resizeImage( toBufferedImage( is ), size );
    }

    public final boolean isSvg( final Icon icon )
    {
        return IMAGE_SVG.is( MediaType.parse( icon.getMimeType() ).withoutParameters() );
    }

}
