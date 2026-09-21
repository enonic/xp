package com.enonic.xp.core.impl.image;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;

import com.enonic.im4j.BundledImageMagick;
import com.enonic.im4j.ExternalImageMagick;
import com.enonic.im4j.ImageMagick;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class ImageMagickTestSupport
{
    static final long MAX_DISK_BYTES = 4L * 1024 * 1024 * 1024;

    private Path installationStorage;

    ImageMagick imageMagick;

    /**
     * Runs the suite against the bundled distribution, or against an externally installed
     * ImageMagick when {@code -Dim4j.external=<path>} names one. The second form is an
     * acceptance harness: it answers whether a candidate build supports every operation XP
     * performs, by running XP's own encoder, decoder and transformer against it.
     */
    @BeforeAll
    void startImageMagick() throws IOException
    {
        final String external = System.getProperty( "im4j.external", "" ).trim();
        if ( external.isEmpty() )
        {
            installationStorage = Files.createTempDirectory( "image-test-installation-" );
            imageMagick = new BundledImageMagick( installationStorage );
        }
        else
        {
            imageMagick = new ExternalImageMagick( Path.of( external ) );
        }
    }

    @AfterAll
    void stopImageMagick() throws IOException
    {
        if ( installationStorage == null )
        {
            return;
        }
        ( (BundledImageMagick) imageMagick ).close();
        Files.delete( installationStorage );
    }

    static ImageMagick external( final String command )
    {
        return () -> new ImageMagick.Installation()
        {
            @Override
            public Path executable() { return Path.of( command ).toAbsolutePath(); }

            @Override
            public Map<String, String> environment() { return Map.of(); }

            @Override
            public void close() {}
        };
    }
}
