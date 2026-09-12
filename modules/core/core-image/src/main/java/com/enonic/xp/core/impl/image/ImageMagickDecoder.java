package com.enonic.xp.core.impl.image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import com.google.common.io.ByteSource;

/** Decodes the first frame to a bounded, 8-bit RGBA raster in an isolated process. */
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

    Source open( final ByteSource blob )
        throws IOException
    {
        Files.createDirectories( temporaryFolder );
        final Path directory = Files.createTempDirectory( temporaryFolder, "decode-" ).toAbsolutePath();
        try
        {
            return new Source( blob, directory );
        }
        catch ( IOException | RuntimeException e )
        {
            delete( directory );
            throw e;
        }
    }

    final class Source
        implements AutoCloseable
    {
        private final Path directory;

        private final Path input;

        private final Path raster;

        private final String coder;

        private final long deadline;

        private final int width;

        private final int height;

        private Source( final ByteSource blob, final Path directory )
            throws IOException
        {
            this.directory = directory;
            this.input = directory.resolve( "input" );
            this.raster = directory.resolve( "raster.png" );
            copyInput( blob, input );
            this.coder = coder( input );
            Files.writeString( directory.resolve( "policy.xml" ), policy(), StandardCharsets.UTF_8 );
            // Extraction is lazy and shared. Give the actual probe + decode a single time budget.
            final String command = command();
            this.deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos( timeoutSeconds );
            final Path dimensions = directory.resolve( "dimensions.txt" );
            run( command, List.of( "-ping", coder + ":" + input + "[0]", "-format", "%w %h", "info:" + dimensions ), dimensions );
            if ( Files.size( dimensions ) > 64 )
            {
                throw new IOException( "Invalid image dimensions" );
            }
            final String[] size = Files.readString( dimensions ).trim().split( " " );
            if ( size.length != 2 )
            {
                throw new IOException( "Invalid image dimensions" );
            }
            this.width = Integer.parseInt( size[0] );
            this.height = Integer.parseInt( size[1] );
            if ( width < 1 || height < 1 || (long) width * height > maxPixels )
            {
                throw new IllegalArgumentException( "Source image exceeds encoding.maxPixels" );
            }
        }

        int width()
        {
            return width;
        }

        int height()
        {
            return height;
        }

        /** Called only after XP has admitted the decoded raster and transformations against its heap limit. */
        BufferedImage read()
            throws IOException
        {
            run( command(), List.of( coder + ":" + input + "[0]", "-strip", "-depth", "8", "PNG32:" + raster ), raster );
            // Check the intermediate header as well, before ImageIO allocates any pixels.
            try (var stream = ImageIO.createImageInputStream( raster.toFile() ))
            {
                final var readers = ImageIO.getImageReaders( stream );
                if ( !readers.hasNext() )
                {
                    throw new IOException( "Image decoder produced an invalid raster" );
                }
                final var reader = readers.next();
                try
                {
                    reader.setInput( stream );
                    if ( reader.getWidth( 0 ) != width || reader.getHeight( 0 ) != height )
                    {
                        throw new IOException( "Image dimensions changed during decoding" );
                    }
                    return reader.read( 0 );
                }
                finally
                {
                    reader.dispose();
                }
            }
        }

        private void run( final String command, final List<String> operation, final Path result )
            throws IOException
        {
            final long remaining = deadline - System.nanoTime();
            if ( remaining <= 0 )
            {
                throw new IOException( "Image decoding exceeded " + timeoutSeconds + " seconds" );
            }
            final List<String> arguments = new ArrayList<>( List.of( command,
                "-limit", "thread", "1", "-limit", "memory", "256MiB", "-limit", "map", "0", "-limit", "disk", "0",
                "-limit", "area", Long.toString( maxPixels ), "-limit", "width", Long.toString( maxPixels ),
                "-limit", "height", Long.toString( maxPixels ), "-limit", "list-length", "16",
                "-limit", "time", Long.toString( Math.max( 1, TimeUnit.NANOSECONDS.toSeconds( remaining ) ) ),
                "-define", "heic:max-threads=1", "-background", "none" ) );
            arguments.addAll( operation );
            final ProcessBuilder builder = ImageMagickEncoder.processBuilder( arguments, "embedded".equals( executable ) );
            final String existingConfiguration = builder.environment().get( "MAGICK_CONFIGURE_PATH" );
            builder.environment().put( "MAGICK_CONFIGURE_PATH", directory +
                ( existingConfiguration == null ? "" : File.pathSeparator + existingConfiguration ) );
            builder.environment().put( "MAGICK_TEMPORARY_PATH", directory.toString() );
            final Process process = builder.directory( directory.toFile() )
                .redirectOutput( ProcessBuilder.Redirect.DISCARD ).redirectError( ProcessBuilder.Redirect.DISCARD ).start();
            try
            {
                process.getOutputStream().close();
                if ( !process.waitFor( remaining, TimeUnit.NANOSECONDS ) )
                {
                    throw new IOException( "Image decoding exceeded " + timeoutSeconds + " seconds" );
                }
                if ( process.exitValue() != 0 || !Files.isRegularFile( result ) || Files.size( result ) == 0 )
                {
                    throw new IOException( "Image decoder failed (exit " + process.exitValue() + ")" );
                }
            }
            catch ( InterruptedException e )
            {
                Thread.currentThread().interrupt();
                throw new IOException( "Image decoding interrupted", e );
            }
            finally
            {
                ImageMagickEncoder.stop( process );
            }
        }

        private String policy()
        {
            // Input/output paths are fixed by XP. Reject delegates, indirect reads and other coders.
            return "<policymap>" +
                "<policy domain=\"delegate\" rights=\"none\" pattern=\"*\"/>" +
                "<policy domain=\"filter\" rights=\"none\" pattern=\"*\"/>" +
                "<policy domain=\"coder\" rights=\"none\" pattern=\"*\"/>" +
                "<policy domain=\"coder\" rights=\"read\" pattern=\"{JPEG,PNG,WEBP,AVIF,HEIC,BMP,TIFF}\"/>" +
                "<policy domain=\"coder\" rights=\"read|write\" pattern=\"{PNG,PNG32}\"/>" +
                "<policy domain=\"coder\" rights=\"write\" pattern=\"INFO\"/>" +
                "<policy domain=\"path\" rights=\"none\" pattern=\"@*\"/>" +
                "<policy domain=\"path\" rights=\"none\" pattern=\"-\"/>" +
                "<policy domain=\"path\" rights=\"none\" pattern=\"[Ff][Dd]:*\"/>" +
                "<policy domain=\"system\" name=\"max-memory-request\" value=\"256MiB\"/>" +
                "</policymap>";
        }

        @Override
        public void close()
            throws IOException
        {
            delete( directory );
        }
    }

    private String command()
        throws IOException
    {
        return "embedded".equals( executable ) ? EmbeddedImageMagick.executable().toString() : executable;
    }

    private void copyInput( final ByteSource blob, final Path input )
        throws IOException
    {
        try (InputStream stream = blob.openStream(); var output = Files.newOutputStream( input ))
        {
            final byte[] buffer = new byte[8192];
            long bytes = 0;
            int read;
            while ( ( read = stream.read( buffer ) ) != -1 )
            {
                bytes += read;
                if ( bytes > maxBytes )
                {
                    throw new IllegalArgumentException( "Source image exceeds decoding.maxBytes" );
                }
                output.write( buffer, 0, read );
            }
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

    private static void delete( final Path directory )
        throws IOException
    {
        try (var paths = Files.walk( directory ))
        {
            for ( Path path : paths.sorted( Comparator.reverseOrder() ).toList() )
            {
                Files.deleteIfExists( path );
            }
        }
    }
}
