package com.enonic.xp.core.impl.image;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.image.Cropping;
import com.enonic.xp.image.ImageFilter;
import com.enonic.xp.image.ImageFilterBuilder;
import com.enonic.xp.image.ReadImageParams;
import com.enonic.xp.media.ImageOrientation;
import com.enonic.xp.util.BinaryReference;

public class ImageServiceImplTest
{
    private ContentService contentService;

    private ImageFilter imageFilter;

    private ImageServiceImpl imageService;

    private ContentId contentId;

    private BinaryReference binaryReference;

    private byte[] imageDataOriginal;

    @Before
    public void setUp()
        throws IOException
    {
        final TemporaryFolder temporaryFolder = new TemporaryFolder();
        temporaryFolder.create();
        System.setProperty( "xp.home", temporaryFolder.getRoot().getPath() );

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
    public void testGetFormatByMineType()
        throws IOException
    {
        final String format = imageService.getFormatByMimeType( "image/jpeg" );
        Assert.assertEquals( "JPEG", format );

        boolean ioExceptionCaught = false;
        try
        {
            imageService.getFormatByMimeType( "image/unknown" );
        }
        catch ( IOException e )
        {
            ioExceptionCaught = true;
        }
        Assert.assertTrue( ioExceptionCaught );
    }

    @Test
    public void testReadImageMinimal()
        throws IOException
    {
        final ReadImageParams readImageParams =
            ReadImageParams.newImageParams().contentId( contentId ).binaryReference( binaryReference ).format( "png" ).build();
        final byte[] imageData = imageService.readImage( readImageParams );

        Assert.assertArrayEquals( imageDataOriginal, imageData );
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
            format( "JPEG" ).
            backgroundColor( 0xFF0000 ).
            quality( 5 ).
            orientation( ImageOrientation.BottomLeft ).
            build();

        byte[] imageData = imageService.readImage( readImageParams );
        Assert.assertArrayEquals( ByteStreams.toByteArray( getClass().getResourceAsStream( "processed.jpg" ) ), imageData );
        Mockito.verify( imageFilter ).filter( Mockito.any() );

        imageData = imageService.readImage( readImageParams );
        Assert.assertArrayEquals( ByteStreams.toByteArray( getClass().getResourceAsStream( "processed.jpg" ) ), imageData );
        Mockito.verify( imageFilter ).filter( Mockito.any() );
    }


}
