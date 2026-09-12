package com.enonic.xp.core.impl.image;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

/**
 * Encodes an already decoded and transformed raster in an isolated process. No request values are
 * interpreted as commands, paths, or ImageMagick expressions.
 */
final class ImageMagickEncoder
{
    private final String executable;

    ImageMagickEncoder( final int timeoutSeconds, final Path temporaryFolder )
    {
        this( "embedded", timeoutSeconds, temporaryFolder );
    }

    private final int timeoutSeconds;

    private final Path temporaryFolder;

    ImageMagickEncoder( final String executable, final int timeoutSeconds, final Path temporaryFolder )
    {
        if ( executable == null || timeoutSeconds < 1 )
        {
            throw new IllegalArgumentException( "Invalid image encoder configuration" );
        }
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

    void write( final BufferedImage image, final String format, final int quality, final OutputStream output )
        throws IOException
    {
        write( image, format, quality, false, output );
    }

    void write( final BufferedImage image, final String format, final int quality, final boolean progressive,
                final OutputStream output )
        throws IOException
    {
        checkEnabled();
        if ( !Set.of( "jpeg", "png", "gif", "webp", "avif" ).contains( format ) || quality < -1 || quality > 100 )
        {
            throw new IllegalArgumentException( "Invalid image encoding parameters" );
        }

        Files.createDirectories( temporaryFolder );
        final Path directory = Files.createTempDirectory( temporaryFolder, "encode-" );
        final Path input = directory.resolve( "input.png" );
        final Path result = directory.resolve( "output." + format );
        Process process = null;
        try
        {
            if ( !ImageIO.write( image, "png", input.toFile() ) )
            {
                throw new IOException( "PNG writer is unavailable" );
            }
            final String command = "embedded".equals( executable ) ? EmbeddedImageMagick.executable().toString() : executable;
            final List<String> arguments = new ArrayList<>( List.of( command,
                                          "-limit", "thread", "1",
                                          "-limit", "memory", "256MiB",
                                          "-limit", "map", "0",
                                          "-limit", "disk", "0",
                                          "-limit", "time", Integer.toString( timeoutSeconds ),
                                          "png:" + input.toAbsolutePath(), "-strip",
                                          "-define", "webp:method=4",
                                          "-define", "heic:speed=6",
                                          "-define", "heic:max-threads=1",
                                          "-interlace", progressive && "jpeg".equals( format ) ? "Plane" : "None" ) );
            if ( quality >= 0 )
            {
                arguments.addAll( List.of( "-quality", Integer.toString( quality ) ) );
            }
            arguments.add( format + ":" + result.toAbsolutePath() );
            final ProcessBuilder builder = new ProcessBuilder( arguments );
            if ( "embedded".equals( executable ) && System.getProperty( "os.name" ).startsWith( "Mac" ) )
            {
                final Path root = Path.of( command ).getParent().getParent();
                builder.environment().put( "MAGICK_HOME", root.toString() );
                builder.environment().put( "MAGICK_CONFIGURE_PATH", root.resolve( "etc/ImageMagick-7" ).toString() );
                builder.environment().put( "LIBHEIF_PLUGIN_PATH", root.resolve( "lib/libheif" ).toString() );
            }
            process = builder.redirectOutput( ProcessBuilder.Redirect.DISCARD )
                .redirectError( ProcessBuilder.Redirect.DISCARD )
                .start();
            process.getOutputStream().close();

            if ( !process.waitFor( timeoutSeconds, TimeUnit.SECONDS ) )
            {
                throw new IOException( "Image encoding exceeded " + timeoutSeconds + " seconds" );
            }
            if ( process.exitValue() != 0 || !Files.isRegularFile( result ) || Files.size( result ) == 0 )
            {
                throw new IOException( "Image encoder failed for " + format + " (exit " + process.exitValue() + ")" );
            }
            Files.copy( result, output );
        }
        catch ( InterruptedException e )
        {
            Thread.currentThread().interrupt();
            throw new IOException( "Image encoding interrupted", e );
        }
        finally
        {
            stop( process );
            Files.deleteIfExists( input );
            Files.deleteIfExists( result );
            Files.deleteIfExists( directory );
        }
    }

    static void stop( final Process process )
    {
        if ( process != null && process.isAlive() )
        {
            process.descendants().forEach( ProcessHandle::destroyForcibly );
            process.destroyForcibly();
            // Wait for the killed encoder before removing files or releasing the processing slot.
            boolean interrupted = Thread.interrupted();
            try
            {
                while ( process.isAlive() )
                {
                    try
                    {
                        process.waitFor();
                    }
                    catch ( InterruptedException e )
                    {
                        interrupted = true;
                    }
                }
            }
            finally
            {
                if ( interrupted )
                {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}
