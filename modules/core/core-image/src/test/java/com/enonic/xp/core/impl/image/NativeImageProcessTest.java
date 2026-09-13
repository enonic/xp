package com.enonic.xp.core.impl.image;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NativeImageProcessTest extends ImageMagickTestSupport
{
    @TempDir
    Path temporaryFolder;

    @Test
    void spillsPixelCacheWithinDiskLimitAndCleansUp() throws Exception
    {
        final Path source = temporaryFolder.resolve( "source.png" );
        ImageIO.write( new BufferedImage( 64, 48, BufferedImage.TYPE_INT_RGB ), "png", source.toFile() );
        final Path work = temporaryFolder.resolve( "work" );
        for ( long disk : new long[]{0, 1_048_576} )
        {
            try (var process = new NativeImageProcess( imageMagick, work, "spill", 30, "PNG", "RGBA", disk ))
            {
                final Path output = process.file( "output.rgba" );
                final var operation = new ArrayList<>( List.of( "-limit", "memory", "1", "PNG:" + source,
                    "-resize", "32x24!" ) );
                operation.addAll( NativeImageRaster.outputArguments( output, false ) );
                if ( disk == 0 )
                {
                    assertThrows( IOException.class, () -> process.run( operation, output ) );
                }
                else
                {
                    process.run( operation, output );
                    final var raster = NativeImageRaster.validate( output, 32, 24, false );
                    assertEquals( 0xff000000, raster.read().getRGB( 16, 12 ) );
                }
            }
            try (var files = Files.list( work )) { assertEquals( 0, files.count() ); }
        }
    }
}
