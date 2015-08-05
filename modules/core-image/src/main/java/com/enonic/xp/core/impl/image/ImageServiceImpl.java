package com.enonic.xp.core.impl.image;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.base.Strings;
import com.google.common.io.ByteSource;

import com.enonic.xp.image.Cropping;
import com.enonic.xp.image.FocalPoint;
import com.enonic.xp.image.ImageFilter;
import com.enonic.xp.image.ImageFilterBuilder;
import com.enonic.xp.image.ImageHelper;
import com.enonic.xp.image.ImageScaleFunction;
import com.enonic.xp.image.ImageScaleFunctionBuilder;
import com.enonic.xp.image.ImageService;
import com.enonic.xp.image.ReadImageParams;
import com.enonic.xp.image.filter.ScaleMaxFunction;
import com.enonic.xp.image.filter.ScaleSquareFunction;
import com.enonic.xp.image.filter.ScaleWidthFunction;
import com.enonic.xp.image.scale.ScaleParams;
import com.enonic.xp.media.ImageOrientation;
import com.enonic.xp.util.Exceptions;

@Component
public class ImageServiceImpl
    implements ImageService
{
    private ImageScaleFunctionBuilder imageScaleFunctionBuilder;

    private ImageFilterBuilder imageFilterBuilder;

    @Override
    public BufferedImage readImage( final ByteSource blob, final ReadImageParams readImageParams )
    {
        //Retrieves the buffered image
        BufferedImage bufferedImage = retrieveBufferedImage( blob );

        if ( bufferedImage != null )
        {
            //Apply the cropping
            if ( readImageParams.getCropping() != null )
            {
                bufferedImage = applyCropping( bufferedImage, readImageParams.getCropping() );
            }

            //Applies the scaling
            if ( readImageParams.getScaleParams() != null )
            {
                bufferedImage = applyScalingParams( bufferedImage, readImageParams.getScaleParams(), readImageParams.getFocalPoint() );
            }
            else if ( readImageParams.getScaleSize() > 0 && ( bufferedImage.getWidth() >= readImageParams.getScaleSize() ) )
            {
                bufferedImage = applyScalingFunction( bufferedImage, readImageParams );
            }

            //Applies the filters
            if ( !Strings.isNullOrEmpty( readImageParams.getFilterParam() ) )
            {
                bufferedImage = applyFilters( bufferedImage, readImageParams.getFilterParam(), readImageParams.getBackgroundColor(),
                                              readImageParams.getFormat() );
            }

            //Applies the rotation
            if ( readImageParams.getOrientation() != ImageOrientation.TopLeft )
            {
                bufferedImage = applyRotation( bufferedImage, readImageParams.getOrientation() );
            }
        }

        return bufferedImage;
    }

    private BufferedImage retrieveBufferedImage( final ByteSource blob )
    {
        final BufferedImage bufferedImage;

        final InputStream inputStream;
        try
        {
            inputStream = blob.openStream();
            bufferedImage = ImageHelper.toBufferedImage( inputStream );
        }
        catch ( IOException e )
        {
            throw Exceptions.unchecked( e );
        }

        return bufferedImage;
    }

    private BufferedImage applyCropping( final BufferedImage bufferedImage, final Cropping cropping )
    {
        final double width = bufferedImage.getWidth();
        final double height = bufferedImage.getHeight();
        return bufferedImage.getSubimage( (int) ( width * cropping.left() ), (int) ( height * cropping.top() ),
                                          (int) ( width * cropping.width() ), (int) ( height * cropping.height() ) );

    }

    private BufferedImage applyScalingParams( final BufferedImage sourceImage, final ScaleParams scaleParams, final FocalPoint focalPoint )
    {
        final ImageScaleFunction imageScaleFunction = imageScaleFunctionBuilder.build( scaleParams, focalPoint );
        return imageScaleFunction.scale( sourceImage );
    }

    private BufferedImage applyScalingFunction( final BufferedImage bufferedImage, final ReadImageParams readImageParams )
    {
        if ( readImageParams.isScaleSquare() )
        {
            return new ScaleSquareFunction( readImageParams.getScaleSize() ).scale( bufferedImage );
        }
        else if ( readImageParams.isScaleWidth() )
        {
            return new ScaleWidthFunction( readImageParams.getScaleSize() ).scale( bufferedImage );
        }
        else
        {
            return new ScaleMaxFunction( readImageParams.getScaleSize() ).scale( bufferedImage );
        }
    }

    private BufferedImage applyFilters( final BufferedImage sourceImage, final String filterParam, final int backgroundColor,
                                        String format )
    {
        final ImageFilter imageFilter = imageFilterBuilder.build( filterParam );
        final BufferedImage targetImage = imageFilter.filter( sourceImage );

        if ( !ImageHelper.supportsAlphaChannel( format ) )
        {
            return ImageHelper.removeAlphaChannel( targetImage, backgroundColor );
        }
        else
        {
            return targetImage;
        }
    }

    private BufferedImage applyRotation( final BufferedImage bufferedImage, final ImageOrientation orientation )
    {
        final AffineTransform transform = new AffineTransform();
        int resultWidth = bufferedImage.getWidth();
        int resultHeight = bufferedImage.getHeight();

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
                resultWidth = bufferedImage.getHeight();
                resultHeight = bufferedImage.getWidth();
                break;
            case RightTop: // -PI/2 and -width
                transform.translate( resultHeight, 0 );
                transform.rotate( Math.PI / 2 );
                resultWidth = bufferedImage.getHeight();
                resultHeight = bufferedImage.getWidth();
                break;
            case RightBottom: // PI/2 and Flip
                transform.scale( -1.0, 1.0 );
                transform.translate( -resultHeight, 0 );
                transform.translate( 0, resultWidth );
                transform.rotate( 3 * Math.PI / 2 );
                resultWidth = bufferedImage.getHeight();
                resultHeight = bufferedImage.getWidth();
                break;
            case LeftBottom: // PI / 2
                transform.translate( 0, resultWidth );
                transform.rotate( 3 * Math.PI / 2 );
                resultWidth = bufferedImage.getHeight();
                resultHeight = bufferedImage.getWidth();
                break;
            default:
                return bufferedImage;

        }
        final BufferedImage destinationImage = new BufferedImage( resultWidth, resultHeight, bufferedImage.getType() );
        final AffineTransformOp op = new AffineTransformOp( transform, AffineTransformOp.TYPE_BICUBIC );
        return op.filter( bufferedImage, destinationImage );
    }

    @Reference
    public void setImageScaleFunctionBuilder( final ImageScaleFunctionBuilder imageScaleFunctionBuilder )
    {
        this.imageScaleFunctionBuilder = imageScaleFunctionBuilder;
    }

    @Reference
    public void setImageFilterBuilder( final ImageFilterBuilder imageFilterBuilder )
    {
        this.imageFilterBuilder = imageFilterBuilder;
    }
}
