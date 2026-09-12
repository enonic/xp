package com.enonic.xp.core.impl.image;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/** Encodes a generated PNG without re-materializing rasters from adjacent native stages. */
final class ImageMagickEncoder
{
    private final String executable;
    private final int timeoutSeconds;
    private final Path temporaryFolder;

    ImageMagickEncoder( final int timeoutSeconds, final Path temporaryFolder )
    {
        this( "embedded", timeoutSeconds, temporaryFolder );
    }

    ImageMagickEncoder( final String executable, final int timeoutSeconds, final Path temporaryFolder )
    {
        if ( executable == null || timeoutSeconds < 1 ) { throw new IllegalArgumentException( "Invalid image encoder configuration" ); }
        this.executable = executable;
        this.timeoutSeconds = timeoutSeconds;
        this.temporaryFolder = temporaryFolder;
    }

    void checkEnabled()
    {
        if ( executable.isBlank() )
        {
            throw new IllegalArgumentException( "ImageMagick encoding is disabled; set encoding.backend=ImageMagic" );
        }
    }

    void write( final BufferedImage image, final String format, final int quality, final OutputStream output ) throws IOException
    {
        write( image, format, quality, false, output );
    }

    void write( final BufferedImage image, final String format, final int quality, final boolean progressive,
                final OutputStream output ) throws IOException
    {
        checkEnabled();
        try (var process = process())
        {
            encode( process, NativeImageRaster.write( image, process.file( "input.png" ) ), format, quality, progressive, output );
        }
    }

    void write( final NativeImageRaster raster, final String format, final int quality, final boolean progressive,
                final OutputStream output ) throws IOException
    {
        checkEnabled();
        try (var process = process()) { encode( process, raster, format, quality, progressive, output ); }
    }

    private NativeImageProcess process() throws IOException
    {
        return new NativeImageProcess( executable, temporaryFolder, "encode", timeoutSeconds, "PNG",
            "JPEG,PNG,GIF,WEBP,AVIF,HEIC" );
    }

    private void encode( final NativeImageProcess process, final NativeImageRaster raster, final String format,
                         final int quality, final boolean progressive, final OutputStream output ) throws IOException
    {
        if ( !Set.of( "jpeg", "png", "gif", "webp", "avif" ).contains( format ) || quality < -1 || quality > 100 )
        {
            throw new IllegalArgumentException( "Invalid image encoding parameters" );
        }
        final Path result = process.file( "output." + format );
        final List<String> operation = new ArrayList<>( List.of( "PNG:" + raster.path(), "-strip",
            "-define", "webp:method=4", "-define", "heic:speed=6",
            "-interlace", progressive && "jpeg".equals( format ) ? "Plane" : "None" ) );
        if ( quality >= 0 ) { operation.addAll( List.of( "-quality", Integer.toString( quality ) ) ); }
        operation.add( format + ":" + result );
        process.run( operation, result );
        Files.copy( result, output );
    }
}
