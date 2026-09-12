package com.enonic.xp.core.impl.image;

import org.junit.jupiter.api.Test;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.core.impl.image.effect.ScaleCalculator;
import com.enonic.xp.core.impl.image.effect.ScaledFunction;
import com.enonic.xp.image.Cropping;
import com.enonic.xp.image.ReadImageParams;
import com.enonic.xp.media.ImageOrientation;
import com.enonic.xp.util.BinaryReference;

import static org.junit.jupiter.api.Assertions.*;

class ImageGeometryTest
{
    @Test
    void rejectsResizeThatExceedsLimitOnlyAfterCropping()
    {
        final var params = params( Cropping.create().right( 0.01 ).build(), ImageOrientation.TopLeft );
        final var scale = new ScaledFunction( ScaleCalculator.width( 1000 ) );
        assertEquals( 1_000_000, scale.estimateResolution( 1000, 1000 ) );
        assertThrows( IllegalArgumentException.class, () -> ImageGeometry.calculate( 1000, 1000, params, scale, 40_000_000 ) );
    }

    @Test
    void orientationPrecedesCroppingAndScale()
    {
        final var params = params( Cropping.create().right( 0.5 ).build(), ImageOrientation.RightTop );
        final var geometry = ImageGeometry.calculate( 100, 40, params, new ScaledFunction( ScaleCalculator.width( 10 ) ), 10000 );
        assertEquals( 20, geometry.cropWidth() );
        assertEquals( 100, geometry.cropHeight() );
        assertEquals( 10, geometry.scale().newWidth );
        assertEquals( 50, geometry.scale().newHeight );
    }

    private NormalizedImageParams params( final Cropping crop, final ImageOrientation orientation )
    {
        return new NormalizedImageParams( ReadImageParams.newImageParams().contentId( ContentId.from( "id" ) )
            .binaryReference( BinaryReference.from( "source" ) ).mimeType( "image/png" ).cropping( crop ).orientation( orientation ).build() );
    }
}
