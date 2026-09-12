package com.enonic.xp.core.impl.image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

/** Runs an admitted transformation plan against a generated PNG in a bounded native process. */
final class ImageMagickTransformer
{
    private final String executable;

    private final int timeoutSeconds;

    private final Path temporaryFolder;

    ImageMagickTransformer( final String executable, final int timeoutSeconds, final Path temporaryFolder )
    {
        if ( executable == null || executable.isBlank() || timeoutSeconds < 1 )
        {
            throw new IllegalArgumentException( "Invalid image transformer configuration" );
        }
        this.executable = executable;
        this.timeoutSeconds = timeoutSeconds;
        this.temporaryFolder = temporaryFolder;
    }

    BufferedImage apply( final BufferedImage source, final ImageMagickTransformPlan plan )
        throws IOException
    {
        Files.createDirectories( temporaryFolder );
        final Path directory = Files.createTempDirectory( temporaryFolder, "transform-" ).toAbsolutePath();
        final Path input = directory.resolve( "input.png" );
        final Path result = directory.resolve( "output.png" );
        Process process = null;
        try
        {
            if ( !ImageIO.write( source, "png", input.toFile() ) )
            {
                throw new IOException( "PNG writer is unavailable" );
            }
            Files.writeString( directory.resolve( "policy.xml" ), "<policymap>" +
                "<policy domain=\"delegate\" rights=\"none\" pattern=\"*\"/>" +
                "<policy domain=\"filter\" rights=\"none\" pattern=\"*\"/>" +
                "<policy domain=\"coder\" rights=\"none\" pattern=\"*\"/>" +
                "<policy domain=\"coder\" rights=\"read|write\" pattern=\"{PNG,PNG24,PNG32}\"/>" +
                "<policy domain=\"path\" rights=\"none\" pattern=\"@*\"/>" +
                "<policy domain=\"system\" name=\"max-memory-request\" value=\"256MiB\"/>" +
                "</policymap>" );
            final String command = "embedded".equals( executable ) ? EmbeddedImageMagick.executable().toString() : executable;
            final List<String> arguments = new ArrayList<>( List.of( command,
                "-limit", "thread", "1", "-limit", "memory", "256MiB", "-limit", "map", "0", "-limit", "disk", "0",
                "-limit", "list-length", "16", "-limit", "time", Integer.toString( timeoutSeconds ),
                "PNG:" + input, "-strip", "-alpha", "on", "-virtual-pixel", "edge" ) );
            arguments.addAll( plan.operations() );
            arguments.addAll( List.of( "-depth", "8", ( plan.alpha() ? "PNG32:" : "PNG24:" ) + result ) );
            final ProcessBuilder builder = ImageMagickEncoder.processBuilder( arguments, "embedded".equals( executable ) );
            final String existing = builder.environment().get( "MAGICK_CONFIGURE_PATH" );
            builder.environment().put( "MAGICK_CONFIGURE_PATH", directory + ( existing == null ? "" : File.pathSeparator + existing ) );
            builder.environment().put( "MAGICK_TEMPORARY_PATH", directory.toString() );
            process = builder.directory( directory.toFile() ).redirectOutput( ProcessBuilder.Redirect.DISCARD )
                .redirectError( ProcessBuilder.Redirect.DISCARD ).start();
            process.getOutputStream().close();
            if ( !process.waitFor( timeoutSeconds, TimeUnit.SECONDS ) )
            {
                throw new IOException( "Image transformation exceeded " + timeoutSeconds + " seconds" );
            }
            if ( process.exitValue() != 0 || !Files.isRegularFile( result ) || Files.size( result ) == 0 )
            {
                throw new IOException( "Image transformer failed (exit " + process.exitValue() + ")" );
            }
            try (var stream = ImageIO.createImageInputStream( result.toFile() ))
            {
                final var readers = ImageIO.getImageReaders( stream );
                if ( !readers.hasNext() )
                {
                    throw new IOException( "Image transformer produced an invalid raster" );
                }
                final var reader = readers.next();
                try
                {
                    reader.setInput( stream );
                    if ( reader.getWidth( 0 ) != plan.width() || reader.getHeight( 0 ) != plan.height() )
                    {
                        throw new IOException( "Unexpected transformed image dimensions" );
                    }
                    return reader.read( 0 );
                }
                finally
                {
                    reader.dispose();
                }
            }
        }
        catch ( InterruptedException e )
        {
            Thread.currentThread().interrupt();
            throw new IOException( "Image transformation interrupted", e );
        }
        finally
        {
            ImageMagickEncoder.stop( process );
            try (var paths = Files.walk( directory ))
            {
                for ( Path path : paths.sorted( Comparator.reverseOrder() ).toList() )
                {
                    Files.deleteIfExists( path );
                }
            }
        }
    }
}
