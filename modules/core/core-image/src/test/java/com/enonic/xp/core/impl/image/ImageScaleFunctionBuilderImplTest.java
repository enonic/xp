/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.xp.core.impl.image;

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.image.FocalPoint;
import com.enonic.xp.image.ScaleParams;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

public final class ImageScaleFunctionBuilderImplTest
{

    private BufferedImage source;

    @BeforeEach
    public final void setUp()
        throws Exception
    {
        source = ImageIO.read( getClass().getResourceAsStream( "effect/source.jpg" ) );
    }

    @Test
    void testEmptyParam()
    {
        final ImageScaleFunctionBuilderImpl functionBuilder = new ImageScaleFunctionBuilderImpl();
        functionBuilder.activate( mock( ImageConfig.class, invocation -> invocation.getMethod().getDefaultValue() ) );

        final ScaleParams scaleParams = new ScaleParams( "width", new Object[]{""} );
        final ImageFunction scaleFunction = functionBuilder.build( scaleParams, FocalPoint.DEFAULT );

        BufferedImage scaled = scaleFunction.apply( source );

        assertEquals( 100, scaled.getWidth() );
    }


    @Test
    void testWidthParam()
    {
        final ImageScaleFunctionBuilderImpl functionBuilder = new ImageScaleFunctionBuilderImpl();
        functionBuilder.activate( mock( ImageConfig.class, invocation -> invocation.getMethod().getDefaultValue() ) );

        final ScaleParams scaleParams = new ScaleParams( "width", new Object[]{"150"} );
        final ImageFunction scaleFunction = functionBuilder.build( scaleParams, FocalPoint.DEFAULT );

        BufferedImage scaled = scaleFunction.apply( source );

        assertEquals( 150, scaled.getWidth() );
        assertNotEquals( scaled.getWidth(), source.getWidth() );
    }

    @Test
    void testHeightParam()
    {
        final ImageScaleFunctionBuilderImpl functionBuilder = new ImageScaleFunctionBuilderImpl();
        functionBuilder.activate( mock( ImageConfig.class, invocation -> invocation.getMethod().getDefaultValue() ) );

        final ScaleParams scaleParams = new ScaleParams( "height", new Object[]{"150"} );
        final ImageFunction scaleFunction = functionBuilder.build( scaleParams, FocalPoint.DEFAULT );

        BufferedImage scaled = scaleFunction.apply( source );

        assertEquals( 150, scaled.getHeight() );
        assertNotEquals( scaled.getHeight(), source.getHeight() );
    }

    @Test
    void unknownScaleFunction()
    {
        final ImageScaleFunctionBuilderImpl functionBuilder = new ImageScaleFunctionBuilderImpl();
        functionBuilder.activate( mock( ImageConfig.class, invocation -> invocation.getMethod().getDefaultValue() ) );

        final ScaleParams scaleParams = new ScaleParams( "fake", new Object[0] );
        assertThrows( IllegalArgumentException.class, () -> functionBuilder.build( scaleParams, FocalPoint.DEFAULT ) );
    }

}
