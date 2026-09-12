package com.enonic.xp.core.impl.image;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;

/** A header-checked PNG owned by its native stage; materialized in Java only at a Java backend boundary. */
record NativeImageRaster(Path path, int width, int height)
{
    NativeImageRaster
    {
        if ( width < 1 || height < 1 ) { throw new IllegalArgumentException( "Invalid raster dimensions" ); }
    }

    static NativeImageRaster validate( final Path path, final int width, final int height ) throws IOException
    {
        final NativeImageRaster raster = new NativeImageRaster( path, width, height );
        raster.inspect( false );
        return raster;
    }

    static NativeImageRaster write( final BufferedImage image, final Path path ) throws IOException
    {
        if ( !ImageIO.write( image, "png", path.toFile() ) ) { throw new IOException( "PNG writer is unavailable" ); }
        return new NativeImageRaster( path, image.getWidth(), image.getHeight() );
    }

    BufferedImage read() throws IOException
    {
        return inspect( true );
    }

    private BufferedImage inspect( final boolean read ) throws IOException
    {
        try (var stream = ImageIO.createImageInputStream( path.toFile() ))
        {
            if ( stream == null ) { throw new IOException( "Missing intermediate raster" ); }
            final var readers = ImageIO.getImageReaders( stream );
            if ( !readers.hasNext() ) { throw new IOException( "Invalid intermediate raster" ); }
            final ImageReader reader = readers.next();
            try
            {
                reader.setInput( stream );
                if ( !"png".equalsIgnoreCase( reader.getFormatName() ) || reader.getWidth( 0 ) != width || reader.getHeight( 0 ) != height )
                {
                    throw new IOException( "Unexpected intermediate raster dimensions or format" );
                }
                return read ? reader.read( 0 ) : null;
            }
            finally { reader.dispose(); }
        }
    }
}
