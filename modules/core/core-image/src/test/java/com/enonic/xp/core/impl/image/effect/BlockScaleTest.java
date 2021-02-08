package com.enonic.xp.core.impl.image.effect;

import java.awt.image.BufferedImage;

import org.junit.jupiter.api.Test;

import com.enonic.xp.image.FocalPoint;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BlockScaleTest
    extends BaseImageFilterTest
{
    @Test
    public void testSize()
    {
        BufferedImage scaled = newScaleFunctions().block( FocalPoint.DEFAULT, 550, 320 ).apply( getOpaque() );
        assertEquals( 550, scaled.getWidth() );
        assertEquals( 320, scaled.getHeight() );

        BufferedImage scaled2 = newScaleFunctions().block( FocalPoint.DEFAULT, 550, 320 ).apply( getOpaque() );
        assertEquals( 550, scaled2.getWidth() );
        assertEquals( 320, scaled2.getHeight() );
    }
}
