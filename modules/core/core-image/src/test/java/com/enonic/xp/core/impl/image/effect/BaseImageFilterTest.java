/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.xp.core.impl.image.effect;

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.BeforeEach;

public abstract class BaseImageFilterTest
{
    private BufferedImage opaque;

    private BufferedImage transparent;

    @BeforeEach
    public final void setUp()
        throws Exception
    {
        this.opaque = ImageIO.read( getClass().getResourceAsStream( "source.jpg" ) );
        this.transparent = ImageIO.read( getClass().getResourceAsStream( "transparent.png" ) );
    }

    protected final BufferedImage getOpaque()
    {
        return this.opaque;
    }

    protected final BufferedImage getTransparent()
    {
        return this.transparent;
    }

    ImageScales newScaleFunctions()
    {
        return new ImageScales( 8000 );
    }

    ImageFilters newFilters()
    {
        return new ImageFilters();
    }
}
