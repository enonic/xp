package com.enonic.xp.core.impl.image.im;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.jspecify.annotations.NullMarked;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.im4j.BundledImageMagick;
import com.enonic.im4j.ExternalImageMagick;
import com.enonic.im4j.ImageMagick;

/**
 * Publishes an ImageMagick installation as an OSGi service, preferring an externally configured
 * executable over the bundled distribution.
 */
@NullMarked
@Component(service = ImageMagick.class, configurationPid = "com.enonic.xp.image")
public final class ImageMagickComponent
    implements ImageMagick
{
    private static final Logger LOG = LoggerFactory.getLogger( ImageMagickComponent.class );

    private final ImageMagick delegate;

    private final BundledImageMagick bundled;

    /**
     * Creates the service, using the configured executable when one is set and the bundle's
     * private data area otherwise. Registration never fails on an unusable installation:
     * consumers hold a mandatory reference to this service, so refusing to register would stop
     * XP serving any image at all, including through ImageIO.
     *
     * @param context the owning bundle context
     * @param config the image configuration
     * @throws IllegalStateException if the framework has no bundle data area
     */
    @Activate
    public ImageMagickComponent( final BundleContext context, final ImageMagickConfig config )
    {
        final var data = context.getDataFile( "imagemagick" );
        if ( data == null )
        {
            throw new IllegalStateException( "ImageMagick requires a bundle data area" );
        }
        this.bundled = new BundledImageMagick( data.toPath() );
        this.delegate = select( config, bundled );
    }

    /**
     * Selects the configured installation, falling back to the bundled one.
     *
     * @param config the image configuration
     * @param bundled the bundled installation
     * @return the installation to publish
     */
    static ImageMagick select( final ImageMagickConfig config, final ImageMagick bundled )
    {
        final String configured = config.imagemagick_executable().trim();
        if ( configured.isEmpty() )
        {
            return bundled;
        }
        final Path executable = Path.of( configured ).toAbsolutePath();
        if ( !Files.isRegularFile( executable ) || !Files.isExecutable( executable ) )
        {
            LOG.warn( "Configured ImageMagick is not an executable file: {}. Native image processing will fail until this is corrected.",
                      executable );
        }
        return new ExternalImageMagick( executable );
    }

    @Override
    public Installation acquire()
        throws IOException
    {
        return delegate.acquire();
    }

    /**
     * Stops accepting new handles. Existing handles retain their installation until closed.
     *
     * @throws IOException if an unused installation cannot be removed
     */
    @Deactivate
    public void deactivate()
        throws IOException
    {
        bundled.close();
    }
}
