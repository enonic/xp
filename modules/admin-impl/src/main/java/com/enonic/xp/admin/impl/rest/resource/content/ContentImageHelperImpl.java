package com.enonic.xp.admin.impl.rest.resource.content;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import com.enonic.xp.media.MediaInfoService;

import com.google.common.io.ByteSource;

import com.enonic.xp.admin.impl.rest.resource.BaseImageHelperImpl;
import com.enonic.xp.image.ImageHelper;
import com.enonic.xp.image.filter.ScaleMaxFilter;
import com.enonic.xp.image.filter.ScaleSquareFilter;
import com.enonic.xp.util.Exceptions;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component
public final class ContentImageHelperImpl
    extends BaseImageHelperImpl
    implements ContentImageHelper
{

    private MediaInfoService mediaInfoService;


    public BufferedImage readImage( final ByteSource blob, final int size, final ImageFilter imageFilter )
    {
        BufferedImage image;
        try (final InputStream inputStream = blob.openStream())
        {
            image = readImage( inputStream, size, imageFilter );
        }
        catch ( IOException e )
        {
            throw Exceptions.unchecked( e );
        }
        Integer orientation = mediaInfoService.getOrientation( blob );
        return ( orientation <= 1 ) ? // check image is need to be rotated
            image : this.rotateImage( orientation, image );
    }

    private BufferedImage readImage( final InputStream inputStream, final int size, final ImageFilter imageFilter )
    {
        final BufferedImage image = ImageHelper.toBufferedImage( inputStream );
        if ( size > 0 && ( image.getWidth() >= size ) )
        {
            switch ( imageFilter )
            {
                case SCALE_SQUARE_FILTER:
                    return new ScaleSquareFilter( size ).filter( image );

                case SCALE_MAX_FILTER:
                    return new ScaleMaxFilter( size ).filter( image );

                default:
                    throw new IllegalArgumentException( "Invalid image filter: " + imageFilter );
            }
        }
        else
        {
            return image;
        }
    }

    private BufferedImage rotateImage( Integer orientation, BufferedImage source )
    {

        AffineTransform transform = new AffineTransform();
        Integer resultWidth = source.getWidth();
        Integer resultHeight = source.getHeight();

        switch ( orientation )
        {
            case 1:
                return source;
            case 2: // Flip X
                transform.scale( -1.0, 1.0 );
                transform.translate( -resultWidth, 0 );
                break;
            case 3: // PI rotation
                transform.translate( resultWidth, resultHeight );
                transform.rotate( Math.PI );
                break;
            case 4: // Flip Y
                transform.scale( 1.0, -1.0 );
                transform.translate( 0, -resultHeight );
                break;
            case 5: // - PI/2 and Flip X
                transform.rotate( -Math.PI / 2 );
                transform.scale( -1.0, 1.0 );
                resultWidth = source.getHeight();
                resultHeight = source.getWidth();
                break;
            case 6: // -PI/2 and -width
                transform.translate( resultHeight, 0 );
                transform.rotate( Math.PI / 2 );
                resultWidth = source.getHeight();
                resultHeight = source.getWidth();
                break;
            case 7: // PI/2 and Flip
                transform.scale( -1.0, 1.0 );
                transform.translate( -resultHeight, 0 );
                transform.translate( 0, resultWidth );
                transform.rotate( 3 * Math.PI / 2 );
                resultWidth = source.getHeight();
                resultHeight = source.getWidth();
                break;
            case 8: // PI / 2
                transform.translate( 0, resultWidth );
                transform.rotate( 3 * Math.PI / 2 );
                resultWidth = source.getHeight();
                resultHeight = source.getWidth();
                break;
            default:
                return source;


        }
        BufferedImage destinationImage = new BufferedImage( resultWidth, resultHeight, source.getType() );
        AffineTransformOp op = new AffineTransformOp( transform, AffineTransformOp.TYPE_BICUBIC );
        return op.filter( source, destinationImage );
    }

    @Reference
    public void setMediaInfoService( final MediaInfoService mediaInfoService )
    {
        this.mediaInfoService = mediaInfoService;
    }


}
