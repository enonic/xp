package com.enonic.xp.core.impl.image;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import com.google.common.io.ByteSource;

/** Decodes the first raster through the shared native process runner. */
final class ImageMagickDecoder
{
    private final String executable;
    private final Path temporaryFolder;
    private final int timeoutSeconds;
    private final long maxPixels;
    private final long maxBytes;

    ImageMagickDecoder( final String executable, final Path temporaryFolder, final int timeoutSeconds,
                        final long maxPixels, final long maxBytes )
    {
        if ( executable == null || executable.isBlank() || timeoutSeconds < 1 || maxPixels < 1 || maxBytes < 1 )
        {
            throw new IllegalArgumentException( "Invalid image decoder configuration" );
        }
        this.executable = executable;
        this.temporaryFolder = temporaryFolder;
        this.timeoutSeconds = timeoutSeconds;
        this.maxPixels = maxPixels;
        this.maxBytes = maxBytes;
    }

    Source open( final ByteSource blob ) throws IOException
    {
        final PreparedImageSource prepared = PreparedImageSource.copy( blob, temporaryFolder, maxBytes, null );
        try { return new Source( prepared.path(), prepared ); }
        catch ( IOException | RuntimeException | Error e ) { prepared.close(); throw e; }
    }

    Source open( final Path prepared ) throws IOException
    {
        return new Source( prepared, null );
    }

    final class Source implements AutoCloseable
    {
        private final NativeImageProcess process;
        private final PreparedImageSource ownedSource;
        private final Path input;
        private final String coder;
        private final int width;
        private final int height;
        private NativeImageRaster raster;

        private Source( final Path input, final PreparedImageSource ownedSource ) throws IOException
        {
            this.input = input.toAbsolutePath();
            this.ownedSource = ownedSource;
            this.coder = coder( input );
            process = new NativeImageProcess( executable, temporaryFolder, "decode", timeoutSeconds,
                "JPEG,PNG,WEBP,AVIF,HEIC,BMP,TIFF", "PNG32,INFO" );
            try
            {
                final Path dimensions = process.file( "dimensions.txt" );
                process.run( List.of( "-limit", "area", Long.toString( maxPixels ),
                    "-limit", "width", Long.toString( maxPixels ), "-limit", "height", Long.toString( maxPixels ),
                    "-background", "none", "-ping", coder + ":" + this.input + "[0]",
                    "-format", "%w %h", "info:" + dimensions ), dimensions );
                if ( Files.size( dimensions ) > 64 ) { throw new IOException( "Invalid image dimensions" ); }
                final String[] size = Files.readString( dimensions ).trim().split( " " );
                if ( size.length != 2 ) { throw new IOException( "Invalid image dimensions" ); }
                width = Integer.parseInt( size[0] );
                height = Integer.parseInt( size[1] );
                if ( width < 1 || height < 1 || (long) width * height > maxPixels )
                {
                    throw new IllegalArgumentException( "Source image exceeds processing.maxPixels" );
                }
            }
            catch ( IOException | RuntimeException | Error e ) { process.close(); throw e; }
        }

        int width() { return width; }
        int height() { return height; }

        NativeImageRaster raster() throws IOException
        {
            if ( raster == null )
            {
                final Path output = process.file( "raster.png" );
                process.run( List.of( "-limit", "area", Long.toString( maxPixels ),
                    "-limit", "width", Long.toString( maxPixels ), "-limit", "height", Long.toString( maxPixels ),
                    "-background", "none", coder + ":" + input + "[0]", "-strip", "-depth", "8", "PNG32:" + output ), output );
                raster = NativeImageRaster.validate( output, width, height );
            }
            return raster;
        }

        BufferedImage read() throws IOException { return raster().read(); }

        @Override
        public void close() throws IOException
        {
            try { process.close(); }
            finally { if ( ownedSource != null ) { ownedSource.close(); } }
        }
    }

    private static String coder( final Path input )
        throws IOException
    {
        final byte[] header;
        try (var stream = Files.newInputStream( input ))
        {
            header = stream.readNBytes( 16 );
        }
        if ( header.length >= 12 )
        {
            final String magic = new String( header, StandardCharsets.ISO_8859_1 );
            if ( magic.startsWith( "\u0089PNG\r\n\u001a\n" ) )
            {
                return "PNG";
            }
            if ( magic.startsWith( "\u00ff\u00d8" ) )
            {
                return "JPEG";
            }
            if ( magic.startsWith( "RIFF" ) && magic.substring( 8, 12 ).equals( "WEBP" ) )
            {
                return "WEBP";
            }
            if ( magic.substring( 4, 8 ).equals( "ftyp" ) )
            {
                return "AVIF";
            }
            if ( magic.startsWith( "BM" ) )
            {
                return "BMP";
            }
            if ( magic.startsWith( "II" ) || magic.startsWith( "MM" ) )
            {
                return "TIFF";
            }
        }
        throw new IllegalArgumentException( "Unsupported source image format for ImageMagic decoding" );
    }

}
