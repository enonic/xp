package com.enonic.xp.core.impl.image;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
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

import com.google.common.io.ByteSink;
import com.google.common.io.ByteSource;
import com.google.common.io.MoreFiles;

import com.enonic.xp.content.ContentService;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.core.impl.image.effect.ImageScaleFunction;
import com.enonic.xp.core.internal.ByteSizeParser;
import com.enonic.xp.core.internal.MemoryLimitParser;
import com.enonic.xp.core.internal.SimpleCsvParser;
import com.enonic.xp.core.internal.security.MessageDigests;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.exception.ThrottlingException;
import com.enonic.xp.home.HomeDir;
import com.enonic.xp.image.Cropping;
import com.enonic.xp.image.FocalPoint;
import com.enonic.xp.image.ImageHelper;
import com.enonic.xp.image.ImageService;
import com.enonic.xp.image.ReadImageParams;
import com.enonic.xp.image.ScaleParams;
import com.enonic.xp.media.ImageOrientation;
import com.enonic.xp.style.ImageStyle;
import com.enonic.xp.style.ImageStyleSettings;
import com.enonic.xp.style.StyleDescriptor;
import com.enonic.xp.style.StyleDescriptorService;

import static java.util.Objects.requireNonNull;

@Component(configurationPid = "com.enonic.xp.image")
public class ImageServiceImpl
    implements ImageService
{
    private final Path cacheFolder = HomeDir.get().toPath().resolve( "work" ).resolve( "cache" ).resolve( "img" );

    private final ImmutableFilesHelper immutableFilesHelper = new ImmutableFilesHelper( cacheFolder.resolve( "ingest" ) );

    private final ContentService contentService;

    private final ImageScaleFunctionBuilder imageScaleFunctionBuilder;

    private final ImageFilterBuilder imageFilterBuilder;

    private final MemoryCircuitBreaker circuitBreaker;

    private final Set<String> progressiveOnFormats;

    private final StyleDescriptorService styleDescriptorService;

    private final ImageMagickEncoder nativeEncoder;

    private final boolean useImageMagick;

    private final boolean useImageMagickDecoder;

    private final ImageMagickDecoder nativeDecoder;

    private final boolean useImageMagickTransformer;

    private final ImageMagickTransformer nativeTransformer;

    private final ImageProcessingGate processingGate;

    private final long maxSourceBytes;

    private final int queueTimeoutSeconds;

    private final long maxProcessingPixels;

    @Activate
    public ImageServiceImpl( @Reference final ContentService contentService,
                             @Reference final ImageScaleFunctionBuilder imageScaleFunctionBuilder,
                             @Reference final ImageFilterBuilder imageFilterBuilder,
                             @Reference final StyleDescriptorService styleDescriptorService, final ImageConfig config )
    {
        this.contentService = contentService;
        this.imageScaleFunctionBuilder = imageScaleFunctionBuilder;
        this.imageFilterBuilder = imageFilterBuilder;
        this.styleDescriptorService = styleDescriptorService;

        if ( config.processing_maxConcurrent() < 1 || config.processing_maxQueue() < 0 ||
            config.processing_queueTimeoutSeconds() < 1 || config.processing_maxPixels() < 1 )
        {
            throw new IllegalArgumentException( "Invalid image processing limits" );
        }
        this.useImageMagick = switch ( config.encoding_backend() )
        {
            case "ImageIO" -> false;
            case "ImageMagic" -> true;
            default -> throw new IllegalArgumentException( "encoding.backend must be ImageIO or ImageMagic" );
        };
        this.useImageMagickDecoder = switch ( config.decoding_backend() )
        {
            case "ImageIO" -> false;
            case "ImageMagic" -> true;
            default -> throw new IllegalArgumentException( "decoding.backend must be ImageIO or ImageMagic" );
        };
        this.useImageMagickTransformer = switch ( config.transformation_backend() )
        {
            case "ImageIO" -> false;
            case "ImageMagic" -> true;
            default -> throw new IllegalArgumentException( "transformation.backend must be ImageIO or ImageMagic" );
        };
        this.nativeTransformer = new ImageMagickTransformer( "embedded", config.processing_timeoutSeconds(),
            cacheFolder.resolve( "transformation" ) );
        this.nativeDecoder = new ImageMagickDecoder( "embedded", cacheFolder.resolve( "decoding" ),
            config.processing_timeoutSeconds(), config.processing_maxPixels(), ByteSizeParser.parse( config.decoding_maxBytes() ) );
        this.processingGate = new ImageProcessingGate( config.processing_maxConcurrent(), config.processing_maxQueue(), config.processing_queueTimeoutSeconds() );
        this.maxSourceBytes = ByteSizeParser.parse( config.decoding_maxBytes() );
        this.queueTimeoutSeconds = config.processing_queueTimeoutSeconds();
        this.maxProcessingPixels = config.processing_maxPixels();
        this.nativeEncoder = new ImageMagickEncoder( useImageMagick ? "embedded" : "", config.processing_timeoutSeconds(),
                                                    cacheFolder.resolve( "encoding" ) );

        this.circuitBreaker = new MemoryCircuitBreaker( toMegaBytes( MemoryLimitParser.maxHeap().parse( config.memoryLimit() ) ) );

        this.progressiveOnFormats = SimpleCsvParser.parseLine( config.progressive() )
            .stream()
            .filter( Predicate.not( String::isEmpty ) )
            .map( s -> s.toLowerCase( Locale.ROOT ) )
            .collect( Collectors.toUnmodifiableSet() );
    }

    @Override
    public ImageStyle getStyle( final String key )
    {
        final DescriptorKey descriptorKey = DescriptorKey.from( key );
        final StyleDescriptor descriptor = styleDescriptorService.getByApplication( descriptorKey.getApplicationKey() );
        final ImageStyle style = descriptor == null ? null : descriptor.getElements().stream()
            .filter( element -> element instanceof ImageStyle && element.getName().equals( descriptorKey.getName() ) )
            .map( ImageStyle.class::cast ).findFirst().orElse( null );
        if ( style == null )
        {
            throw new com.enonic.xp.style.ImageStyleNotFoundException( key );
        }
        ImageStyleSettings.from( style );
        return style;
    }

    @Override
    public ByteSource readImage( final ReadImageParams readImageParams )
        throws IOException
    {
        final ImageStyle style = readImageParams.getStyle() == null ? null : getStyle( readImageParams.getStyle() );
        if ( readImageParams.getExpectedStyle() != null && !ImageStyleSettings.from( readImageParams.getExpectedStyle() ).equals( ImageStyleSettings.from( style ) ) )
        {
            throw new IllegalArgumentException( "Image style changed during request; regenerate the image URL" );
        }
        final NormalizedImageParams normalizedImageParams = new NormalizedImageParams( readImageParams, style );
        // Cache-only requests must not read the source to discover a missing checksum.
        if ( readImageParams.isCacheOnly() && normalizedImageParams.getAttachmentSha512() == null )
        {
            throw new IllegalArgumentException( "A source checksum is required for cache-only image requests" );
        }
        final String checksum = resolveAttachmentSha512( normalizedImageParams );
        final Path path = checksum == null ? null : getCachedImagePath( normalizedImageParams, checksum );
        if ( path != null && Files.exists( path ) ) { return MoreFiles.asByteSource( path ); }
        if ( readImageParams.isCacheOnly() )
        {
            throw new IllegalArgumentException( "Image is not cached; regeneration requires a matching image fingerprint" );
        }
        if ( isModernFormat( normalizedImageParams.getFormat() ) ) { nativeEncoder.checkEnabled(); }
        if ( !nativeProcessing() ) { return generateImage( normalizedImageParams, checksum, path ); }
        // Missing attachment metadata is resolved from source bytes only after bounded admission.
        final Object key = path == null ? pendingKey( normalizedImageParams ) : path;
        return processingGate.execute( key, () -> generateImage( normalizedImageParams, checksum, path ) );
    }

    private Object pendingKey( final NormalizedImageParams params )
    {
        final var context = ContextAccessor.current();
        final MessageDigest digest = MessageDigests.sha512();
        MessageDigests.updateWithString( digest, String.valueOf( context.getRepositoryId() ) );
        MessageDigests.updateWithString( digest, String.valueOf( context.getBranch() ) );
        MessageDigests.updateWithString( digest, params.getContentId().toString() );
        MessageDigests.updateWithString( digest, params.getBinaryReference().toString() );
        return "pending:" + getCachedImagePath( params, MessageDigests.formatHex( digest ) );
    }

    private String resolveAttachmentSha512( final NormalizedImageParams params )
    {
        if ( params.getAttachmentSha512() != null ) { return params.getAttachmentSha512(); }
        return contentService.getById( params.getContentId() ).getAttachments()
            .byName( params.getBinaryReference().toString() ).getSha512();
    }

    private ByteSource generateImage( final NormalizedImageParams params, final String checksum, final Path path ) throws IOException
    {
        if ( path != null )
        {
            return immutableFilesHelper.computeIfAbsent( path, sink -> {
                try (var prepared = prepareSource( params, checksum ))
                {
                    writePrepared( prepared, params, sink );
                }
                catch ( IOException e ) { throw new UncheckedIOException( e ); }
            }, nativeProcessing() ? queueTimeoutSeconds : 0 );
        }
        try (var prepared = prepareSource( params, null ))
        {
            return immutableFilesHelper.computeIfAbsent( getCachedImagePath( params, prepared.checksum() ),
                sink -> writePrepared( prepared, params, sink ), nativeProcessing() ? queueTimeoutSeconds : 0 );
        }
    }

    private PreparedImageSource prepareSource( final NormalizedImageParams params, final String checksum ) throws IOException
    {
        final ByteSource source = contentService.getBinary( params.getContentId(), params.getBinaryReference() );
        if ( source == null ) { throw new IllegalArgumentException( "No binary found for content " + params.getContentId() ); }
        return PreparedImageSource.copy( source, cacheFolder.resolve( "source" ), nativeProcessing() ? maxSourceBytes : Long.MAX_VALUE, checksum );
    }

    private void writePrepared( final PreparedImageSource source, final NormalizedImageParams params, final ByteSink sink )
    {
        try { createImage( source.path(), params, sink ); }
        catch ( IOException e ) { throw new UncheckedIOException( e ); }
    }

    private boolean nativeProcessing()
    {
        return useImageMagick || useImageMagickDecoder || useImageMagickTransformer;
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

        MessageDigests.updateWithIntLE( digest, readImageParams.getOrientation().ordinal() );

        MessageDigests.updateWithIntLE( digest, readImageParams.getBackgroundColor() );
        MessageDigests.updateWithIntLE( digest, readImageParams.getQuality() );
        MessageDigests.updateWithString( digest, readImageParams.getFormat() );
        MessageDigests.updateWithString( digest, readImageParams.getScaleParams().toString() );
        MessageDigests.updateWithString( digest, readImageParams.getFilterParam().toString() );

        // Preserve existing ImageIO and modern cache keys. Native legacy encodings have their own entries.
        if ( useImageMagick && !isModernFormat( readImageParams.getFormat() ) )
        {
            MessageDigests.updateWithString( digest, "ImageMagic" );
            MessageDigests.updateWithString( digest, Boolean.toString( progressiveOnFormats.contains( readImageParams.getFormat() ) ) );
        }
        if ( useImageMagickDecoder )
        {
            MessageDigests.updateWithString( digest, "decoding:ImageMagic" );
        }
        if ( useImageMagickTransformer )
        {
            MessageDigests.updateWithString( digest, "transformation:ImageMagic" );
        }
        final String hash = MessageDigests.formatHex( digest );
        return cacheFolder.resolve( "sha256" )
            .resolve( hash.substring( 0, 2 ) )
            .resolve( hash.substring( 2, 4 ) )
            .resolve( hash );
    }

    private void createImage( final Path source, final NormalizedImageParams params, final ByteSink sink ) throws IOException
    {
        try (ImageMagickDecoder.Source nativeSource = useImageMagickDecoder ? nativeDecoder.open( source ) : null;
             ImageInputStream stream = nativeSource == null ? ImageIO.createImageInputStream( source.toFile() ) : null)
        {
            final ImageReader reader = nativeSource == null ? getImageReader( stream ) : null;
            try
            {
                final int width = nativeSource == null ? reader.getWidth( 0 ) : nativeSource.width();
                final int height = nativeSource == null ? reader.getHeight( 0 ) : nativeSource.height();
                if ( nativeProcessing() && (long) width * height > maxProcessingPixels )
                {
                    throw new IllegalArgumentException( "Source image exceeds processing.maxPixels" );
                }
                final ImageTypeSpecifier type = reader == null ? null : reader.getRawImageType( 0 );
                final int pixelSize = type == null ? 4 : Math.max( 4, ( type.getColorModel().getPixelSize() + 7 ) / 8 );
                final boolean mayHaveAlpha = type == null || type.getColorModel().hasAlpha();
                final boolean rotate = params.getOrientation() != ImageOrientation.TopLeft;
                final boolean filters = !params.getFilterParam().isEmpty();
                final boolean background = !NormalizedImageParams.supportsAlpha( params.getFormat() ) && ( mayHaveAlpha || filters );
                final ImageScaleFunction scale = ScaleParams.NO_SCALE.getName().equals( params.getScaleParams().getName() ) ? null :
                    imageScaleFunctionBuilder.build( params.getScaleParams(), toCropRelativeFocalPoint( params.getFocalPoint(), params.getCropping() ) );
                final ImageGeometry geometry = ImageGeometry.calculate( width, height, params, scale,
                    nativeProcessing() ? maxProcessingPixels : Long.MAX_VALUE );
                final ImageMagickTransformPlan nativePlan;
                if ( useImageMagickTransformer )
                {
                    imageFilterBuilder.build( params.getFilterParam() );
                    nativePlan = ImageMagickTransformPlan.fromGeometry( width, height, params, geometry, maxProcessingPixels );
                }
                else { nativePlan = null; }

                // Account for source storage, rotated copies, resize intermediates and filter output before any raster allocation.
                final long peak = nativePlan == null ? geometry.peakPixels() : nativePlan.peakPixels();
                final long memory = (long) width * height * pixelSize * ( rotate && nativePlan == null ? 2 : 1 ) +
                    peak * pixelSize * 2 + ( scale == null || nativePlan != null ? 0 : (long) width * height * pixelSize / 3 );
                final int required = Math.max( 1, toMegaBytes( memory + 1_048_575 ) );
                final int permitted;
                if ( nativeProcessing() ) { circuitBreaker.tryAcquire( required ); permitted = required; }
                else { permitted = circuitBreaker.softTryAcquire( required ); }
                try
                {
                    if ( nativePlan != null )
                    {
                        try (var transformed = nativeSource == null ?
                            nativeTransformer.transform( reader.read( 0, reader.getDefaultReadParam() ), nativePlan ) :
                            nativeTransformer.transform( nativeSource.raster(), nativePlan ))
                        {
                            encode( params, sink, transformed.raster(), null );
                        }
                    }
                    else if ( nativeSource != null && useImageMagick && !rotate && !geometry.cropped() && scale == null && !filters && !background )
                    {
                        encode( params, sink, nativeSource.raster(), null );
                    }
                    else
                    {
                        BufferedImage image = nativeSource == null ? reader.read( 0, reader.getDefaultReadParam() ) : nativeSource.read();
                        requireNonNull( image, "BufferedImage is null" );
                        if ( rotate ) { image = applyRotation( image, params.getOrientation() ); }
                        image = geometry.apply( image );
                        if ( filters ) { image = imageFilterBuilder.build( params.getFilterParam() ).apply( image ); }
                        if ( background ) { image = ImageHelper.removeAlphaChannel( image, params.getBackgroundColor() ); }
                        if ( nativeProcessing() && (long) image.getWidth() * image.getHeight() > maxProcessingPixels )
                        {
                            throw new IllegalArgumentException( "Transformed image exceeds processing.maxPixels" );
                        }
                        encode( params, sink, null, image );
                    }
                }
                finally { circuitBreaker.release( permitted ); }
            }
            finally { if ( reader != null ) { reader.dispose(); } }
        }
    }

    private void encode( final NormalizedImageParams params, final ByteSink sink, final NativeImageRaster raster,
                         final BufferedImage image ) throws IOException
    {
        // Preserve legacy quality=0 semantics; modern encoders accept zero as an explicit quality.
        final int quality = params.getQuality() == 0 && !isModernFormat( params.getFormat() ) ? -1 : params.getQuality();
        final boolean progressive = progressiveOnFormats.contains( params.getFormat() );
        try (OutputStream output = sink.openBufferedStream())
        {
            if ( useImageMagick )
            {
                if ( raster != null ) { nativeEncoder.write( raster, params.getFormat(), quality, progressive, output ); }
                else { nativeEncoder.write( image, params.getFormat(), quality, progressive, output ); }
            }
            else { ImageHelper.writeImage( output, raster == null ? image : raster.read(), params.getFormat(), quality, progressive ); }
        }
    }

    private static boolean isModernFormat( final String format )
    {
        return "webp".equals( format ) || "avif".equals( format );
    }

    private static int toMegaBytes( long bytesValue )
    {
        return Math.toIntExact( bytesValue / 1024 / 1024 );
    }

    static FocalPoint toCropRelativeFocalPoint( final FocalPoint focalPoint, final Cropping cropping )
    {
        if ( cropping.isUnmodified() )
        {
            return focalPoint;
        }
        final double x = Math.clamp( ( focalPoint.xOffset() - cropping.left() ) / cropping.width(), 0.0, 1.0 );
        final double y = Math.clamp( ( focalPoint.yOffset() - cropping.top() ) / cropping.height(), 0.0, 1.0 );
        return new FocalPoint( x, y );
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
