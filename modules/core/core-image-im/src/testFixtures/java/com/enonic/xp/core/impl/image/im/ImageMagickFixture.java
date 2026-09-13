package com.enonic.xp.core.impl.image.im;

import java.io.IOException;
import java.nio.file.Path;

import org.jspecify.annotations.NullMarked;

import com.enonic.xp.core.internal.image.ImageMagick;

/** Runs the production installation lifecycle without starting an OSGi framework. */
@NullMarked
public final class ImageMagickFixture implements ImageMagick, AutoCloseable
{
    private final ImageMagickService service;

    /**
     * Creates an installation fixture.
     * @param storage the private test storage directory
     */
    public ImageMagickFixture( final Path storage )
    {
        service = new ImageMagickService( storage, platform(), ImageMagickService.class::getResourceAsStream );
    }

    /**
     * Returns the platform resource name.
     * @return the normalized operating system and architecture
     */
    public static String platform()
    {
        return ImageMagickService.platform( System.getProperty( "os.name" ), System.getProperty( "os.arch" ) );
    }

    /**
     * Acquires the fixture installation.
     * @return an installation handle
     * @throws IOException if installation fails or the fixture is closed
     */
    @Override
    public Installation acquire() throws IOException { return service.acquire(); }

    /**
     * Stops the fixture and removes unused installation files.
     * @throws IOException if cleanup fails
     */
    @Override
    public void close() throws IOException { service.deactivate(); }
}
