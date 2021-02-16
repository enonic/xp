package com.enonic.xp.image;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.media.ImageOrientation;
import com.enonic.xp.util.BinaryReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ReadImageParamsTest
{
    @Test
    public void test()
    {
        final ReadImageParams readImageParams = ReadImageParams.newImageParams().
            contentId( ContentId.from( "contentId" ) ).
            binaryReference( BinaryReference.from( "binaryReference" ) ).
            cropping( Cropping.create().bottom( 1 ).right( 1 ).build() ).
            scaleParams( new ScaleParams( "scaleParams", null ) ).
            focalPoint( FocalPoint.DEFAULT ).
            scaleSize( 1 ).
            scaleSquare( true ).
            scaleWidth( true ).
            filterParam( "filterParam" ).
            backgroundColor( 0xFF0000 ).
            mimeType( "image/png" ).
            quality( 2 ).
            orientation( ImageOrientation.BottomRight ).
            build();

        assertEquals( ContentId.from( "contentId" ), readImageParams.getContentId() );
        assertEquals( BinaryReference.from( "binaryReference" ), readImageParams.getBinaryReference() );
        assertEquals( BinaryReference.from( "binaryReference" ), readImageParams.getBinaryReference() );
        assertEquals( Cropping.create().bottom( 1 ).right( 1 ).build(), readImageParams.getCropping() );
        assertEquals( "scaleParams", readImageParams.getScaleParams().getName() );
        assertEquals( FocalPoint.DEFAULT, readImageParams.getFocalPoint() );
        assertEquals( 1, readImageParams.getScaleSize() );
        assertTrue( readImageParams.isScaleSquare() );
        assertTrue( readImageParams.isScaleWidth() );
        assertEquals( "filterParam", readImageParams.getFilterParam() );
        assertEquals( 0xFF0000, readImageParams.getBackgroundColor() );
        assertEquals( "image/png", readImageParams.getMimeType() );
        assertEquals( 2, readImageParams.getQuality() );
        assertEquals( ImageOrientation.BottomRight, readImageParams.getOrientation() );
    }

    @Test
    public void testWithoutMimeTypeAndFormat()
        throws IOException
    {
        assertThrows( IllegalArgumentException.class, () -> ReadImageParams.newImageParams().
            contentId( ContentId.from( "content-id" ) ).
            binaryReference( BinaryReference.from( "ref" ) ).
            build() );
    }

    @Test
    public void testWithBothMimeTypeAndFormat()
        throws IOException
    {
        assertThrows( IllegalArgumentException.class, () -> ReadImageParams.newImageParams().
            contentId( ContentId.from( "content-id" ) ).
            binaryReference( BinaryReference.from( "ref" ) ).format( "PNG" ).mimeType( "image/jpeg" ).
            build() );
    }

    @Test
    public void testQuality()
    {
        assertThrows( IllegalArgumentException.class, () -> ReadImageParams.newImageParams().
            contentId( ContentId.from( "content-id" ) ).
            binaryReference( BinaryReference.from( "ref" ) ).
            mimeType( "image/png" ).
            quality( 101 ).
            build() );

        assertThrows( IllegalArgumentException.class, () -> ReadImageParams.newImageParams().
            contentId( ContentId.from( "content-id" ) ).
            binaryReference( BinaryReference.from( "ref" ) ).
            mimeType( "image/png" ).
            quality( -1 ).
            build() );

        assertEquals( 1, ReadImageParams.newImageParams().
            contentId( ContentId.from( "content-id" ) ).
            binaryReference( BinaryReference.from( "ref" ) ).
            mimeType( "image/png" ).
            quality( 1 ).
            build().getQuality() );

        assertEquals( 0, ReadImageParams.newImageParams().
            contentId( ContentId.from( "content-id" ) ).
            binaryReference( BinaryReference.from( "ref" ) ).
            mimeType( "image/png" ).
            build().getQuality() );
    }

    @Test
    public void testBackgroundColor()
    {
        assertThrows( IllegalArgumentException.class, () -> ReadImageParams.newImageParams().
            contentId( ContentId.from( "content-id" ) ).
            binaryReference( BinaryReference.from( "ref" ) ).
            mimeType( "image/png" ).
            backgroundColor( Integer.MAX_VALUE ).
            build() );

        assertThrows( IllegalArgumentException.class, () -> ReadImageParams.newImageParams().
            contentId( ContentId.from( "content-id" ) ).
            binaryReference( BinaryReference.from( "ref" ) ).
            mimeType( "image/png" ).
            backgroundColor( -1 ).
            build() );

        assertEquals( 1, ReadImageParams.newImageParams().
            contentId( ContentId.from( "content-id" ) ).
            binaryReference( BinaryReference.from( "ref" ) ).
            mimeType( "image/png" ).
            backgroundColor( 1 ).
            build().getBackgroundColor() );

        assertEquals( 0xFFFFFF, ReadImageParams.newImageParams().
            contentId( ContentId.from( "content-id" ) ).
            binaryReference( BinaryReference.from( "ref" ) ).
            mimeType( "image/png" ).
            build().getBackgroundColor() );
    }
}
