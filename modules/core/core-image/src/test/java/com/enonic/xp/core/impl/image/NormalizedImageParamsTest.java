package com.enonic.xp.core.impl.image;

import org.junit.jupiter.api.Test;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.image.Cropping;
import com.enonic.xp.image.ReadImageParams;
import com.enonic.xp.image.ScaleParams;
import com.enonic.xp.style.ImageStyle;
import com.enonic.xp.util.BinaryReference;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NormalizedImageParamsTest
{
    @Test
    void modernFormatsRequireResolvedStyle()
    {
        for ( String format : new String[]{"webp", "avif"} )
        {
            assertThrows( IllegalArgumentException.class,
                          () -> new NormalizedImageParams( noFormatTemplate().mimeType( "image/" + format ).build() ) );
            // Merely supplying an unverified style name must not enable the encoder.
            assertThrows( IllegalArgumentException.class,
                          () -> new NormalizedImageParams( noFormatTemplate().mimeType( "image/" + format ).style( "app:card" ).build() ) );
        }
    }

    @Test
    void resolvedStyleOwnsAllProcessingParameters()
    {
        final ImageStyle style = ImageStyle.create().name( "card" ).scale( "block(640,360)" )
            .format( "webp" ).quality( 75 ).filter( "grayscale()" ).build();
        final NormalizedImageParams params = new NormalizedImageParams( noFormatTemplate().mimeType( "image/webp" )
            .scaleParams( new ScaleParams( "square", new Object[]{8000} ) ).filterParam( "blur(100)" )
            .quality( 100 ).backgroundColor( 0 ).build(), style );
        assertEquals( "block(640,360)", params.getScaleParams().toString() );
        assertEquals( "webp", params.getFormat() );
        assertEquals( 75, params.getQuality() );
        assertEquals( "grayscale()", params.getFilterParam().toString() );
        assertEquals( 0xFFFFFF, params.getBackgroundColor() );
    }

    @Test
    void outputTypeMustMatchResolvedStyle()
    {
        final ImageStyle style = ImageStyle.create().name( "card" ).scale( "max(100)" ).format( "avif" ).build();
        assertThrows( IllegalArgumentException.class,
                      () -> new NormalizedImageParams( noFormatTemplate().mimeType( "image/webp" ).build(), style ) );
        assertEquals( 85, new NormalizedImageParams( noFormatTemplate().mimeType( "image/avif" ).build(), style ).getQuality() );
    }

    @Test
    void normalizeFormat()
    {
        assertEquals( "jpeg", new NormalizedImageParams( noFormatTemplate().mimeType( "image/jpeg" ).build() ).getFormat() );
        assertEquals( "png", new NormalizedImageParams( noFormatTemplate().mimeType( "image/png" ).build() ).getFormat() );
        assertEquals( "gif", new NormalizedImageParams( noFormatTemplate().mimeType( "image/gif" ).build() ).getFormat() );
        assertThrows( IllegalArgumentException.class,
                      () -> new NormalizedImageParams( noFormatTemplate().mimeType( "image/bmp" ).build() ) );
    }


    @Test
    void normalizeNoScaleParams()
    {
        final ScaleParams scaleParams = new NormalizedImageParams( someFormatTemplate().build() ).getScaleParams();
        assertEquals( "full()", scaleParams.toString() );
    }

    @Test
    void normalizeInsignificantCropping()
    {
        final ScaleParams scaleParams =
            new NormalizedImageParams( someFormatTemplate().cropping( Cropping.create().build() ).build() ).getScaleParams();
        assertEquals( "full()", scaleParams.toString() );
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
