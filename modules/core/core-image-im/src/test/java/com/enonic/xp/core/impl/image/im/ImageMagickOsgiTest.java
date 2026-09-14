package com.enonic.xp.core.impl.image.im;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.felix.framework.FrameworkFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;

import com.enonic.xp.core.internal.image.ImageMagick;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ImageMagickOsgiTest
{
    @TempDir
    Path storage;

    @Test
    void packagedBundleActivatesAndCleansUpAcrossRestart() throws Exception
    {
        final var framework = new FrameworkFactory().newFramework( Map.of(
            Constants.FRAMEWORK_STORAGE, storage.resolve( "felix" ).toString(),
            Constants.FRAMEWORK_SYSTEMPACKAGES_EXTRA,
            "com.enonic.xp.core.internal.image;version=" + System.getProperty( "imagemagick.api.version" ) +
                ",org.jspecify.annotations;version=1.0.0" ) );
        framework.start();
        try
        {
            final BundleContext context = framework.getBundleContext();
            for ( String file : System.getProperty( "imagemagick.osgi.bundles" ).split( Pattern.quote( File.pathSeparator ) ) )
            {
                context.installBundle( Path.of( file ).toUri().toString() );
            }
            for ( Bundle bundle : context.getBundles() )
            {
                if ( bundle.getBundleId() != 0 ) { bundle.start(); }
            }
            final Bundle bundle = context.installBundle( Path.of( System.getProperty( "imagemagick.bundle" ) ).toUri().toString() );
            bundle.start();
            final ServiceReference<ImageMagick> reference = reference( context );
            final ImageMagick service = context.getService( reference );
            assertNotNull( service );
            final Path previous;
            try (var handle = service.acquire())
            {
                previous = handle.executable();
                assertTrue( Files.isExecutable( previous ) );
                assertTrue( previous.startsWith( storage ) );
                bundle.stop();
                assertTrue( Files.exists( previous ) );
                assertThrows( IOException.class, service::acquire );
            }
            assertFalse( Files.exists( previous ) );
            context.ungetService( reference );
            bundle.start();
            final var nextReference = reference( context );
            final var next = context.getService( nextReference );
            assertNotNull( next );
            try (var handle = next.acquire())
            {
                assertFalse( previous.equals( handle.executable() ) );
                assertTrue( Files.isExecutable( handle.executable() ) );
            }
            bundle.stop();
            context.ungetService( nextReference );
        }
        finally
        {
            framework.stop();
            framework.waitForStop( 10_000 );
        }
    }

    private static ServiceReference<ImageMagick> reference( final BundleContext context )
    {
        return assertTimeoutPreemptively( Duration.ofSeconds( 10 ), () -> {
            ServiceReference<ImageMagick> reference;
            while ( ( reference = context.getServiceReference( ImageMagick.class ) ) == null ) { Thread.sleep( 10 ); }
            return reference;
        } );
    }
}
