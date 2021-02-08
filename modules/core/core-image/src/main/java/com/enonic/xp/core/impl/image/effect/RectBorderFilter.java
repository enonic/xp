/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.xp.core.impl.image.effect;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import com.enonic.xp.core.impl.image.ImageFunction;
import com.enonic.xp.image.ImageHelper;

public final class RectBorderFilter
    implements ImageFunction
{
    private final int size;

    private final int color;

    public RectBorderFilter( int size, int color )
    {
        this.size = size;
        this.color = color;
    }

    @Override
    public BufferedImage apply( BufferedImage source )
    {
        BufferedImage dest = ImageHelper.createImage( source, true );
        Graphics2D g = getGraphics( dest );
        g.setPaint( new Color( this.color, false ) );

        // drawing the border around image consists of of 4 rectangles with thickness = this.size
        if ( this.size > 0 )
        {
            g.fillRect( 0, 0, source.getWidth(), this.size );
            g.fillRect( 0, 0, this.size, source.getHeight() );
            g.fillRect( source.getWidth() - this.size, 0, this.size, source.getHeight() );
            g.fillRect( 0, source.getHeight() - this.size, source.getWidth(), this.size );
        }

        g.setPaint( new TexturePaint( source, new Rectangle2D.Float( 0, 0, source.getWidth(), source.getHeight() ) ) );
        g.fillRect( this.size, this.size, source.getWidth() - ( this.size * 2 ), source.getHeight() - ( this.size * 2 ) );
        g.dispose();

        return dest;
    }

    private static Graphics2D getGraphics( final BufferedImage img )
    {
        Graphics2D g = img.createGraphics();
        g.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        return g;
    }
}
