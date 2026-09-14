package com.enonic.xp.image;

import org.junit.jupiter.api.Test;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.media.ImageOrientation;
import com.enonic.xp.style.ImageStyle;
import com.enonic.xp.util.BinaryReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReadImageParamsTest
{
    @Test
    void defaultsDoNotBecomeExplicitOverridesWhenBuilderIsReused()
    {
        final ReadImageParams.Builder builder = ReadImageParams.newImageParams()
            .contentId( ContentId.from( "contentid" ) ).binaryReference( BinaryReference.from( "source" ) ).mimeType( "image/png" );
        assertNull( builder.quality );
        final ReadImageParams unstyled = builder.build();
        assertEquals( 0, unstyled.getQuality() );
        assertEquals( 0xFFFFFF, unstyled.getBackgroundColor() );
        assertNull( builder.quality );
        final ImageStyle style = ImageStyle.create().name( "card" ).build();
        assertSame( style, builder.style( style ).build().getStyle() );
    }

    @Test
    void explicitDefaultsConflictWithStyleIncludingDirectQualityAssignment()
    {
        final ReadImageParams.Builder builder = ReadImageParams.newImageParams()
            .contentId( ContentId.from( "contentid" ) ).binaryReference( BinaryReference.from( "source" ) ).mimeType( "image/png" )
            .style( ImageStyle.create().name( "card" ).build() );
        builder.quality = 0;
        assertThrows( IllegalArgumentException.class, builder::build );
        builder.quality = null;
        builder.quality( 0 );
        assertThrows( IllegalArgumentException.class, builder::build );
        builder.quality = null;
        builder.backgroundColor( 0xFFFFFF );
        assertThrows( IllegalArgumentException.class, builder::build );
    }

    @Test
    void test()
    {
        final ReadImageParams readImageParams = ReadImageParams.newImageParams().
            contentId( ContentId.from( "contentid" ) ).
            binaryReference( BinaryReference.from( "binaryReference" ) ).
            cropping( Cropping.create().build() ).
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

        assertEquals( ContentId.from( "contentid" ), readImageParams.getContentId() );
        assertEquals( BinaryReference.from( "binaryReference" ), readImageParams.getBinaryReference() );
        assertEquals( BinaryReference.from( "binaryReference" ), readImageParams.getBinaryReference() );
        assertEquals( Cropping.create().build(), readImageParams.getCropping() );
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
    void testWithoutMimeTypeAndFormat()
    {
        assertThrows( NullPointerException.class, () -> ReadImageParams.newImageParams().
            contentId( ContentId.from( "content-id" ) ).
            binaryReference( BinaryReference.from( "ref" ) ).
            build() );
    }

    @Test
    void testQuality()
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
    void testBackgroundColor()
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
