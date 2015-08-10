package com.enonic.xp.core.impl.image;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.base.Strings;
import com.google.common.io.ByteSource;

import com.enonic.xp.home.HomeDir;
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
import com.enonic.xp.util.FilesHelper;

@Component
public class ImageServiceImpl
    implements ImageService
{
    private ImageScaleFunctionBuilder imageScaleFunctionBuilder;

    private ImageFilterBuilder imageFilterBuilder;

    @Override
    public byte[] readImage( final ByteSource blob, final String id, final String binaryReference, final ReadImageParams readImageParams )
        throws IOException
    {
        final Path cachedImagePath = getCachedImagePath( id, binaryReference, readImageParams );
        byte[] serializedImage = FilesHelper.readAllBytes( cachedImagePath );
        if ( serializedImage == null )
        {
            final BufferedImage bufferedImage = readBufferedImage( blob, readImageParams );
            if ( bufferedImage != null )
            {
                serializedImage = serializeImage( readImageParams, bufferedImage );
                FilesHelper.write( cachedImagePath, serializedImage );
            }
        }
        return serializedImage;
    }

    @Override
    public String getFormatByMimeType( final String mimeType )
        throws IOException
    {
        final Iterator<ImageWriter> i = ImageIO.getImageWritersByMIMEType( mimeType );
        if ( !i.hasNext() )
        {
            throw new IOException( "The image-based media type " + mimeType + " is not supported for writing" );
        }

        return i.next().getOriginatingProvider().getFormatNames()[0];
    }

    private Path getCachedImagePath( final String id, final String binaryReference, final ReadImageParams readImageParams )
    {
        final String homeDir = HomeDir.get().toString();

        //Cropping string value
        final String cropping = readImageParams.getCropping() != null ? readImageParams.getCropping().toString() : "no-cropping";

        //Scale string value
        String scale = "no-scale";
        if ( readImageParams.getScaleParams() != null )
        {
            scale = "scale-" + readImageParams.getScaleParams().toString() + "-" + readImageParams.getFocalPoint().toString();
        }
        else if ( readImageParams.getScaleSize() > 0 )
        {
            if ( readImageParams.isScaleSquare() )
            {
                scale = "scalesquare-" + readImageParams.getScaleSize();
            }
            else if ( readImageParams.isScaleWidth() )
            {
                scale = "scalewidth-" + readImageParams.getScaleSize();
            }
            else
            {
                scale = "scalemax-" + readImageParams.getScaleSize();
            }
        }

        //Filter string value
        final String filter =
            readImageParams.getFilterParam() != null ? readImageParams.getFilterParam() + "-" + readImageParams.getBackgroundColor() + "-" +
                readImageParams.getFormat() : "no-filter";

        //Orientating string value
        final String orientation = readImageParams.getOrientation().toString();

        //Serialization string value
        final String serialization = readImageParams.getQuality() + "-" + readImageParams.getFormat();

        return Paths.get( homeDir, "work", "cache", "img", id, cropping, scale, filter, orientation, serialization, binaryReference ).
            toAbsolutePath();
    }

    private BufferedImage readBufferedImage( final ByteSource blob, final ReadImageParams readImageParams )
        throws IOException
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
            //TODO If/Else due to a difference of treatment between admin and portal. Should be uniform
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
        throws IOException
    {
        final InputStream inputStream = blob.openStream();
        return ImageHelper.toBufferedImage( inputStream );
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

    private byte[] serializeImage( final ReadImageParams readImageParams, final BufferedImage bufferedImage )
        throws IOException
    {
        final byte[] serializedImage;
        //TODO If/Else due to a difference of treatment between admin and portal. Should be uniform
        if ( readImageParams.getQuality() != 0 )
        {
            serializedImage = serializeImage( bufferedImage, readImageParams.getFormat(), readImageParams.getQuality() );
        }
        else
        {
            serializedImage = serializeImage( bufferedImage, readImageParams.getFormat() );
        }
        return serializedImage;
    }

    private byte[] serializeImage( final BufferedImage bufferedImage, final String format, final int quality )
        throws IOException
    {
        return ImageHelper.writeImage( bufferedImage, format, quality );
    }

    private byte[] serializeImage( final BufferedImage bufferedImage, final String format )
        throws IOException
    {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write( bufferedImage, format, out );
        return out.toByteArray();
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
