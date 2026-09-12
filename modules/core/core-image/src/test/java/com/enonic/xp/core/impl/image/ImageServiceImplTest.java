package com.enonic.xp.core.impl.image;

import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Files;
import java.util.HexFormat;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.api.io.TempDir;

import com.google.common.io.ByteSource;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.attachment.Attachments;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.core.internal.security.MessageDigests;
import com.enonic.xp.exception.ThrottlingException;
import com.enonic.xp.image.Cropping;
import com.enonic.xp.image.FocalPoint;
import com.enonic.xp.image.ReadImageParams;
import com.enonic.xp.image.ScaleParams;
import com.enonic.xp.media.ImageOrientation;
import com.enonic.xp.style.ImageStyle;
import com.enonic.xp.style.StyleDescriptor;
import com.enonic.xp.style.StyleDescriptorService;
import com.enonic.xp.util.BinaryReference;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class ImageServiceImplTest
{
    @TempDir
    public Path temporaryFolder;

    private ContentService contentService;

    private ImageServiceImpl imageService;

    private StyleDescriptorService styleDescriptorService;

    private ContentId contentId;

    private BinaryReference binaryReference;

    private byte[] imageDataOriginal;

    ImageConfig imageConfig;

    @BeforeEach
    void setUp( final TestInfo info )
    {
        System.setProperty( "xp.home", temporaryFolder.toFile().getPath() );

        contentId = ContentId.from( "contentid" );
        binaryReference = BinaryReference.from( "binaryRef" );
        contentService = mock( ContentService.class );

        styleDescriptorService = mock( StyleDescriptorService.class );

        imageConfig = mock( ImageConfig.class, invocation -> invocation.getMethod().getDefaultValue() );

        if ( info.getTags().contains( "progressive_disabled" ) )
        {
            when( imageConfig.progressive() ).thenReturn( "" );
        }

        imageService = newImageService();
    }

    private ImageServiceImpl newImageService()
    {
        ImageFilterBuilderImpl imageFilterBuilder = new ImageFilterBuilderImpl();
        imageFilterBuilder.activate( imageConfig );

        final ImageScaleFunctionBuilderImpl imageScaleFunctionBuilder = new ImageScaleFunctionBuilderImpl();

        imageScaleFunctionBuilder.activate( imageConfig );

        return new ImageServiceImpl( contentService, imageScaleFunctionBuilder, imageFilterBuilder, styleDescriptorService, imageConfig );
    }

    private void processingStyle( final Integer quality )
    {
        when( styleDescriptorService.getByApplication( ApplicationKey.from( "app" ) ) ).thenReturn(
            StyleDescriptor.create().application( ApplicationKey.from( "app" ) )
                .addStyleElement( ImageStyle.create().name( "card" ).quality( quality ).build() ).build() );
    }

    private ReadImageParams styledParams( final String format )
    {
        return styledParams( format, false );
    }

    private ReadImageParams styledParams( final String format, final boolean cacheOnly )
    {
        return ReadImageParams.newImageParams().contentId( contentId ).binaryReference( binaryReference )
            .attachmentSha512( HexFormat.of().formatHex( MessageDigests.sha512().digest( imageDataOriginal ) ) )
            .mimeType( "image/" + format ).scaleParams( new ScaleParams( "square", new Object[]{10} ) )
            .style( "app:card" ).cacheOnly( cacheOnly ).build();
    }

    @ParameterizedTest
    @ValueSource(strings = {"png", "jpeg", "webp", "avif"})
    void cacheOnlyServesExistingRenditionButNeverRegenerates( final String format )
        throws Exception
    {
        mockOriginalImage( "original.png" );
        processingStyle( 80 );
        final ReadImageParams cacheOnly = styledParams( format, true );
        final Path cache = temporaryFolder.resolve( "work/cache/img/sha256" );
        assertThrows( IllegalArgumentException.class, () -> imageService.readImage( cacheOnly ) );
        assertTrue( Files.notExists( cache ) );
        verifyNoInteractions( contentService );

        if ( "webp".equals( format ) || "avif".equals( format ) )
        {
            when( imageConfig.encoding_backend() ).thenReturn( "ImageMagic" );
            imageService = newImageService();
        }
        final byte[] expected = imageService.readImage( styledParams( format ) ).read();
        // A cached response needs neither an enabled encoder nor the original source bytes.
        when( imageConfig.encoding_backend() ).thenReturn( "ImageIO" );
        imageService = newImageService();
        assertArrayEquals( expected, imageService.readImage( cacheOnly ).read() );
        verify( contentService, times( 1 ) ).getBinary( contentId, binaryReference );

        // A changed style must not cause a cache-only request to write another rendition.
        processingStyle( 70 );
        assertThrows( IllegalArgumentException.class, () -> imageService.readImage( cacheOnly ) );
        processingStyle( 80 );
        try (var paths = Files.walk( cache ))
        {
            for ( Path path : paths.filter( Files::isRegularFile ).toList() )
            {
                Files.delete( path );
            }
        }
        assertThrows( IllegalArgumentException.class, () -> imageService.readImage( cacheOnly ) );
        verify( contentService, times( 1 ) ).getBinary( contentId, binaryReference );
        try (var paths = Files.walk( cache ))
        {
            assertEquals( 0, paths.filter( Files::isRegularFile ).count() );
        }
    }

    @Test
    void rejectsUnknownBackend()
    {
        when( imageConfig.encoding_backend() ).thenReturn( "typo" );
        assertThrows( IllegalArgumentException.class, this::newImageService );
    }

    @ParameterizedTest
    @ValueSource(strings = {"webp", "avif"})
    void imageIoBackendRejectsModernCacheMissBeforeReadingSource( final String format )
    {
        mockOriginalImage( "original.png" );
        processingStyle( 80 );
        assertThrows( IllegalArgumentException.class, () -> imageService.readImage( styledParams( format ) ) );
        verifyNoInteractions( contentService );
    }

    @ParameterizedTest
    @ValueSource(strings = {"jpeg", "png", "gif"})
    void switchingBackendUsesSeparateCacheEntries( final String format )
        throws Exception
    {
        mockOriginalImage( "original.png" );
        processingStyle( 80 );
        final byte[] original = imageService.readImage( styledParams( format ) ).read();
        when( imageConfig.encoding_backend() ).thenReturn( "ImageMagic" );
        imageService = newImageService();
        final byte[] nativeOutput = imageService.readImage( styledParams( format ) ).read();
        assertEquals( 10, javax.imageio.ImageIO.read( new java.io.ByteArrayInputStream( nativeOutput ) ).getWidth() );
        assertArrayEquals( nativeOutput, imageService.readImage( styledParams( format ) ).read() );
        when( imageConfig.encoding_backend() ).thenReturn( "ImageIO" );
        imageService = newImageService();
        assertArrayEquals( original, imageService.readImage( styledParams( format ) ).read() );
        verify( contentService, times( 2 ) ).getBinary( contentId, binaryReference );
    }

    @Test
    void cacheOnlyDoesNotReadSourceToDiscoverMissingChecksum()
    {
        final ReadImageParams params = ReadImageParams.newImageParams().contentId( contentId ).binaryReference( binaryReference )
            .mimeType( "image/png" ).cacheOnly( true ).build();
        assertThrows( IllegalArgumentException.class, () -> imageService.readImage( params ) );
        verifyNoInteractions( contentService );
    }

    @Test
    void rejectsUnknownAndInvalidStylesBeforeReadingContent()
    {
        assertThrows( IllegalArgumentException.class, () -> imageService.getStyle( "app:missing" ) );
        processingStyle( -1 );
        assertThrows( IllegalArgumentException.class, () -> imageService.getStyle( "app:card" ) );
        processingStyle( 101 );
        assertThrows( IllegalArgumentException.class, () -> imageService.getStyle( "app:card" ) );
        processingStyle( null );
        assertEquals( "card", imageService.getStyle( "app:card" ).getName() );
        verifyNoInteractions( contentService );
    }

    @Test
    void directModernEncodingWithoutStyleIsRejected()
    {
        for ( String format : new String[]{"webp", "avif"} )
        {
            assertThrows( IllegalArgumentException.class, () -> imageService.readImage(
                ReadImageParams.newImageParams().contentId( contentId ).binaryReference( binaryReference )
                    .mimeType( "image/" + format ).build() ) );
        }
        verifyNoInteractions( contentService );
    }

    @Test
    void styleChangeInvalidatesCachedImage()
        throws Exception
    {
        mockOriginalImage( "original.png" );
        processingStyle( 10 );
        final byte[] first = imageService.readImage( styledParams( "png" ) ).read();
        assertEquals( 10, ImageIO.read( new ByteArrayInputStream( first ) ).getWidth() );
        imageService.readImage( styledParams( "png" ) ).read();
        verify( contentService, times( 1 ) ).getBinary( contentId, binaryReference );

        processingStyle( 20 );
        final byte[] second = imageService.readImage( styledParams( "png" ) ).read();
        assertEquals( 10, ImageIO.read( new ByteArrayInputStream( second ) ).getWidth() );
        verify( contentService, times( 2 ) ).getBinary( contentId, binaryReference );
    }

    @Test
    void sameStyleCachesDifferentOutputFormatsSeparately()
        throws Exception
    {
        mockOriginalImage( "original.png" );
        processingStyle( 10 );
        final byte[] png = imageService.readImage( styledParams( "png" ) ).read();
        final byte[] jpeg = imageService.readImage( styledParams( "jpeg" ) ).read();
        assertEquals( 0x89, Byte.toUnsignedInt( png[0] ) );
        assertEquals( 0xff, Byte.toUnsignedInt( jpeg[0] ) );
        assertArrayEquals( png, imageService.readImage( styledParams( "png" ) ).read() );
        assertArrayEquals( jpeg, imageService.readImage( styledParams( "jpeg" ) ).read() );
        verify( contentService, times( 2 ) ).getBinary( contentId, binaryReference );
    }

    @Test
    void rejectsStyleChangedSinceFingerprintWasResolved()
    {
        processingStyle( 20 );
        final ReadImageParams params = ReadImageParams.newImageParams().contentId( contentId ).binaryReference( binaryReference )
            .mimeType( "image/webp" ).style( "app:card" )
            .expectedStyle( ImageStyle.create().name( "card" ).quality( 10 ).build() ).build();
        final IllegalArgumentException error = assertThrows( IllegalArgumentException.class, () -> imageService.readImage( params ) );
        assertTrue( error.getMessage().contains( "changed during request" ) );
        verifyNoInteractions( contentService );
    }

    @Test
    void oversizedSourceRejectedBeforeStartingEncoder()
    {
        mockOriginalImage( "original.png" );
        processingStyle( 10 );
        when( imageConfig.encoding_backend() ).thenReturn( "ImageMagic" );
        when( imageConfig.encoding_maxPixels() ).thenReturn( 1L );
        imageService = newImageService();
        final IllegalArgumentException error = assertThrows( IllegalArgumentException.class,
            () -> imageService.readImage( styledParams( "webp" ) ) );
        assertTrue( error.getMessage().contains( "Source image" ) );
    }

    @Test
    void boundedQueueRejectsExcessRequestsAndReleasesSlotsAfterFailure()
        throws Exception
    {
        mockOriginalImage( "original.png" );
        processingStyle( 10 );
        when( imageConfig.encoding_backend() ).thenReturn( "ImageMagic" );
        when( imageConfig.encoding_maxConcurrent() ).thenReturn( 1 );
        when( imageConfig.encoding_maxQueue() ).thenReturn( 0 );
        imageService = newImageService();
        final var started = new CountDownLatch( 1 );
        final var release = new CountDownLatch( 1 );
        when( contentService.getBinary( contentId, binaryReference ) ).thenAnswer( invocation -> {
            started.countDown();
            assertTrue( release.await( 10, TimeUnit.SECONDS ) );
            throw new IOException( "Source read failed" );
        } );
        try (var executor = Executors.newSingleThreadExecutor())
        {
            final var first = executor.submit( () -> assertThrows( IOException.class,
                () -> imageService.readImage( styledParams( "webp" ) ) ) );
            try
            {
                assertTrue( started.await( 5, TimeUnit.SECONDS ) );
                assertThrows( ThrottlingException.class, () -> imageService.readImage( styledParams( "webp" ) ) );
            }
            finally
            {
                release.countDown();
            }
            first.get( 10, TimeUnit.SECONDS );
        }
        // A failed conversion neither leaves a cache entry nor consumes the next request's slot.
        assertThrows( IOException.class, () -> imageService.readImage( styledParams( "webp" ) ) );
        verify( contentService, times( 2 ) ).getBinary( contentId, binaryReference );
    }

    private byte[] readImage( final String path )
    {
        try (InputStream is = getClass().getResourceAsStream( path ))
        {
            return is.readAllBytes();
        }
        catch ( IOException e )
        {
            throw new RuntimeException( e );
        }
    }

    private void mockOriginalImage( final String path )
    {
        imageDataOriginal = readImage( path );
        when( contentService.getBinary( contentId, binaryReference ) ).thenReturn( ByteSource.wrap( imageDataOriginal ) );

        final String sha512 = HexFormat.of().formatHex( MessageDigests.sha512().digest( imageDataOriginal ) );

        final Content content = mock( Content.class );
        final Attachment attachment =
            Attachment.create().name( binaryReference.toString() ).mimeType( "image/png" ).sha512( sha512 ).build();
        when( content.getAttachments() ).thenReturn( Attachments.from( attachment ) );
        when( contentService.getById( contentId ) ).thenReturn( content );
    }

    @Test
    void readImage_minimal()
        throws IOException
    {
        mockOriginalImage( "effect/transparent.png" );

        final ReadImageParams readImageParams =
            ReadImageParams.newImageParams().contentId( contentId ).binaryReference( binaryReference ).mimeType( "image/png" ).build();
        final ByteSource imageData = imageService.readImage( readImageParams );

        assertArrayEquals( imageDataOriginal, imageData.read() );
    }

    @Test
    void readImage_jpeg_progressive_default()
        throws IOException
    {
        mockOriginalImage( "original.png" );

        final ReadImageParams readImageParams =
            ReadImageParams.newImageParams().contentId( contentId ).binaryReference( binaryReference ).mimeType( "image/jpeg" ).build();

        ByteSource imageData = imageService.readImage( readImageParams );
        assertArrayEquals( readImage( "progressive.jpg" ), imageData.read() );
    }

    @Test
    @Tag("progressive_disabled")
    public void readImage_progressive_disabled()
        throws IOException
    {
        mockOriginalImage( "original.png" );

        final ReadImageParams readImageParams =
            ReadImageParams.newImageParams().contentId( contentId ).binaryReference( binaryReference ).mimeType( "image/jpeg" ).build();

        ByteSource imageData = imageService.readImage( readImageParams );
        assertArrayEquals( readImage( "not_progressive.jpg" ), imageData.read() );
    }

    @Test
    void readImage_with_cache()
        throws IOException
    {
        mockOriginalImage( "effect/transparent.png" );

        Cropping cropping = Cropping.create().top( 0.25 ).bottom( 0.75 ).left( 0.25 ).right( 0.75 ).build();

        final ReadImageParams readImageParams = ReadImageParams.newImageParams()
            .contentId( contentId )
            .binaryReference( binaryReference )
            .cropping( cropping )
            .scaleSize( 128 )
            .filterParam( "blur(10)" )
            .mimeType( "image/jpeg" )
            .backgroundColor( 0xFF0000 )
            .quality( 5 )
            .orientation( ImageOrientation.BottomLeft )
            .build();

        ByteSource imageData = imageService.readImage( readImageParams );

        assertArrayEquals( readImage( "processed.jpg" ), imageData.read() );

        imageData = imageService.readImage( readImageParams );
        assertArrayEquals( readImage( "processed.jpg" ), imageData.read() );

        verify( contentService, times( 2 ) ).getById( contentId );
        verify( contentService ).getBinary( contentId, binaryReference );
        verifyNoMoreInteractions( contentService );
    }

    @Test
    void toCropRelativeFocalPoint_remaps_original_into_crop_frame()
    {
        // Round-trip of issue #12107: the migration stored focal (0.5,0.5)-of-crop as original-relative
        // (0.44908, 0.49598); the renderer must map it back into the crop frame -> (0.5, 0.5).
        final Cropping cropping = Cropping.create()
            .left( 0.16319485 )
            .top( 0.21010056 )
            .right( 0.73495956 )
            .bottom( 0.78186527 )
            .build();

        final FocalPoint result = ImageServiceImpl.toCropRelativeFocalPoint( new FocalPoint( 0.44907720, 0.49598291 ), cropping );

        assertEquals( 0.5, result.xOffset(), 1e-4 );
        assertEquals( 0.5, result.yOffset(), 1e-4 );
    }

    @Test
    void toCropRelativeFocalPoint_returns_same_instance_when_unmodified()
    {
        final FocalPoint focalPoint = new FocalPoint( 0.3, 0.7 );

        assertSame( focalPoint, ImageServiceImpl.toCropRelativeFocalPoint( focalPoint, Cropping.DEFAULT ) );
    }

    @Test
    void toCropRelativeFocalPoint_clamps_point_outside_crop()
    {
        final Cropping cropping = Cropping.create().left( 0.5 ).top( 0.5 ).right( 1.0 ).bottom( 1.0 ).build();

        final FocalPoint result = ImageServiceImpl.toCropRelativeFocalPoint( new FocalPoint( 0.1, 0.9 ), cropping );

        assertEquals( 0.0, result.xOffset(), 1e-9 ); // 0.1 is left of the crop -> clamped to 0
        assertEquals( 0.8, result.yOffset(), 1e-9 ); // (0.9 - 0.5) / 0.5
    }

    @Test
    void readImage_cropping_out_of_bounds_does_not_throw()
    {
        mockOriginalImage( "effect/transparent.png" );

        // Legacy zoomed cropPosition that escaped the offline migration: edges exceed [0,1].
        // Before the fix this threw java.awt.image.RasterFormatException from BufferedImage.getSubimage.
        final Cropping cropping = Cropping.create().top( 0.367 ).left( 0.285 ).bottom( 1.367 ).right( 1.285 ).build();

        final ReadImageParams readImageParams = ReadImageParams.newImageParams()
            .contentId( contentId )
            .binaryReference( binaryReference )
            .cropping( cropping )
            .mimeType( "image/png" )
            .build();

        assertDoesNotThrow( () -> imageService.readImage( readImageParams ) );
    }

    @Test
    void readImage_filter_on_jpeg()
    {
        mockOriginalImage( "effect/source.jpg" );

        final ReadImageParams readImageParams = ReadImageParams.newImageParams()
            .contentId( contentId )
            .binaryReference( binaryReference )
            .filterParam( "sepia(10)" )
            .mimeType( "image/jpeg" )
            .orientation( ImageOrientation.BottomLeft )
            .build();

        assertDoesNotThrow( () -> imageService.readImage( readImageParams ) );
    }

    @Test
    void readImage_cmyk_is_flattened_to_srgb()
        throws IOException
    {
        mockOriginalImage( "effect/cmyk.jpg" );

        final ReadImageParams readImageParams = ReadImageParams.newImageParams()
            .contentId( contentId )
            .binaryReference( binaryReference )
            .mimeType( "image/jpeg" )
            .scaleSize( 128 )
            .build();

        final ByteSource imageData = imageService.readImage( readImageParams );

        final BufferedImage decoded;
        try (InputStream stream = imageData.openStream())
        {
            decoded = ImageIO.read( stream );
        }
        // CMYK JPEGs render unreliably across browsers (Firefox in particular). The scaler flattens
        // CMYK inputs to sRGB so the produced thumbnail is universally displayable.
        assertEquals( ColorSpace.TYPE_RGB, decoded.getColorModel().getColorSpace().getType() );
    }

    @Test
    void readImage_grayscale_jpeg_preserves_grayscale_after_scaling()
        throws IOException
    {
        mockOriginalImage( "effect/grayscale.jpg" );

        final ReadImageParams readImageParams = ReadImageParams.newImageParams()
            .contentId( contentId )
            .binaryReference( binaryReference )
            .mimeType( "image/jpeg" )
            .scaleSize( 32 )
            .build();

        final ByteSource imageData = imageService.readImage( readImageParams );

        final BufferedImage decoded;
        try (InputStream stream = imageData.openStream())
        {
            decoded = ImageIO.read( stream );
        }
        // Before the fix, the scaled JPEG came back as TYPE_3BYTE_BGR / sRGB because the
        // scale step in ImageHelper flattened the source ColorModel.
        assertEquals( BufferedImage.TYPE_BYTE_GRAY, decoded.getType() );
        assertEquals( ColorSpace.TYPE_GRAY, decoded.getColorModel().getColorSpace().getType() );
        assertEquals( 1, decoded.getColorModel().getNumComponents() );
    }
}
