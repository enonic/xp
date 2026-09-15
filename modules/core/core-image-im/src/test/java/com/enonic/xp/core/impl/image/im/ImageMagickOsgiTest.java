package com.enonic.xp.core.impl.image.im;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
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

import com.enonic.im4j.ImageMagick;

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
            Constants.FRAMEWORK_SYSTEMPACKAGES_EXTRA, "org.jspecify.annotations;version=1.0.0" ) );
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
            for ( String file : System.getProperty( "imagemagick.im4j.bundles" ).split( Pattern.quote( File.pathSeparator ) ) )
            {
                context.installBundle( Path.of( file ).toUri().toString() );
            }
            final Bundle bundle = context.installBundle( Path.of( System.getProperty( "imagemagick.bundle" ) ).toUri().toString() );
            bundle.start();
            // The bundle under test imports com.enonic.im4j from the im4j bundle, not from the system bundle, so
            // only a context wired to that import (the tested bundle's own) can see the service; the framework's
            // context is used only for ungetService, which does not require class-space visibility.
            final String className = ImageMagick.class.getName();
            final ServiceReference<?> reference = reference( bundle.getBundleContext(), className );
            final ImageMagick service = bridge( bundle.getBundleContext().getService( reference ), ImageMagick.class );
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
            final ServiceReference<?> nextReference = reference( bundle.getBundleContext(), className );
            final ImageMagick next = bridge( bundle.getBundleContext().getService( nextReference ), ImageMagick.class );
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

    private static ServiceReference<?> reference( final BundleContext context, final String className )
    {
        return assertTimeoutPreemptively( Duration.ofSeconds( 10 ), () -> {
            ServiceReference<?> reference;
            while ( ( reference = context.getServiceReference( className ) ) == null ) { Thread.sleep( 10 ); }
            return reference;
        } );
    }

    /**
     * Adapts an object loaded by the im4j bundle's own class loader to this test's copy of the same-named
     * interface, so the rest of the test can use the ordinary, statically typed API.
     */
    private static <T> T bridge( final Object target, final Class<T> type )
    {
        final Object proxy = Proxy.newProxyInstance( type.getClassLoader(), new Class<?>[]{ type }, ( p, method, args ) -> {
            try
            {
                final var real = target.getClass().getMethod( method.getName(), method.getParameterTypes() );
                real.setAccessible( true );
                final Object result = real.invoke( target, args );
                final Class<?> returnType = method.getReturnType();
                return returnType.isInterface() && result != null && !returnType.isInstance( result )
                    ? bridge( result, returnType )
                    : result;
            }
            catch ( InvocationTargetException e )
            {
                throw e.getCause();
            }
        } );
        return type.cast( proxy );
    }
}
