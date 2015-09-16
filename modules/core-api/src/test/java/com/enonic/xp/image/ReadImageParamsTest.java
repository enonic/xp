package com.enonic.xp.image;

import org.junit.Assert;
import org.junit.Test;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.media.ImageOrientation;
import com.enonic.xp.util.BinaryReference;

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

        Assert.assertEquals( ContentId.from( "contentId" ), readImageParams.getContentId() );
        Assert.assertEquals( BinaryReference.from( "binaryReference" ), readImageParams.getBinaryReference() );
        Assert.assertEquals( BinaryReference.from( "binaryReference" ), readImageParams.getBinaryReference() );
        Assert.assertEquals( Cropping.create().bottom( 1 ).right( 1 ).build(), readImageParams.getCropping() );
        Assert.assertEquals( "scaleParams", readImageParams.getScaleParams().getName() );
        Assert.assertEquals( FocalPoint.DEFAULT, readImageParams.getFocalPoint() );
        Assert.assertEquals( 1, readImageParams.getScaleSize() );
        Assert.assertTrue( readImageParams.isScaleSquare() );
        Assert.assertTrue( readImageParams.isScaleWidth() );
        Assert.assertEquals( "filterParam", readImageParams.getFilterParam() );
        Assert.assertEquals( 0xFF0000, readImageParams.getBackgroundColor() );
        Assert.assertEquals( "format", readImageParams.getFormat() );
        Assert.assertEquals( 2, readImageParams.getQuality() );
        Assert.assertEquals( ImageOrientation.BottomRight, readImageParams.getOrientation() );
    }
}
