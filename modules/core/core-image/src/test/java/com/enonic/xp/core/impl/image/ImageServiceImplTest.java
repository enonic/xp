package com.enonic.xp.core.impl.image;

import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.HexFormat;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.io.TempDir;

import com.google.common.io.ByteSource;

import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.attachment.Attachments;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.core.internal.security.MessageDigests;
import com.enonic.xp.image.Cropping;
import com.enonic.xp.image.FocalPoint;
import com.enonic.xp.image.ReadImageParams;
import com.enonic.xp.media.ImageOrientation;
import com.enonic.xp.util.BinaryReference;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class ImageServiceImplTest
{
    @TempDir
    public Path temporaryFolder;

    private ContentService contentService;

    private ImageServiceImpl imageService;

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

        imageConfig = mock( ImageConfig.class, invocation -> invocation.getMethod().getDefaultValue() );

        if ( info.getTags().contains( "progressive_disabled" ) )
        {
            when( imageConfig.progressive() ).thenReturn( "" );
        }

        ImageFilterBuilderImpl imageFilterBuilder = new ImageFilterBuilderImpl();
        imageFilterBuilder.activate( imageConfig );

        final ImageScaleFunctionBuilderImpl imageScaleFunctionBuilder = new ImageScaleFunctionBuilderImpl();

        imageScaleFunctionBuilder.activate( imageConfig );

        imageService = new ImageServiceImpl( contentService, imageScaleFunctionBuilder, imageFilterBuilder, imageConfig );
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
