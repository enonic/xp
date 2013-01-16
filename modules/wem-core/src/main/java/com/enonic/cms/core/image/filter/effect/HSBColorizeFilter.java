/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.image.filter.effect;

import java.awt.Color;
import java.awt.image.RGBImageFilter;

public final class HSBColorizeFilter
    extends RGBImageFilter
{
    float fgHue;

    float fgSaturation;

    float fgBrightness;

    public HSBColorizeFilter( int rgb )
    {
        Color col = new Color( rgb );

        float[] hsbvals = new float[3];
        Color.RGBtoHSB( col.getRed(), col.getGreen(), col.getBlue(), hsbvals );

        fgHue = hsbvals[0];
        fgSaturation = hsbvals[1];
        fgBrightness = hsbvals[2];
        canFilterIndexColorModel = true;
    }

    public int filterRGB( int x, int y, int rgb )
    {
        int alpha = ( rgb >> 24 ) & 0xff;
        int red = ( rgb >> 16 ) & 0xff;
        int green = ( rgb >> 8 ) & 0xff;
        int blue = ( rgb ) & 0xff;
        float[] hsbvals = new float[3];

        Color.RGBtoHSB( red, green, blue, hsbvals );
        float newHue = fgHue;
        float newSaturation = hsbvals[1] * fgSaturation;
        float newBrightness = hsbvals[2] * ( hsbvals[1] * fgBrightness + ( 1 - hsbvals[1] ) );
        rgb = Color.HSBtoRGB( newHue, newSaturation, newBrightness );
        return ( rgb & 0x00ffffff ) | ( alpha << 24 );
    }
}
