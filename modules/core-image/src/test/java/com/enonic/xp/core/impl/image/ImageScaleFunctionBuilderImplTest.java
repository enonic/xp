/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.xp.core.impl.image;

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.core.impl.image.command.ScaleWidthFunctionCommand;
import com.enonic.xp.image.FocalPoint;
import com.enonic.xp.image.scale.ScaleParams;

import static org.junit.Assert.*;

public final class ImageScaleFunctionBuilderImplTest
{

    private BufferedImage source;

    @Before
    public final void setUp()
        throws Exception
    {
        source = ImageIO.read( getClass().getResourceAsStream( "effect/source.jpg" ) );
    }

    @Test
    public void testEmptyParam()
    {
        final ImageScaleFunctionBuilderImpl functionBuilder = new ImageScaleFunctionBuilderImpl();
        final ScaleParams scaleParams = new ScaleParams( "width", new Object[]{""} );
        final ImageScaleFunction scaleFunction = functionBuilder.build( scaleParams, FocalPoint.DEFAULT );

        BufferedImage scaled = scaleFunction.scale( source );

        assertEquals( ScaleWidthFunctionCommand.DEF_WIDTH_VALUE, scaled.getWidth() );
    }


    @Test
    public void testWidthParam()
    {
        final ImageScaleFunctionBuilderImpl functionBuilder = new ImageScaleFunctionBuilderImpl();
        final ScaleParams scaleParams = new ScaleParams( "width", new Object[]{"150"} );
        final ImageScaleFunction scaleFunction = functionBuilder.build( scaleParams, FocalPoint.DEFAULT );

        BufferedImage scaled = scaleFunction.scale( source );

        assertEquals( 150, scaled.getWidth() );
        assertNotEquals( scaled.getWidth(), source.getWidth() );
    }

    @Test
    public void testHeightParam()
    {
        final ImageScaleFunctionBuilderImpl functionBuilder = new ImageScaleFunctionBuilderImpl();
        final ScaleParams scaleParams = new ScaleParams( "height", new Object[]{"150"} );
        final ImageScaleFunction scaleFunction = functionBuilder.build( scaleParams, FocalPoint.DEFAULT );

        BufferedImage scaled = scaleFunction.scale( source );

        assertEquals( 150, scaled.getHeight() );
        assertNotEquals( scaled.getHeight(), source.getHeight() );
    }
}
