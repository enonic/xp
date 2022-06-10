package com.enonic.xp.core.impl.image;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.google.common.io.ByteSource;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.image.Cropping;
import com.enonic.xp.image.ReadImageParams;
import com.enonic.xp.media.ImageOrientation;
import com.enonic.xp.util.BinaryReference;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
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

    @BeforeEach
    void setUp()
    {
        System.setProperty( "xp.home", temporaryFolder.toFile().getPath() );

        contentId = ContentId.from( "contentId" );
        binaryReference = BinaryReference.from( "binaryRef" );
        contentService = mock( ContentService.class );

        final ImageConfig imageConfig = mock( ImageConfig.class, invocation -> invocation.getMethod().getDefaultValue() );

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
        when( contentService.getBinaryKey( contentId, binaryReference ) ).thenReturn( "binaryKey" );
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
    @Deprecated
    void readImage_with_format()
        throws IOException
    {
        mockOriginalImage( "effect/transparent.png" );

        final ReadImageParams readImageParams =
            ReadImageParams.newImageParams().contentId( contentId ).binaryReference( binaryReference ).format( "png" ).build();
        final ByteSource imageData = imageService.readImage( readImageParams );

        assertArrayEquals( imageDataOriginal, imageData.read() );
    }

    @Test
    public void readImage_with_cache()
        throws IOException
    {
        mockOriginalImage( "effect/transparent.png" );

        Cropping cropping = Cropping.create().top( 0.25 ).bottom( 0.75 ).left( 0.25 ).right( 0.75 ).zoom( 2 ).build();

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

        verify( contentService, times( 2 ) ).getBinaryKey( contentId, binaryReference );
        verify( contentService ).getBinary( contentId, binaryReference );
        verifyNoMoreInteractions( contentService );
    }

    @Test
    public void readImage_filter_on_jpeg()
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
}
