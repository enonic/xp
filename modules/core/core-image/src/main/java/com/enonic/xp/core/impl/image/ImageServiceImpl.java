package com.enonic.xp.core.impl.image;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HexFormat;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.stream.ImageInputStream;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.io.ByteSink;
import com.google.common.io.ByteSource;

import com.enonic.xp.content.ContentService;
import com.enonic.xp.core.impl.image.effect.ImageScaleFunction;
import com.enonic.xp.core.internal.MemoryLimitParser;
import com.enonic.xp.core.internal.SimpleCsvParser;
import com.enonic.xp.home.HomeDir;
import com.enonic.xp.image.Cropping;
import com.enonic.xp.image.ImageHelper;
import com.enonic.xp.image.ImageService;
import com.enonic.xp.image.ReadImageParams;
import com.enonic.xp.media.ImageOrientation;

@Component
public class ImageServiceImpl
    implements ImageService
{
    private static final Logger LOG = LoggerFactory.getLogger( ImageServiceImpl.class );

    private final Path cacheFolder = HomeDir.get().toPath().resolve( "work" ).resolve( "cache" ).resolve( "img" );

    private final ContentService contentService;

    private final ImageScaleFunctionBuilder imageScaleFunctionBuilder;

    private final ImageFilterBuilder imageFilterBuilder;

    private final MemoryCircuitBreaker circuitBreaker;

    private final Set<String> progressiveOnFormats;

    @Activate
    public ImageServiceImpl( @Reference final ContentService contentService,
                             @Reference final ImageScaleFunctionBuilder imageScaleFunctionBuilder,
                             @Reference final ImageFilterBuilder imageFilterBuilder, final ImageConfig config )
    {
        this.contentService = contentService;
        this.imageScaleFunctionBuilder = imageScaleFunctionBuilder;
        this.imageFilterBuilder = imageFilterBuilder;

        this.circuitBreaker = new MemoryCircuitBreaker( toMegaBytes( MemoryLimitParser.maxHeap().parse( config.memoryLimit() ) ) );

        this.progressiveOnFormats = SimpleCsvParser.parseLine( config.progressive() )
            .stream()
            .map( String::trim )
            .filter( Predicate.not( String::isEmpty ) )
            .map( s -> s.toLowerCase( Locale.ROOT ) )
            .collect( Collectors.toUnmodifiableSet() );
    }

    @Override
    public ByteSource readImage( final ReadImageParams readImageParams )
        throws IOException
    {
        NormalizedImageParams normalizedImageParams = new NormalizedImageParams( readImageParams );
        final Path cachedImagePath = getCachedImagePath( normalizedImageParams );
        return ImmutableFilesHelper.computeIfAbsent( cachedImagePath, sink -> writeImage( normalizedImageParams, sink ) );
    }

    private boolean writeImage( final NormalizedImageParams readImageParams, final ByteSink sink )
    {
        try
        {
            final ByteSource blob = contentService.getBinary( readImageParams.getContentId(), readImageParams.getBinaryReference() );

            if ( blob != null )
            {
                return createImage( blob, readImageParams, sink );
            }
            return false;
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
    }

    private Path getCachedImagePath( final NormalizedImageParams readImageParams )
    {
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
        final String hash = HexFormat.of().formatHex( hashCode.asBytes() );
        return cacheFolder.resolve( hash.substring( 0, 2 ) )
            .resolve( hash.substring( 2, 4 ) )
            .resolve( hash.substring( 4, 6 ) )
            .resolve( hash );
    }

    private boolean createImage( final ByteSource blob, final NormalizedImageParams readImageParams, ByteSink sink )
        throws IOException
    {
        try (InputStream inputStream = blob.openStream(); ImageInputStream stream = ImageIO.createImageInputStream( inputStream ))
        {
            final ImageReader imageReader = getImageReader( stream );

            if ( imageReader != null )
            {
                try
                {
                    final int width = imageReader.getWidth( 0 );
                    final int height = imageReader.getHeight( 0 );

                    final ImageTypeSpecifier rawImageType = imageReader.getRawImageType( 0 );
                    final int pixelSize;
                    final boolean mayHaveAlpha;
                    if ( rawImageType != null )
                    {
                        final ColorModel originalColorModel = rawImageType.getColorModel();
                        pixelSize = originalColorModel.getPixelSize() / Byte.SIZE;
                        mayHaveAlpha = originalColorModel.hasAlpha();
                    }
                    else
                    {
                        // Fallback to 4 bytes per pixel and assume alpha channel
                        pixelSize = 4;
                        mayHaveAlpha = true;
                    }

                    final boolean toRotate = readImageParams.getOrientation() != ImageOrientation.TopLeft;
                    final boolean toApplyFilters = !readImageParams.getFilterParam().isEmpty();
                    final boolean toAddBackground =
                        !"png".equals( readImageParams.getFormat() ) && ( mayHaveAlpha || toApplyFilters );
                    final boolean toScale = readImageParams.getScaleParams() != null;
                    final boolean toCrop = readImageParams.getCropping() != null;

                    final int originalMultiplier = 1 + ( toRotate || ( !toScale && ( toApplyFilters || toAddBackground ) ) ? 1 : 0 );

                    final int originalMemoryRequirements =
                        Math.max( toMegaBytes( (long) width * height * pixelSize * originalMultiplier ), 1 );

                    final ImageScaleFunction imageScaleFunction;
                    final int scaledMemoryRequirements;
                    if ( toScale )
                    {
                        imageScaleFunction =
                            imageScaleFunctionBuilder.build( readImageParams.getScaleParams(), readImageParams.getFocalPoint() );
                        final int scaledMultiplier = 1 + ( ( toApplyFilters || toAddBackground ) ? 1 : 0 );
                        scaledMemoryRequirements = Math.max(
                            toMegaBytes( (long) imageScaleFunction.estimateResolution( width, height ) * pixelSize * scaledMultiplier ),
                            1 );
                    }
                    else
                    {
                        imageScaleFunction = null;
                        scaledMemoryRequirements = 0;
                    }

                    final int totalMemoryRequirementsEstimate = originalMemoryRequirements + scaledMemoryRequirements;

                    LOG.debug( "Estimated original {} scaled {} total {} requirements. With pixelSize {}", originalMemoryRequirements,
                               scaledMemoryRequirements, totalMemoryRequirementsEstimate, pixelSize );

                    final int permitted = circuitBreaker.softTryAcquire( totalMemoryRequirementsEstimate );
                    try
                    {
                        BufferedImage bufferedImage = imageReader.read( 0, imageReader.getDefaultReadParam() );
                        imageReader.dispose();
                        if ( bufferedImage != null )
                        {
                            if ( toRotate )
                            {
                                bufferedImage = applyRotation( bufferedImage, readImageParams.getOrientation() );
                            }

                            if ( toCrop )
                            {
                                bufferedImage = applyCropping( bufferedImage, readImageParams.getCropping() );
                            }

                            if ( toScale )
                            {
                                bufferedImage = imageScaleFunction.apply( bufferedImage );
                            }

                            if ( toApplyFilters )
                            {
                                bufferedImage = imageFilterBuilder.build( readImageParams.getFilterParam() ).apply( bufferedImage );
                            }

                            if ( toAddBackground )
                            {
                                bufferedImage = ImageHelper.removeAlphaChannel( bufferedImage, readImageParams.getBackgroundColor() );
                            }

                            // Previous ImageHelper implementation interpreted 0 as system default quality explicitly,
                            // and anything below 0 as system default due to Exception swallow
                            // New implementation supports 0 value (it means "best compression" for PNG),
                            // but 0 quality in image service need to be retrofitted to "system default", otherwise JPEG with 0 quality
                            // is over-compressed and looks way different from system default compressed image.
                            final int writeImageQuality = readImageParams.getQuality() == 0 ? -1 : readImageParams.getQuality();

                            final boolean progressive =
                                progressiveOnFormats.stream().anyMatch( format -> format.equalsIgnoreCase( readImageParams.getFormat() ) );

                            try (OutputStream outputStream = sink.openBufferedStream())
                            {
                                ImageHelper.writeImage( outputStream, bufferedImage, readImageParams.getFormat(), writeImageQuality, progressive );
                            }
                            LOG.debug( "Finish writing" );
                            return true;
                        }
                    }
                    finally
                    {
                        circuitBreaker.release( permitted );
                    }
                }
                finally
                {
                    imageReader.dispose();
                }

            }
        }
        return false;
    }

    private static int toMegaBytes( long bytesValue )
    {
        return Math.toIntExact( bytesValue / 1024 / 1024 );
    }

    private static BufferedImage applyCropping( final BufferedImage bufferedImage, final Cropping cropping )
    {
        final double width = bufferedImage.getWidth();
        final double height = bufferedImage.getHeight();
        return bufferedImage.getSubimage( (int) ( width * cropping.left() ), (int) ( height * cropping.top() ),
                                          (int) ( width * cropping.width() ), (int) ( height * cropping.height() ) );

    }

    private static BufferedImage applyRotation( final BufferedImage bufferedImage, final ImageOrientation orientation )
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

    private static ImageReader getImageReader( final ImageInputStream stream )
    {
        if ( stream == null )
        {
            return null;
        }
        final Iterator<ImageReader> imageReaders = ImageIO.getImageReaders( stream );
        if ( imageReaders.hasNext() )
        {
            final ImageReader imageReader = imageReaders.next();
            imageReader.setInput( stream );

            return imageReader;
        }
        else
        {
            return null;
        }
    }
}
