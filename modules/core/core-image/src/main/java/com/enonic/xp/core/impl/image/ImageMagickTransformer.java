package com.enonic.xp.core.impl.image;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/** Owns transformed PNG files until the next backend has consumed them. */
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

    BufferedImage apply( final BufferedImage source, final ImageMagickTransformPlan plan ) throws IOException
    {
        try (var result = transform( source, plan )) { return result.raster().read(); }
    }

    Result transform( final BufferedImage source, final ImageMagickTransformPlan plan ) throws IOException
    {
        final NativeImageProcess process = process();
        try { return transform( process, NativeImageRaster.write( source, process.file( "input.png" ) ), plan ); }
        catch ( IOException | RuntimeException | Error e ) { process.close(); throw e; }
    }

    Result transform( final NativeImageRaster source, final ImageMagickTransformPlan plan ) throws IOException
    {
        final NativeImageProcess process = process();
        try { return transform( process, source, plan ); }
        catch ( IOException | RuntimeException | Error e ) { process.close(); throw e; }
    }

    private NativeImageProcess process() throws IOException
    {
        return new NativeImageProcess( executable, temporaryFolder, "transform", timeoutSeconds, "PNG", "PNG24,PNG32" );
    }

    private Result transform( final NativeImageProcess process, final NativeImageRaster source,
                              final ImageMagickTransformPlan plan ) throws IOException
    {
        final Path output = process.file( "output.png" );
        final List<String> operation = new ArrayList<>( List.of( "PNG:" + source.path(),
            "-strip", "-alpha", "on", "-virtual-pixel", "edge" ) );
        operation.addAll( plan.operations() );
        operation.addAll( List.of( "-depth", "8", ( plan.alpha() ? "PNG32:" : "PNG24:" ) + output ) );
        process.run( operation, output );
        return new Result( process, NativeImageRaster.validate( output, plan.width(), plan.height() ) );
    }

    record Result(NativeImageProcess process, NativeImageRaster raster) implements AutoCloseable
    {
        @Override
        public void close() throws IOException { process.close(); }
    }
}
