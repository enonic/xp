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
import com.enonic.xp.image.ReadImageParams;
import com.enonic.xp.util.BinaryReference;

public class ImageServiceImplTest
{
    private ContentService contentService;

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

        imageService = new ImageServiceImpl();
        imageService.setContentService( contentService );
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
            ReadImageParams.newImageParams().contentId( contentId ).binaryReference( binaryReference ).format( "PNG" ).build();
        final byte[] imageData = imageService.readImage( readImageParams );
        Assert.assertArrayEquals( imageDataOriginal, imageData );
    }


}
