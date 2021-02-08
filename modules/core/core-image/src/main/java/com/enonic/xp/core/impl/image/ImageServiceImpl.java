package com.enonic.xp.core.impl.image;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.io.ByteSource;

import com.enonic.xp.content.ContentService;
import com.enonic.xp.core.impl.image.parser.FilterSetExpr;
import com.enonic.xp.home.HomeDir;
import com.enonic.xp.image.Cropping;
import com.enonic.xp.image.FocalPoint;
import com.enonic.xp.image.ImageHelper;
import com.enonic.xp.image.ImageService;
import com.enonic.xp.image.ReadImageParams;
import com.enonic.xp.image.ScaleParams;
import com.enonic.xp.media.ImageOrientation;
import com.enonic.xp.util.HexEncoder;

@Component
public class ImageServiceImpl
    implements ImageService
{
    private final ContentService contentService;

    private final ImageScaleFunctionBuilder imageScaleFunctionBuilder;

    private final ImageFilterBuilder imageFilterBuilder;

    @Activate
    public ImageServiceImpl( @Reference final ContentService contentService,
                             @Reference final ImageScaleFunctionBuilder imageScaleFunctionBuilder,
                             @Reference final ImageFilterBuilder imageFilterBuilder )
    {
        this.contentService = contentService;
        this.imageScaleFunctionBuilder = imageScaleFunctionBuilder;
        this.imageFilterBuilder = imageFilterBuilder;
    }

    @Override
    public ByteSource readImage( final ReadImageParams readImageParams )
        throws IOException
    {
        NormalizedImageParams normalizedImageParams = new NormalizedImageParams( readImageParams );
        final Path cachedImagePath = getCachedImagePath( normalizedImageParams );
        return ImmutableFilesHelper.computeIfAbsent( cachedImagePath, () -> createImage( normalizedImageParams ) );
    }

    private ByteSource createImage( final NormalizedImageParams readImageParams )
        throws IOException
    {
        final ByteSource blob = contentService.getBinary( readImageParams.getContentId(), readImageParams.getBinaryReference() );

        if ( blob != null )
        {
            final BufferedImage bufferedImage = readBufferedImage( blob, readImageParams );
            if ( bufferedImage != null )
            {
                // Previous ImageHelper implementation interpreted 0 as system default quality explicitly,
                // and anything below 0 as system default due to Exception swallow
                // New implementation supports 0 value (it means "best compression" for PNG),
                // but 0 quality in image service need to be retrofitted to "system default", otherwise JPEG with 0 quality
                // is over-compressed and looks way different from system default compressed image.
                final int writeImageQuality = readImageParams.getQuality() == 0 ? -1 : readImageParams.getQuality();
                return ByteSource.wrap( ImageHelper.writeImage( bufferedImage, readImageParams.getFormat(), writeImageQuality ) );
            }
        }
        return null;
    }

    @Deprecated
    @Override
    public String getFormatByMimeType( final String mimeType )
        throws IOException
    {
        return ImageHelper.getFormatByMimeType( mimeType );
    }

    private Path getCachedImagePath( final NormalizedImageParams readImageParams )
    {
        final String homeDir = HomeDir.get().toString();

        //Cropping string value
        final String cropping = readImageParams.getCropping() == null ? "no-cropping" : readImageParams.getCropping().toString();

        //Scale string value
        final String scale = readImageParams.getScaleParams() == null
            ? "no-scale"
            : "scale-" + readImageParams.getScaleParams() + "-" + readImageParams.getFocalPoint();

        //Filter string value
        final String filter = readImageParams.getFilterParam().isEmpty() ? "no-filter" : readImageParams.getFilterParam().toString();

        final String format = readImageParams.getFormat();
        //Background string value
        final String background = "background-" + readImageParams.getBackgroundColor();

        //Orientating string value
        final String orientation = "orientation-" + readImageParams.getOrientation();

        //Serialization string value
        final String quality = "quality-" + readImageParams.getQuality();
        //Source binary key
        final String binaryKey = contentService.getBinaryKey( readImageParams.getContentId(), readImageParams.getBinaryReference() );

        final String key = String.join( "/", binaryKey, cropping, scale, filter, format, background, orientation, quality,
                                        readImageParams.getBinaryReference().toString() );
        final HashCode hashCode = Hashing.sha1().hashString( key, StandardCharsets.UTF_8 );
        final String hash = HexEncoder.toHex( hashCode.asBytes() );
        return Paths.get( homeDir, "work", "cache", "img", hash.substring( 0, 2 ), hash.substring( 2, 4 ), hash.substring( 4, 6 ),
                          hash ).toAbsolutePath();
    }

    private BufferedImage readBufferedImage( final ByteSource blob, final NormalizedImageParams readImageParams )
        throws IOException
    {
        //Retrieves the buffered image
        BufferedImage bufferedImage = retrieveBufferedImage( blob );

        if ( bufferedImage != null )
        {
            //Applies the rotation
            if ( readImageParams.getOrientation() != ImageOrientation.TopLeft )
            {
                bufferedImage = applyRotation( bufferedImage, readImageParams.getOrientation() );
            }

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

            //Applies the filters
            if ( !readImageParams.getFilterParam().isEmpty() )
            {
                bufferedImage = applyFilters( bufferedImage, readImageParams.getFilterParam() );
            }

            //Applies alpha channel removal
            if ( !"png".equals( readImageParams.getFormat() ) )
            {
                bufferedImage = ImageHelper.removeAlphaChannel( bufferedImage, readImageParams.getBackgroundColor() );
            }
        }

        return bufferedImage;
    }

    private BufferedImage retrieveBufferedImage( final ByteSource blob )
        throws IOException
    {
        try (InputStream inputStream = blob.openStream())
        {
            return ImageHelper.toBufferedImage( inputStream );
        }
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
        return imageScaleFunctionBuilder.build( scaleParams, focalPoint ).apply( sourceImage );
    }

    private BufferedImage applyFilters( final BufferedImage sourceImage, final FilterSetExpr filterParam )
    {
        return imageFilterBuilder.build( filterParam ).apply( sourceImage );
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
            case LeftTop: // -PI/2 and Flip X
                transform.scale( -1.0, 1.0 );
                transform.rotate( Math.PI / 2 );
                resultWidth = bufferedImage.getHeight();
                resultHeight = bufferedImage.getWidth();
                break;
            case RightTop: // -PI/2
                transform.translate( resultHeight, 0 );
                transform.rotate( Math.PI / 2 );
                resultWidth = bufferedImage.getHeight();
                resultHeight = bufferedImage.getWidth();
                break;
            case RightBottom: // PI/2 and Flip X
                transform.scale( -1.0, 1.0 );
                transform.translate( -resultHeight, 0 );
                transform.translate( 0, resultWidth );
                transform.rotate( -Math.PI / 2 );
                resultWidth = bufferedImage.getHeight();
                resultHeight = bufferedImage.getWidth();
                break;
            case LeftBottom: // PI/2
                transform.translate( 0, resultWidth );
                transform.rotate( -Math.PI / 2 );
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
}
