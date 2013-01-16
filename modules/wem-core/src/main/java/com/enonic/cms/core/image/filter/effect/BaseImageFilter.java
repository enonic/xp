/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.image.filter.effect;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import com.enonic.cms.core.image.ImageHelper;
import com.enonic.cms.core.image.filter.ImageFilter;

public abstract class BaseImageFilter
    implements ImageFilter
{
    protected final BufferedImage createImage( BufferedImage src )
    {
        return ImageHelper.createImage( src, true );
    }

    protected final BufferedImage getScaledInstance( BufferedImage img, int targetWidth, int targetHeight )
    {
        return ImageHelper.getScaledInstance( img, targetWidth, targetHeight );
    }

    protected final Graphics2D getGraphics( BufferedImage img )
    {
        Graphics2D g = img.createGraphics();
        g.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        return g;
    }
}
