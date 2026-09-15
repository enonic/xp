package com.enonic.xp.core.impl.image;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;

import com.enonic.im4j.BundledImageMagick;
import com.enonic.im4j.ImageMagick;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class ImageMagickTestSupport
{
    static final long MAX_DISK_BYTES = 4L * 1024 * 1024 * 1024;

    private Path installationStorage;

    BundledImageMagick imageMagick;

    @BeforeAll
    void startImageMagick() throws IOException
    {
        installationStorage = Files.createTempDirectory( "image-test-installation-" );
        imageMagick = new BundledImageMagick( installationStorage );
    }

    @AfterAll
    void stopImageMagick() throws IOException
    {
        imageMagick.close();
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
