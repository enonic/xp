package com.enonic.xp.core.impl.image.effect;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import com.enonic.xp.image.ImageHelper;

public abstract class BaseImageProcessor
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
