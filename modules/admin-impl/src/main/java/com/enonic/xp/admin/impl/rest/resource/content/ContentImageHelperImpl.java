package com.enonic.xp.admin.impl.rest.resource.content;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import org.osgi.service.component.annotations.Component;

import com.google.common.io.ByteSource;

import com.enonic.xp.admin.impl.rest.resource.BaseImageHelperImpl;
import com.enonic.xp.image.ImageHelper;
import com.enonic.xp.image.filter.ScaleMaxFunction;
import com.enonic.xp.image.filter.ScaleSquareFunction;
import com.enonic.xp.media.ImageOrientation;
import com.enonic.xp.util.Exceptions;

@Component
public final class ContentImageHelperImpl
    extends BaseImageHelperImpl
    implements ContentImageHelper
{

    public BufferedImage readAndRotateImage( final ByteSource blob, final ImageParams imageParams )
    {
        final BufferedImage image = readImage( blob, imageParams );
        return rotateImage( imageParams.getOrientation(), image );
    }

    public BufferedImage readImage( final ByteSource blob, final ImageParams imageParams )
    {
        BufferedImage image;
        try (final InputStream inputStream = blob.openStream())
        {
            image = readImage( inputStream, imageParams );
        }
        catch ( IOException e )
        {
            throw Exceptions.unchecked( e );
        }

        return image;
    }

    private BufferedImage readImage( final InputStream inputStream, final ImageParams imageParams )
    {
        final BufferedImage image = ImageHelper.toBufferedImage( inputStream );
        if ( imageParams.getSize() > 0 && ( image.getWidth() >= imageParams.getSize() ) )
        {
            if ( imageParams.isCropRequired() )
            {
                return new ScaleSquareFunction( imageParams.getSize() ).scale( image );
            }
            else
            {
                return new ScaleMaxFunction( imageParams.getSize() ).scale( image );
            }
        }
        else
        {
            return image;
        }
    }

    private BufferedImage rotateImage( final ImageOrientation orientation, final BufferedImage source )
    {
        if ( orientation == ImageOrientation.TopLeft )
        {
            return source;
        }

        final AffineTransform transform = new AffineTransform();
        int resultWidth = source.getWidth();
        int resultHeight = source.getHeight();

        switch ( orientation )
        {
            case TopRight: // Flip X
                transform.scale( -1.0, 1.0 );
                transform.translate( -resultWidth, 0 );
                break;
            case BottomRight: // PI rotation
                transform.translate( resultWidth, resultHeight );
                transform.rotate( Math.PI );
                break;
            case BottomLeft: // Flip Y
                transform.scale( 1.0, -1.0 );
                transform.translate( 0, -resultHeight );
                break;
            case LeftTop: // - PI/2 and Flip X
                transform.rotate( -Math.PI / 2 );
                transform.scale( -1.0, 1.0 );
                resultWidth = source.getHeight();
                resultHeight = source.getWidth();
                break;
            case RightTop: // -PI/2 and -width
                transform.translate( resultHeight, 0 );
                transform.rotate( Math.PI / 2 );
                resultWidth = source.getHeight();
                resultHeight = source.getWidth();
                break;
            case RightBottom: // PI/2 and Flip
                transform.scale( -1.0, 1.0 );
                transform.translate( -resultHeight, 0 );
                transform.translate( 0, resultWidth );
                transform.rotate( 3 * Math.PI / 2 );
                resultWidth = source.getHeight();
                resultHeight = source.getWidth();
                break;
            case LeftBottom: // PI / 2
                transform.translate( 0, resultWidth );
                transform.rotate( 3 * Math.PI / 2 );
                resultWidth = source.getHeight();
                resultHeight = source.getWidth();
                break;
            default:
                return source;

        }
        final BufferedImage destinationImage = new BufferedImage( resultWidth, resultHeight, source.getType() );
        final AffineTransformOp op = new AffineTransformOp( transform, AffineTransformOp.TYPE_BICUBIC );
        return op.filter( source, destinationImage );
    }

}
