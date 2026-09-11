package com.enonic.xp.core.impl.image;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

/**
 * Encodes an already decoded and transformed raster in an isolated process. No request values are
 * interpreted as commands, paths, or ImageMagick expressions.
 */
final class ImageMagickEncoder
{
    private final String executable;

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
            throw new IllegalArgumentException( "Modern image encoding is disabled; configure encoding.executable" );
        }
    }

    void write( final BufferedImage image, final String format, final int quality, final OutputStream output )
        throws IOException
    {
        checkEnabled();
        if ( !( "webp".equals( format ) || "avif".equals( format ) ) || quality < 0 || quality > 100 )
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
            process = new ProcessBuilder( executable,
                                          "-limit", "thread", "1",
                                          "-limit", "memory", "256MiB",
                                          "-limit", "map", "0",
                                          "-limit", "disk", "0",
                                          "-limit", "time", Integer.toString( timeoutSeconds ),
                                          "png:" + input.toAbsolutePath(), "-strip",
                                          "-quality", Integer.toString( quality ),
                                          "-define", "webp:method=4",
                                          "-define", "heic:speed=6",
                                          "-define", "heic:max-threads=1",
                                          format + ":" + result.toAbsolutePath() )
                .redirectOutput( ProcessBuilder.Redirect.DISCARD )
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
            Files.deleteIfExists( input );
            Files.deleteIfExists( result );
            Files.deleteIfExists( directory );
        }
    }
}
