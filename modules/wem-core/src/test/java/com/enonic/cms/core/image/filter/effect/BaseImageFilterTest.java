/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.image.filter.effect;

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import org.junit.Before;

public abstract class BaseImageFilterTest
{
    private BufferedImage opaque;

    private BufferedImage transparent;

    @Before
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
}
