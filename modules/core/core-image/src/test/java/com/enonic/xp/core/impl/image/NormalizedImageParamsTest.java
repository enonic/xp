package com.enonic.xp.core.impl.image;

import org.junit.jupiter.api.Test;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.image.Cropping;
import com.enonic.xp.image.ReadImageParams;
import com.enonic.xp.image.ScaleParams;
import com.enonic.xp.util.BinaryReference;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NormalizedImageParamsTest
{
    @Test
    void normalizeFormat()
    {
        assertEquals( "JPEG", new NormalizedImageParams( noFormatTemplate().mimeType( "image/jpeg" ).build() ).getFormat() );
        assertEquals( "png", new NormalizedImageParams( noFormatTemplate().mimeType( "image/png" ).build() ).getFormat() );
        assertEquals( "gif", new NormalizedImageParams( noFormatTemplate().mimeType( "image/gif" ).build() ).getFormat() );
        assertThrows( IllegalArgumentException.class,
                      () -> new NormalizedImageParams( noFormatTemplate().mimeType( "image/bmp" ).build() ) );
    }


    @Test
    void normalizeNoScaleParams()
    {
        final ScaleParams scaleParams = new NormalizedImageParams( someFormatTemplate().build() ).getScaleParams();
        assertNull( scaleParams );
    }

    @Test
    void normalizeInsignificantCropping()
    {
        final ScaleParams scaleParams = new NormalizedImageParams(
            someFormatTemplate().cropping( Cropping.create().build() ).build() ).getScaleParams();
        assertNull( scaleParams );
    }

    @Test
    void normalizeScaleParams()
    {
        final ScaleParams scaleParams = new NormalizedImageParams(
            someFormatTemplate().scaleParams( new ScaleParams( "block", new Object[]{10, 15} ) )
                .scaleSquare( true )
                .scaleWidth( true )
                .scaleSize( 10 )
                .build() ).getScaleParams();
        assertEquals( "block", scaleParams.getName() );
        assertArrayEquals( new Object[]{10, 15}, scaleParams.getArguments() );
    }

    @Test
    void normalizeScaleSquire()
    {
        final ScaleParams scaleParams = new NormalizedImageParams(
            someFormatTemplate().scaleSquare( true ).scaleWidth( true ).scaleSize( 10 ).build() ).getScaleParams();
        assertEquals( "square", scaleParams.getName() );
        assertArrayEquals( new Object[]{10}, scaleParams.getArguments() );
    }

    @Test
    void normalizeScaleWidth()
    {
        final ScaleParams scaleParams = new NormalizedImageParams(
            someFormatTemplate().scaleSquare( false ).scaleWidth( true ).scaleSize( 10 ).build() ).getScaleParams();
        assertEquals( "width", scaleParams.getName() );
        assertArrayEquals( new Object[]{10}, scaleParams.getArguments() );
    }

    @Test
    void normalizeScaleMax()
    {
        final ScaleParams scaleParams = new NormalizedImageParams(
            someFormatTemplate().scaleSquare( false ).scaleWidth( false ).scaleSize( 10 ).build() ).getScaleParams();
        assertEquals( "max", scaleParams.getName() );
        assertArrayEquals( new Object[]{10}, scaleParams.getArguments() );
    }

    @Test
    void normalizeBackgroundColor()
    {
        assertEquals( 0xFFFFFF, new NormalizedImageParams( noFormatTemplate().mimeType( "image/jpeg" ).build() ).getBackgroundColor() );

        assertEquals( 0x00FF00, new NormalizedImageParams(
            noFormatTemplate().mimeType( "image/jpeg" ).backgroundColor( 0x00FF00 ).build() ).getBackgroundColor() );

        assertEquals( 0xFFFFFF, new NormalizedImageParams(
            noFormatTemplate().mimeType( "image/png" ).backgroundColor( 0x00FF00 ).build() ).getBackgroundColor() );
    }

    private static ReadImageParams.Builder someFormatTemplate()
    {
        return noFormatTemplate().mimeType( "image/png" );
    }

    private static ReadImageParams.Builder noFormatTemplate()
    {
        return ReadImageParams.newImageParams().contentId( ContentId.from( "123" ) ).binaryReference( BinaryReference.from( "456" ) );
    }
}
