package com.enonic.xp.core.impl.image;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

/** Common process limits, policy, deadline and temporary-file ownership for every native stage. */
final class NativeImageProcess implements AutoCloseable
{
    private final String executable;
    private final int timeoutSeconds;
    private final Path directory;
    private long deadline;

    NativeImageProcess( final String executable, final Path folder, final String stage, final int timeoutSeconds,
                        final String readCoders, final String writeCoders ) throws IOException
    {
        this.executable = executable;
        this.timeoutSeconds = timeoutSeconds;
        Files.createDirectories( folder );
        directory = Files.createTempDirectory( folder, stage + "-" ).toAbsolutePath();
        try
        {
            Files.writeString( file( "policy.xml" ), "<policymap>" +
                "<policy domain=\"delegate\" rights=\"none\" pattern=\"*\"/>" +
                "<policy domain=\"filter\" rights=\"none\" pattern=\"*\"/>" +
                "<policy domain=\"coder\" rights=\"none\" pattern=\"*\"/>" +
                "<policy domain=\"coder\" rights=\"read\" pattern=\"{" + readCoders + "}\"/>" +
                "<policy domain=\"coder\" rights=\"write\" pattern=\"{" + writeCoders + "}\"/>" +
                "<policy domain=\"coder\" rights=\"read|write\" pattern=\"RGBA\"/>" +
                "<policy domain=\"path\" rights=\"none\" pattern=\"@*\"/>" +
                "<policy domain=\"path\" rights=\"none\" pattern=\"-\"/>" +
                "<policy domain=\"path\" rights=\"none\" pattern=\"[Ff][Dd]:*\"/>" +
                "<policy domain=\"system\" name=\"max-memory-request\" value=\"256MiB\"/>" +
                "</policymap>" );
        }
        catch ( IOException | RuntimeException e )
        {
            close();
            throw e;
        }
    }

    Path file( final String name )
    {
        return directory.resolve( name );
    }

    void run( final List<String> operation, final Path result ) throws IOException
    {
        final String command = "embedded".equals( executable ) ? EmbeddedImageMagick.executable().toString() : executable;
        if ( deadline == 0 )
        {
            deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos( timeoutSeconds );
        }
        final long remaining = deadline - System.nanoTime();
        if ( remaining <= 0 )
        {
            throw new IOException( "Image processing exceeded " + timeoutSeconds + " seconds" );
        }
        final List<String> arguments = new ArrayList<>( List.of( command,
            "-limit", "thread", "1", "-limit", "memory", "256MiB", "-limit", "map", "0", "-limit", "disk", "0",
            "-limit", "list-length", "16", "-limit", "time",
            Long.toString( Math.max( 1, TimeUnit.NANOSECONDS.toSeconds( remaining ) ) ), "-define", "heic:max-threads=1" ) );
        arguments.addAll( operation );
        final ProcessBuilder builder = new ProcessBuilder( arguments );
        if ( "embedded".equals( executable ) && System.getProperty( "os.name" ).startsWith( "Mac" ) )
        {
            final Path root = Path.of( command ).getParent().getParent();
            builder.environment().put( "MAGICK_HOME", root.toString() );
            builder.environment().put( "MAGICK_CONFIGURE_PATH", root.resolve( "etc/ImageMagick-7" ).toString() );
            builder.environment().put( "LIBHEIF_PLUGIN_PATH", root.resolve( "lib/libheif" ).toString() );
        }
        final String existing = builder.environment().get( "MAGICK_CONFIGURE_PATH" );
        builder.environment().put( "MAGICK_CONFIGURE_PATH", directory + ( existing == null ? "" : File.pathSeparator + existing ) );
        builder.environment().put( "MAGICK_TEMPORARY_PATH", directory.toString() );
        final Process process = builder.directory( directory.toFile() ).redirectOutput( ProcessBuilder.Redirect.DISCARD )
            .redirectError( ProcessBuilder.Redirect.DISCARD ).start();
        try
        {
            process.getOutputStream().close();
            if ( !process.waitFor( remaining, TimeUnit.NANOSECONDS ) )
            {
                throw new IOException( "Image processing exceeded " + timeoutSeconds + " seconds" );
            }
            if ( process.exitValue() != 0 || !Files.isRegularFile( result ) || Files.size( result ) == 0 )
            {
                throw new IOException( "Image processing failed (exit " + process.exitValue() + ")" );
            }
        }
        catch ( InterruptedException e )
        {
            Thread.currentThread().interrupt();
            throw new IOException( "Image processing interrupted", e );
        }
        finally
        {
            stop( process );
        }
    }

    static void stop( final Process process )
    {
        if ( process != null && process.isAlive() )
        {
            process.descendants().forEach( ProcessHandle::destroyForcibly );
            process.destroyForcibly();
            boolean interrupted = Thread.interrupted();
            try
            {
                while ( process.isAlive() )
                {
                    try { process.waitFor(); }
                    catch ( InterruptedException e ) { interrupted = true; }
                }
            }
            finally
            {
                if ( interrupted ) { Thread.currentThread().interrupt(); }
            }
        }
    }

    @Override
    public void close() throws IOException
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
