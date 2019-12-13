/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.xp.core.impl.image;

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

public final class ImageFilterBuilderImplTest
{

    private BufferedImage source;

    @BeforeEach
    public final void setUp()
        throws Exception
    {
        source = ImageIO.read( getClass().getResourceAsStream( "effect/source.jpg" ) );
    }

    @Test
    public void testEmpty()
    {
        final BufferedImage source = Mockito.mock( BufferedImage.class );

        final ImageFilterBuilderImpl imageFilterBuilder = new ImageFilterBuilderImpl();
        final ImageFilter imageFilter = imageFilterBuilder.build( "" );

        assertSame( source, imageFilter.filter( source ) );

    }

    @Test
    public void testInvalidQuery()
    {
        final ImageFilterBuilderImpl imageFilterBuilder = new ImageFilterBuilderImpl();
        final ImageFilter imageFilter = imageFilterBuilder.build( "invalid1(1);invalid2(2)" );

        assertSame( source, imageFilter.filter( source ) );
    }

    @Test
    public void testValidQuery()
    {
        final ImageFilterBuilderImpl imageFilterBuilder = new ImageFilterBuilderImpl();
        final ImageFilter imageFilter = imageFilterBuilder.build( "border(1);blur(2)" );

        assertNotSame( source, imageFilter.filter( source ) );
    }
}
