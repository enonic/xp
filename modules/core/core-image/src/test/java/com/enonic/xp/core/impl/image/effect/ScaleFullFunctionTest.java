package com.enonic.xp.core.impl.image.effect;

import java.awt.image.BufferedImage;

import org.junit.Test;

import com.enonic.xp.core.impl.image.ImageScaleFunction;
import com.enonic.xp.core.impl.image.ScaleFullFunction;

import static org.junit.Assert.*;

public class ScaleFullFunctionTest
    extends BaseImageFilterTest
{
    @Test
    public void testScale()
    {
        BufferedImage scaled = scale();
        assertEquals( getOpaque().getWidth(), scaled.getWidth() );
        assertEquals( getOpaque().getHeight(), scaled.getHeight() );
    }

    private BufferedImage scale()
    {
        ImageScaleFunction scaleFunction = new ScaleFullFunction();
        return scaleFunction.scale( getOpaque() );
    }
}