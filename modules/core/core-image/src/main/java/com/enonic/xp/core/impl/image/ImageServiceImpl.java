package com.enonic.xp.core.impl.image;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.io.ByteSource;

import com.enonic.xp.content.ContentService;
import com.enonic.xp.core.impl.image.effect.ScaleMaxFunction;
import com.enonic.xp.core.impl.image.effect.ScaleSquareFunction;
import com.enonic.xp.core.impl.image.effect.ScaleWidthFunction;
import com.enonic.xp.home.HomeDir;
import com.enonic.xp.image.Cropping;
import com.enonic.xp.image.FocalPoint;
import com.enonic.xp.image.ImageHelper;
import com.enonic.xp.image.ImageService;
import com.enonic.xp.image.ReadImageParams;
import com.enonic.xp.image.ScaleParams;
import com.enonic.xp.media.ImageOrientation;
import com.enonic.xp.util.HexEncoder;

import static com.google.common.base.Strings.isNullOrEmpty;

@Component
public class ImageServiceImpl
    implements ImageService
{
    private ContentService contentService;

    private ImageScaleFunctionBuilder imageScaleFunctionBuilder;

    private ImageFilterBuilder imageFilterBuilder;

    @Override
    public ByteSource readImage( final ReadImageParams readImageParams )
        throws IOException
    {
        if ( renderAsSource( readImageParams ) )
        {
            return contentService.getBinary( readImageParams.getContentId(), readImageParams.getBinaryReference() );
        }

        final Path cachedImagePath = getCachedImagePath( readImageParams );
        return ImmutableFilesHelper.computeIfAbsent( cachedImagePath, () -> createImage( readImageParams ) );
    }

    private ByteSource createImage( final ReadImageParams readImageParams )
        throws IOException
    {
        final ByteSource blob = contentService.getBinary( readImageParams.getContentId(), readImageParams.getBinaryReference() );

        if ( blob != null )
        {
            final BufferedImage bufferedImage = readBufferedImage( blob, readImageParams );
            if ( bufferedImage != null )
            {
                if ( readImageParams.getMimeType() != null )
                {
                    return ByteSource.wrap(
                        ImageHelper.serializeImage( bufferedImage, readImageParams.getMimeType(), readImageParams.getQuality() ) );
                }
                else if ( readImageParams.getFormat() != null )
                {
                    final ByteArrayOutputStream out = new ByteArrayOutputStream();
                    ImageIO.write( bufferedImage, readImageParams.getFormat(), out );
                    return ByteSource.wrap( out.toByteArray() );
                }
            }
        }
        return null;
    }

    private boolean renderAsSource( final ReadImageParams params )
    {
        return "image/gif".equals( params.getMimeType() ) || "gif".equals( params.getFormat() );
    }

    @Deprecated
    @Override
    public String getFormatByMimeType( final String mimeType )
        throws IOException
    {
        return ImageHelper.getFormatByMimeType( mimeType );
    }

    private Path getCachedImagePath( final ReadImageParams readImageParams )
        throws IOException
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
        final String filter = readImageParams.getFilterParam() != null ? readImageParams.getFilterParam() : "no-filter";

        final String format = readImageParams.getMimeType() == null
            ? readImageParams.getFormat()
            : ImageHelper.getFormatByMimeType( readImageParams.getMimeType() );
        //Background string value
        final String background = "background-" + readImageParams.getBackgroundColor();

        //Orientating string value
        final String orientation = "orientation-" + readImageParams.getOrientation().toString();

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

    private BufferedImage readBufferedImage( final ByteSource blob, final ReadImageParams readImageParams )
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
            if ( !isNullOrEmpty( readImageParams.getFilterParam() ) )
            {
                bufferedImage = applyFilters( bufferedImage, readImageParams.getFilterParam() );
            }

            //Applies alpha channel removal
            if ( !"image/png".equals( readImageParams.getMimeType() ) && !ImageHelper.supportsAlphaChannel( readImageParams.getFormat() ) )
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

    private BufferedImage applyFilters( final BufferedImage sourceImage, final String filterParam )
    {
        final ImageFilter imageFilter = imageFilterBuilder.build( filterParam );
        return imageFilter.filter( sourceImage );
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

    @Reference
    public void setContentService( final ContentService contentService )
    {
        this.contentService = contentService;
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
