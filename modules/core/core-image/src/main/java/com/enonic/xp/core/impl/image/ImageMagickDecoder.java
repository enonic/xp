package com.enonic.xp.core.impl.image;

import java.awt.color.ColorSpace;
import java.awt.color.ICC_Profile;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.google.common.io.ByteSource;

import com.enonic.xp.core.internal.image.ImageMagick;

/** Decodes the first raster through the shared native process runner. */
@NullMarked
final class ImageMagickDecoder
{
    private final ImageMagick imageMagick;
    private final Path temporaryFolder;
    private final int timeoutSeconds;
    private final long maxPixels;
    private final long maxBytes;
    private final long maxDiskBytes;

    ImageMagickDecoder( final ImageMagick imageMagick, final Path temporaryFolder, final int timeoutSeconds,
                        final long maxPixels, final long maxBytes, final long maxDiskBytes )
    {
        if ( timeoutSeconds < 1 || maxPixels < 1 || maxBytes < 1 )
        {
            throw new IllegalArgumentException( "Invalid image decoder configuration" );
        }
        this.imageMagick = imageMagick;
        this.temporaryFolder = temporaryFolder;
        this.timeoutSeconds = timeoutSeconds;
        this.maxPixels = maxPixels;
        this.maxBytes = maxBytes;
        this.maxDiskBytes = maxDiskBytes;
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
        private final @Nullable PreparedImageSource ownedSource;
        private final Path input;
        private final String coder;
        private final int width;
        private final int height;
        private final boolean hasIccProfile;
        private @Nullable NativeImageRaster raster;

        private Source( final Path input, final @Nullable PreparedImageSource ownedSource ) throws IOException
        {
            this.input = input.toAbsolutePath();
            this.ownedSource = ownedSource;
            this.coder = coder( input );
            process = new NativeImageProcess( imageMagick, temporaryFolder, "decode", timeoutSeconds,
                "JPEG,PNG,WEBP,AVIF,HEIC,BMP,TIFF,ICC", "RGBA,INFO", maxDiskBytes );
            try
            {
                final Path dimensions = process.file( "dimensions.txt" );
                process.run( List.of( "-limit", "area", Long.toString( maxPixels ),
                    "-limit", "width", Long.toString( maxPixels ), "-limit", "height", Long.toString( maxPixels ),
                    "-background", "none", "-ping", coder + ":" + this.input + "[0]",
                    "-format", "%w %h %[profiles]", "info:" + dimensions ), dimensions );
                if ( Files.size( dimensions ) > 512 ) { throw new IOException( "Invalid image metadata" ); }
                final String[] size = Files.readString( dimensions ).trim().split( " ", 3 );
                if ( size.length < 2 ) { throw new IOException( "Invalid image dimensions" ); }
                width = Integer.parseInt( size[0] );
                height = Integer.parseInt( size[1] );
                hasIccProfile = size.length == 3 && List.of( size[2].split( "[,\\s]+" ) ).contains( "icc" );
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
                final Path output = process.file( "raster.rgba" );
                final List<String> operation = new ArrayList<>( List.of( "-limit", "area", Long.toString( maxPixels ),
                    "-limit", "width", Long.toString( maxPixels ), "-limit", "height", Long.toString( maxPixels ),
                    // EXIF orientation is applied from XP metadata by the transformer, without native auto-orient.
                    "-background", "none", coder + ":" + input + "[0]" ) );
                if ( hasIccProfile )
                {
                    final Path profile = process.file( "srgb.icc" );
                    Files.write( profile, ICC_Profile.getInstance( ColorSpace.CS_sRGB ).getData() );
                    operation.addAll( List.of( "-profile", profile.toString() ) );
                }
                operation.addAll( List.of( "-colorspace", "sRGB", "-strip" ) );
                operation.addAll( NativeImageRaster.outputArguments( output, true ) );
                process.run( operation, output );
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
