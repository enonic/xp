package com.enonic.xp.core.impl.image;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/** Encodes a raw raster without re-materializing pixels from adjacent native stages. */
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
            encode( process, NativeImageRaster.write( image, process.file( "input.rgba" ) ), format, quality, progressive, output );
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
        return new NativeImageProcess( executable, temporaryFolder, "encode", timeoutSeconds, "RGBA",
            "JPEG,PNG,PNG24,PNG32,GIF,WEBP,AVIF,HEIC" );
    }

    private void encode( final NativeImageProcess process, final NativeImageRaster raster, final String format,
                         final int quality, final boolean progressive, final OutputStream output ) throws IOException
    {
        if ( !Set.of( "jpeg", "png", "gif", "webp", "avif" ).contains( format ) || quality < -1 || quality > 100 )
        {
            throw new IllegalArgumentException( "Invalid image encoding parameters" );
        }
        final Path result = process.file( "output." + format );
        final List<String> operation = new ArrayList<>( raster.inputArguments() );
        operation.addAll( List.of( "-strip",
            "-define", "webp:method=4", "-define", "heic:speed=6",
            "-interlace", progressive && "jpeg".equals( format ) ? "Plane" : "None" ) );
        if ( quality >= 0 ) { operation.addAll( List.of( "-quality", Integer.toString( quality ) ) ); }
        // Keep sRGB samples in RGB channels: ImageIO treats grayscale PNG samples as linear gray.
        final String coder = "png".equals( format ) ? ( raster.alpha() ? "PNG32" : "PNG24" ) : format;
        operation.add( coder + ":" + result );
        process.run( operation, result );
        Files.copy( result, output );
    }
}
