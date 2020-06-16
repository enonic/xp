package com.enonic.xp.core.impl.image;

import java.io.IOException;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;

import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.image.Cropping;
import com.enonic.xp.image.ReadImageParams;
import com.enonic.xp.media.ImageOrientation;
import com.enonic.xp.util.BinaryReference;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class ImageServiceImplTest
{
    @TempDir
    public Path temporaryFolder;

    private ContentService contentService;

    private ImageFilter imageFilter;

    private ImageServiceImpl imageService;

    private ContentId contentId;

    private BinaryReference binaryReference;

    private byte[] imageDataOriginal;

    @BeforeEach
    public void setUp()
        throws IOException
    {
        System.setProperty( "xp.home", temporaryFolder.toFile().getPath() );

        contentId = ContentId.from( "contentId" );
        binaryReference = BinaryReference.from( "binaryRef" );
        contentService = Mockito.mock( ContentService.class );
        imageDataOriginal = ByteStreams.toByteArray( getClass().getResourceAsStream( "effect/transparent.png" ) );
        Mockito.when( contentService.getBinary( contentId, binaryReference ) ).thenReturn( ByteSource.wrap( imageDataOriginal ) );
        Mockito.when( contentService.getBinaryKey( contentId, binaryReference ) ).thenReturn( "binaryKey" );

        ImageFilterBuilder imageFilterBuilder = Mockito.mock( ImageFilterBuilder.class );
        imageFilter = Mockito.mock( ImageFilter.class );
        Mockito.when( imageFilter.filter( Mockito.any() ) ).thenAnswer( invocation -> invocation.getArguments()[0] );
        Mockito.when( imageFilterBuilder.build( Mockito.any() ) ).thenReturn( imageFilter );

        imageService = new ImageServiceImpl();
        imageService.setContentService( contentService );
        imageService.setImageFilterBuilder( imageFilterBuilder );
    }

    @Test
    public void testReadImageMinimal()
        throws IOException
    {
        final ReadImageParams readImageParams =
            ReadImageParams.newImageParams().contentId( contentId ).binaryReference( binaryReference ).mimeType( "image/png" ).build();
        final ByteSource imageData = imageService.readImage( readImageParams );

        assertArrayEquals( imageDataOriginal, imageData.read() );
    }

    @Test
    @Deprecated
    public void testReadImageWithFormat()
        throws IOException
    {
        final ReadImageParams readImageParams =
            ReadImageParams.newImageParams().contentId( contentId ).binaryReference( binaryReference ).format( "png" ).build();
        final ByteSource imageData = imageService.readImage( readImageParams );

        assertArrayEquals( imageDataOriginal, imageData.read() );
    }

    @Test
    public void testReadImageWithCache()
        throws IOException
    {
        Cropping cropping = Cropping.create().top( 0.25 ).bottom( 0.75 ).left( 0.25 ).right( 0.75 ).zoom( 2 ).build();

        final ReadImageParams readImageParams = ReadImageParams.newImageParams().
            contentId( contentId ).
            binaryReference( binaryReference ).
            cropping( cropping ).
            scaleSize( 128 ).
            filterParam( "blur(10)" ).
            mimeType( "image/jpeg" ).
            backgroundColor( 0xFF0000 ).
            quality( 5 ).
            orientation( ImageOrientation.BottomLeft ).
            build();

        ByteSource imageData = imageService.readImage( readImageParams );
        assertArrayEquals( ByteStreams.toByteArray( getClass().getResourceAsStream( "processed.jpg" ) ), imageData.read() );
        Mockito.verify( imageFilter ).filter( Mockito.any() );

        imageData = imageService.readImage( readImageParams );
        assertArrayEquals( ByteStreams.toByteArray( getClass().getResourceAsStream( "processed.jpg" ) ), imageData.read() );
        Mockito.verify( imageFilter ).filter( Mockito.any() );
    }


}
