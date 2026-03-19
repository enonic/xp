package com.enonic.xp.core.impl.image;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.Iterator;
import java.util.Locale;
import java.util.Objects;
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

import com.google.common.io.ByteSink;
import com.google.common.io.ByteSource;

import com.enonic.xp.content.ContentService;
import com.enonic.xp.core.impl.image.effect.ImageScaleFunction;
import com.enonic.xp.core.internal.MemoryLimitParser;
import com.enonic.xp.core.internal.SimpleCsvParser;
import com.enonic.xp.core.internal.security.MessageDigests;
import com.enonic.xp.home.HomeDir;
import com.enonic.xp.image.Cropping;
import com.enonic.xp.image.FocalPoint;
import com.enonic.xp.image.ImageHelper;
import com.enonic.xp.image.ImageService;
import com.enonic.xp.image.ReadImageParams;
import com.enonic.xp.image.ScaleParams;
import com.enonic.xp.media.ImageOrientation;

@Component
public class ImageServiceImpl
    implements ImageService
{
    private static final Logger LOG = LoggerFactory.getLogger( ImageServiceImpl.class );

    private final Path cacheFolder = HomeDir.get().toPath().resolve( "work" ).resolve( "cache" ).resolve( "img" );

    private final ImmutableFilesHelper immutableFilesHelper = new ImmutableFilesHelper( cacheFolder.resolve( "tmp" ) );

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
        final NormalizedImageParams normalizedImageParams = new NormalizedImageParams( readImageParams );

        final String attachmentSha512 = Objects.requireNonNullElseGet( normalizedImageParams.getAttachmentSha512(),
                                                                       () -> contentService.getById( normalizedImageParams.getContentId() )
                                                                           .getAttachments()
                                                                           .byName( normalizedImageParams.getBinaryReference().toString() )
                                                                           .getSha512() );

        return immutableFilesHelper.computeIfAbsent( getCachedImagePath( normalizedImageParams, attachmentSha512 ),
                                                     sink -> writeImage( normalizedImageParams, attachmentSha512, sink ) );
    }


    private void writeImage( final NormalizedImageParams readImageParams, String expectedSha512, final ByteSink sink )
    {
        try
        {
            final ByteSource blob = contentService.getBinary( readImageParams.getContentId(), readImageParams.getBinaryReference() );

            if ( blob == null )
            {
                throw new IllegalArgumentException(
                    "No binary found for content [" + readImageParams.getContentId() + "] and binary reference [" +
                        readImageParams.getBinaryReference() + "]" );
            }

            final MessageDigest sha512Digest = MessageDigests.sha512();
            try (InputStream is = blob.openStream(); DigestInputStream dis = new DigestInputStream( is, sha512Digest ))
            {
                dis.transferTo( OutputStream.nullOutputStream() );
            }
            final String resultingSha512 = HexFormat.of().formatHex( sha512Digest.digest() );

            if ( !expectedSha512.equals( resultingSha512 ) )
            {
                throw new IllegalStateException(
                    "Attachment checksum mismatch for content [" + readImageParams.getContentId() + "] and binary reference" +
                        readImageParams.getBinaryReference() + "]" );
            }

            createImage( blob, readImageParams, sink );
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
    }

    private Path getCachedImagePath( final NormalizedImageParams readImageParams, final String attachmentSha512 )
    {
        final MessageDigest digest = MessageDigests.sha256();

        digest.update( HexFormat.of().parseHex( attachmentSha512 ) );

        final FocalPoint focalPoint = readImageParams.getFocalPoint();
        MessageDigests.updateWithDoubleLE( digest, focalPoint.xOffset() );
        MessageDigests.updateWithDoubleLE( digest, focalPoint.yOffset() );

        final Cropping cropping = readImageParams.getCropping();
        MessageDigests.updateWithDoubleLE( digest, cropping.top() );
        MessageDigests.updateWithDoubleLE( digest, cropping.left() );
        MessageDigests.updateWithDoubleLE( digest, cropping.bottom() );
        MessageDigests.updateWithDoubleLE( digest, cropping.right() );
        MessageDigests.updateWithDoubleLE( digest, cropping.zoom() );

        MessageDigests.updateWithIntLE( digest, readImageParams.getOrientation().ordinal() );

        MessageDigests.updateWithIntLE( digest, readImageParams.getBackgroundColor() );
        MessageDigests.updateWithIntLE( digest, readImageParams.getQuality() );
        MessageDigests.updateWithString( digest, readImageParams.getFormat() );
        MessageDigests.updateWithString( digest, readImageParams.getScaleParams().toString() );
        MessageDigests.updateWithString( digest, readImageParams.getFilterParam().toString() );

        final String hash = HexFormat.of().formatHex( digest.digest() );
        return cacheFolder.resolve( "sha256" )
            .resolve( hash.substring( 0, 2 ) )
            .resolve( hash.substring( 2, 4 ) )
            .resolve( hash.substring( 4, 6 ) )
            .resolve( hash.substring( 6 ) );
    }

    private void createImage( final ByteSource blob, final NormalizedImageParams readImageParams, ByteSink sink )
        throws IOException
    {
        try (InputStream inputStream = blob.openStream(); ImageInputStream stream = ImageIO.createImageInputStream( inputStream ))
        {
            final ImageReader imageReader = getImageReader( stream );

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
                final boolean toAddBackground = !"png".equals( readImageParams.getFormat() ) && ( mayHaveAlpha || toApplyFilters );
                final boolean toScale = !ScaleParams.NO_SCALE.getName().equals( readImageParams.getScaleParams().getName() );
                final boolean toCrop = !readImageParams.getCropping().isUnmodified();

                final int originalMultiplier = 1 + ( toRotate || ( !toScale && ( toApplyFilters || toAddBackground ) ) ? 1 : 0 );

                final int originalMemoryRequirements = Math.max( toMegaBytes( (long) width * height * pixelSize * originalMultiplier ), 1 );

                final ImageScaleFunction imageScaleFunction;
                final int scaledMemoryRequirements;
                if ( toScale )
                {
                    imageScaleFunction =
                        imageScaleFunctionBuilder.build( readImageParams.getScaleParams(), readImageParams.getFocalPoint() );
                    final int scaledMultiplier = 1 + ( ( toApplyFilters || toAddBackground ) ? 1 : 0 );
                    scaledMemoryRequirements = Math.max(
                        toMegaBytes( (long) imageScaleFunction.estimateResolution( width, height ) * pixelSize * scaledMultiplier ), 1 );
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
                    Objects.requireNonNull( bufferedImage, "BufferedImage is null" );
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
            throw new IllegalArgumentException( "No suitable ImageInputStream" );
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
            throw new IllegalArgumentException( "No suitable ImageReader" );
        }
    }
}
