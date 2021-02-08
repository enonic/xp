package com.enonic.xp.core.impl.image.effect;

import java.awt.image.BufferedImage;

import org.junit.jupiter.api.Test;

import com.enonic.xp.image.FocalPoint;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FullScaleTest
    extends BaseImageFilterTest
{
    @Test
    public void testScale()
    {

        BufferedImage scaled = newScaleFunctions().full( FocalPoint.DEFAULT ).apply( getOpaque() );
        assertEquals( getOpaque().getWidth(), scaled.getWidth() );
        assertEquals( getOpaque().getHeight(), scaled.getHeight() );
    }
}
