package com.enonic.xp.core.impl.image;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.ByteSink;
import com.google.common.io.ByteSource;

import com.enonic.xp.image.Cropping;
import com.enonic.xp.image.ScaleParams;
import com.enonic.xp.media.ImageOrientation;

class ExternalImageProcessor
{
    private static final Logger LOG = LoggerFactory.getLogger( ExternalImageProcessor.class );

    private static final Set<String> SUPPORTED_FORMATS = Set.of( "png", "jpeg", "gif", "webp" );

    private final String executablePath;

    ExternalImageProcessor( final String executablePath )
    {
        this.executablePath = executablePath;
    }

    boolean supportsFormat( final String format )
    {
        return SUPPORTED_FORMATS.contains( format );
    }

    void processImage( final ByteSource source, final ImageProcessingInstruction instruction, final ByteSink sink )
        throws IOException
    {
        final Path tempDir = Files.createTempDirectory( "xp-image-" );
        try
        {
            final Path inputFile = tempDir.resolve( "input" );
            final String outputExtension = mapFormatToExtension( instruction.getFormat() );
            final Path outputFile = tempDir.resolve( "output." + outputExtension );

            try (InputStream is = source.openStream(); OutputStream os = Files.newOutputStream( inputFile ))
            {
                is.transferTo( os );
            }

            final List<String> command = buildCommand( inputFile, outputFile, instruction );

            LOG.debug( "Executing external image processor: {}", command );

            final ProcessBuilder processBuilder = new ProcessBuilder( command );
            processBuilder.redirectErrorStream( true );
            final Process process = processBuilder.start();

            final String processOutput;
            try (InputStream processInputStream = process.getInputStream())
            {
                processOutput = new String( processInputStream.readAllBytes() );
            }

            final int exitCode;
            try
            {
                exitCode = process.waitFor();
            }
            catch ( InterruptedException e )
            {
                Thread.currentThread().interrupt();
                throw new IOException( "External image processor interrupted", e );
            }

            if ( exitCode != 0 )
            {
                throw new IOException(
                    "External image processor failed with exit code " + exitCode + ": " + processOutput );
            }

            try (InputStream is = Files.newInputStream( outputFile ); OutputStream os = sink.openBufferedStream())
            {
                is.transferTo( os );
            }
        }
        finally
        {
            deleteDirectory( tempDir );
        }
    }

    private List<String> buildCommand( final Path inputFile, final Path outputFile,
                                       final ImageProcessingInstruction instruction )
    {
        final List<String> command = new ArrayList<>();
        command.add( this.executablePath );

        command.add( inputFile.toAbsolutePath().toString() );

        final ScaleParams scaleParams = instruction.getScaleParams();
        if ( !ScaleParams.NO_SCALE.getName().equals( scaleParams.getName() ) )
        {
            addScaleArgs( command, scaleParams );
        }

        if ( !instruction.getCropping().isUnmodified() )
        {
            addCropArgs( command, instruction.getCropping() );
        }

        if ( instruction.getOrientation() != ImageOrientation.TopLeft )
        {
            command.add( "--rotate" );
        }

        final String outputSuffix = buildOutputSuffix( instruction );
        command.add( "-o" );
        command.add( outputFile.toAbsolutePath() + outputSuffix );

        return command;
    }

    private static void addScaleArgs( final List<String> command, final ScaleParams scaleParams )
    {
        final Object[] args = scaleParams.getArguments();

        switch ( scaleParams.getName() )
        {
            case "width":
                if ( args.length > 0 )
                {
                    command.add( "--size" );
                    command.add( String.valueOf( args[0] ) );
                }
                break;
            case "height":
                if ( args.length > 0 )
                {
                    command.add( "--size" );
                    command.add( "x" + args[0] );
                }
                break;
            case "max":
                if ( args.length > 0 )
                {
                    command.add( "--size" );
                    command.add( String.valueOf( args[0] ) );
                    command.add( "--no-rotate" );
                }
                break;
            case "square":
                if ( args.length > 0 )
                {
                    command.add( "--size" );
                    command.add( args[0] + "x" + args[0] );
                    command.add( "--smartcrop" );
                    command.add( "attention" );
                }
                break;
            case "block":
                if ( args.length >= 2 )
                {
                    command.add( "--size" );
                    command.add( args[0] + "x" + args[1] );
                    command.add( "--smartcrop" );
                    command.add( "attention" );
                }
                break;
            case "wide":
                if ( args.length >= 2 )
                {
                    command.add( "--size" );
                    command.add( args[0] + "x" + args[1] );
                }
                break;
            default:
                LOG.warn( "Unsupported scale type for external processor: {}", scaleParams.getName() );
        }
    }

    private static void addCropArgs( final List<String> command, final Cropping cropping )
    {
        // vipsthumbnail doesn't directly support arbitrary cropping with coordinates,
        // so we use smartcrop for basic crop support
        if ( !cropping.isUnmodified() )
        {
            command.add( "--smartcrop" );
            command.add( "attention" );
        }
    }

    private static String buildOutputSuffix( final ImageProcessingInstruction instruction )
    {
        final StringBuilder sb = new StringBuilder();
        sb.append( "[" );

        final int quality = instruction.getQuality();
        if ( quality > 0 )
        {
            sb.append( "Q=" ).append( quality );
        }

        sb.append( ",strip" );

        sb.append( "]" );
        return sb.toString();
    }

    private static String mapFormatToExtension( final String format )
    {
        return switch ( format )
        {
            case "jpeg" -> "jpg";
            default -> format;
        };
    }

    private static void deleteDirectory( final Path dir )
    {
        try
        {
            Files.walk( dir ).sorted( java.util.Comparator.reverseOrder() ).forEach( path -> {
                try
                {
                    Files.deleteIfExists( path );
                }
                catch ( IOException e )
                {
                    LOG.debug( "Failed to delete temp file: {}", path, e );
                }
            } );
        }
        catch ( IOException e )
        {
            LOG.debug( "Failed to clean up temp directory: {}", dir, e );
        }
    }
}
