/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.wem.core.image.filter.effect;

import java.awt.Color;
import java.awt.image.RGBImageFilter;

public final class ColorizeFilter
    extends RGBImageFilter
{
    private final float rBoost;

    private final float gBoost;

    private final float bBoost;

    public ColorizeFilter( float r, float g, float b )
    {
        this.rBoost = r < 0 ? 0 : r;
        this.gBoost = g < 0 ? 0 : g;
        this.bBoost = b < 0 ? 0 : b;
        this.canFilterIndexColorModel = true;
    }

    public int filterRGB( int x, int y, int rgb )
    {
        Color c = new Color( rgb );
        int r = c.getRed();
        int g = c.getGreen();
        int b = c.getBlue();

        int gry = ( r + g + b ) / 3;
        r = g = b = gry;

        r = (int) ( r * this.rBoost );
        g = (int) ( g * this.gBoost );
        b = (int) ( b * this.bBoost );
        if ( r > 255 )
        {
            r = 255;
        }
        if ( g > 255 )
        {
            g = 255;
        }
        if ( b > 255 )
        {
            b = 255;
        }

        int alpha = ( rgb >> 24 ) & 0xff;
        rgb = new Color( r, g, b ).getRGB();

        return ( rgb & 0x00ffffff ) | ( alpha << 24 );
    }
}