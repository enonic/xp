package com.enonic.xp.core.impl.image.im;

import java.io.IOException;

import org.jspecify.annotations.NullMarked;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

import com.enonic.im4j.BundledImageMagick;
import com.enonic.im4j.ImageMagick;

/** Publishes the bundled ImageMagick installation as an OSGi service. */
@NullMarked
@Component(service = ImageMagick.class)
public final class ImageMagickComponent
    implements ImageMagick
{
    private final BundledImageMagick delegate;

    /**
     * Creates the service using the bundle's private data area.
     *
     * @param context the owning bundle context
     * @throws IllegalStateException if the framework has no bundle data area
     */
    @Activate
    public ImageMagickComponent( final BundleContext context )
    {
        final var data = context.getDataFile( "imagemagick" );
        if ( data == null )
        {
            throw new IllegalStateException( "ImageMagick requires a bundle data area" );
        }
        this.delegate = new BundledImageMagick( data.toPath() );
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
        delegate.close();
    }
}
