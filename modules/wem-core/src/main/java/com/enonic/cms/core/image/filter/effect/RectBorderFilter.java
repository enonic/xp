/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.image.filter.effect;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.TexturePaint;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public final class RectBorderFilter
    extends BaseImageFilter
{
    private final int size;

    private final int color;

    public RectBorderFilter( int size, int color )
    {
        this.size = size;
        this.color = color;
    }

    public BufferedImage filter( BufferedImage source )
    {
        BufferedImage dest = createImage( source );
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
}
