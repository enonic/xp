package com.enonic.xp.core.impl.image.effect;

import java.awt.image.BufferedImage;

import org.junit.Test;

import com.enonic.xp.image.ImageFilter;
import com.enonic.xp.image.ImageScaleFunction;

import static org.junit.Assert.*;

public class ScaleBlockFunctionTest
    extends BaseImageFilterTest
{
    @Test
    public void testSize()
    {
        BufferedImage scaled = scale( 550, 320 );
        assertEquals( 550, scaled.getWidth() );
        assertEquals( 320, scaled.getHeight() );

        BufferedImage scaled2 = scaleWithOffset( 550, 320, 0.1f, 0.3f );
        assertEquals( 550, scaled2.getWidth() );
        assertEquals( 320, scaled2.getHeight() );
    }

    private BufferedImage scale( int width, int height )
    {
        ImageScaleFunction scaleFunction = new ScaleBlockFunction( width, height, 0.5f, 0.5f );
        return scaleFunction.scale( getOpaque() );
    }

    private BufferedImage scaleWithOffset( int width, int height, float xOffset, float yOffset )
    {
        ImageScaleFunction scaleFunction = new ScaleBlockFunction( width, height, xOffset, yOffset );
        return scaleFunction.scale( getOpaque() );
    }
}
