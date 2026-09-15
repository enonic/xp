package com.enonic.xp.core.impl.image;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.jspecify.annotations.NullMarked;

import com.enonic.im4j.ImageMagick;
import com.enonic.im4j.ImageMagickProcess;

/** Owns transformed raw rasters until the next backend has consumed them. */
@NullMarked
final class ImageMagickTransformer
{
    private final ImageMagick imageMagick;
    private final int timeoutSeconds;
    private final Path temporaryFolder;
    private final long maxDiskBytes;

    ImageMagickTransformer( final ImageMagick imageMagick, final int timeoutSeconds, final Path temporaryFolder,
                           final long maxDiskBytes )
    {
        if ( timeoutSeconds < 1 )
        {
            throw new IllegalArgumentException( "Invalid image transformer configuration" );
        }
        this.imageMagick = imageMagick;
        this.timeoutSeconds = timeoutSeconds;
        this.temporaryFolder = temporaryFolder;
        this.maxDiskBytes = maxDiskBytes;
    }

    BufferedImage apply( final BufferedImage source, final ImageMagickTransformPlan plan ) throws IOException
    {
        try (var result = transform( source, plan )) { return result.raster().read(); }
    }

    Result transform( final BufferedImage source, final ImageMagickTransformPlan plan ) throws IOException
    {
        final ImageMagickProcess process = process();
        try { return transform( process, NativeImageRaster.write( source, process.file( "input.rgba" ) ), plan ); }
        catch ( IOException | RuntimeException | Error e ) { process.close(); throw e; }
    }

    Result transform( final NativeImageRaster source, final ImageMagickTransformPlan plan ) throws IOException
    {
        final ImageMagickProcess process = process();
        try { return transform( process, source, plan ); }
        catch ( IOException | RuntimeException | Error e ) { process.close(); throw e; }
    }

    private ImageMagickProcess process() throws IOException
    {
        return new ImageMagickProcess( imageMagick, temporaryFolder, "transform", timeoutSeconds, "RGBA", "RGBA", maxDiskBytes );
    }

    private Result transform( final ImageMagickProcess process, final NativeImageRaster source,
                              final ImageMagickTransformPlan plan ) throws IOException
    {
        final Path output = process.file( "output.rgba" );
        final List<String> operation = new ArrayList<>( source.inputArguments() );
        operation.addAll( List.of( "-strip", "-alpha", "on", "-virtual-pixel", "edge" ) );
        operation.addAll( plan.operations() );
        operation.addAll( NativeImageRaster.outputArguments( output, plan.alpha() ) );
        process.run( operation, output );
        return new Result( process, NativeImageRaster.validate( output, plan.width(), plan.height(), plan.alpha() ) );
    }

    record Result(ImageMagickProcess process, NativeImageRaster raster) implements AutoCloseable
    {
        @Override
        public void close() throws IOException { process.close(); }
    }
}
