package com.enonic.xp.admin.impl.rest.resource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;

import javax.imageio.ImageIO;

import com.google.common.io.ByteStreams;
import com.google.common.net.MediaType;

import com.enonic.xp.icon.Icon;
import com.enonic.xp.image.ImageHelper;

public abstract class BaseImageHelper
{

    private static final MediaType IMAGE_SVG = MediaType.SVG_UTF_8.withoutParameters();

    protected final byte[] loadDefaultImage( final String imageName )
    {
        try (InputStream in = getClass().getResourceAsStream( imageName + ".svg" ))
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

    protected final Icon loadDefaultIcon( final String iconName )
    {
        final byte[] image = loadDefaultImage( iconName );
        return Icon.from( image, "image/svg+xml", Instant.ofEpochMilli( 0L ) );
    }

    public final byte[] readIconImage( final Icon icon, final int size )
        throws IOException
    {
        if ( isSvg( icon ) )
        {
            return icon.toByteArray();
        }
        else
        {
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageHelper.writeImage( out, ImageHelper.scaleSquare( ImageIO.read( icon.asInputStream() ), size ),
                                    ImageHelper.getFormatByMimeType( icon.getMimeType() ), -1 );
            return out.toByteArray();
        }
    }

    public final boolean isSvg( final Icon icon )
    {
        return IMAGE_SVG.is( MediaType.parse( icon.getMimeType() ).withoutParameters() );
    }

}
