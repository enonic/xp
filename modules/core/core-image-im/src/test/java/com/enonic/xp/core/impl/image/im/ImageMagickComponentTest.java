package com.enonic.xp.core.impl.image.im;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.enonic.im4j.ImageMagick;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ImageMagickComponentTest
{
    @TempDir
    Path directory;

    @Test
    void usesTheBundledInstallationWhenNothingIsConfigured()
    {
        final ImageMagick bundled = bundled();

        assertSame( bundled, ImageMagickComponent.select( config( "" ), bundled ) );
        assertSame( bundled, ImageMagickComponent.select( config( "   " ), bundled ) );
    }

    @Test
    void usesTheConfiguredExecutableWhenOneIsSet()
        throws Exception
    {
        final Path executable = Files.writeString( directory.resolve( "magick" ), "#!/bin/sh\nexit 0\n" );
        assertTrue( executable.toFile().setExecutable( true, true ) );

        final ImageMagick selected = ImageMagickComponent.select( config( executable.toString() ), bundled() );

        try (var handle = selected.acquire())
        {
            assertEquals( executable.toAbsolutePath(), handle.executable() );
        }
    }

    @Test
    void registersWithAnUnusableConfiguredPathAndFailsOnlyOnAcquire()
    {
        // A mandatory reference binds this service, so refusing to register would stop XP
        // serving any image at all. A bad path must surface at acquire() instead.
        final Path missing = directory.resolve( "absent" );

        final ImageMagick selected = ImageMagickComponent.select( config( missing.toString() ), bundled() );

        final IOException e = assertThrows( IOException.class, selected::acquire );
        assertTrue( e.getMessage().contains( missing.toString() ) );
    }

    @Test
    void convertsThroughAnExternallyInstalledImageMagick()
        throws Exception
    {
        final Path magick = Path.of( "/opt/homebrew/bin/magick" );
        Assumptions.assumeTrue( Files.isExecutable( magick ), "no Homebrew ImageMagick on this machine" );

        final ImageMagick selected = ImageMagickComponent.select( config( magick.toString() ), bundled() );

        try (var handle = selected.acquire())
        {
            assertEquals( magick, handle.executable() );
            assertTrue( handle.environment().isEmpty() );
        }
    }

    private static ImageMagick bundled()
    {
        return () -> new ImageMagick.Installation()
        {
            @Override
            public Path executable()
            {
                return Path.of( "bundled" );
            }

            @Override
            public Map<String, String> environment()
            {
                return Map.of();
            }

            @Override
            public void close()
            {
            }
        };
    }

    private static ImageMagickConfig config( final String executable )
    {
        return new ImageMagickConfig()
        {
            @Override
            public String imagemagick_executable()
            {
                return executable;
            }

            @Override
            public Class<? extends Annotation> annotationType()
            {
                return ImageMagickConfig.class;
            }
        };
    }
}
