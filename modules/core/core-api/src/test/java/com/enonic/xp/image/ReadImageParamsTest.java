package com.enonic.xp.image;

import org.junit.jupiter.api.Test;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.media.ImageOrientation;
import com.enonic.xp.util.BinaryReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
            format( "format" ).
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
        assertEquals( "format", readImageParams.getFormat() );
        assertEquals( 2, readImageParams.getQuality() );
        assertEquals( ImageOrientation.BottomRight, readImageParams.getOrientation() );
    }
}
