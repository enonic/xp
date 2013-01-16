/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.image.filter.effect;

import java.awt.image.BufferedImage;

public final class ScaleMaxFilter
    extends BaseImageFilter
{
    private final int size;

    public ScaleMaxFilter( int size )
    {
        this.size = size;
    }

    public BufferedImage filter( BufferedImage source )
    {
        int width = source.getWidth();
        int height = source.getHeight();
        int max = Math.max( width, height );

        float scale = (float) this.size / (float) max;
        int newWidth = (int) ( (float) width * scale );
        int newHeight = (int) ( (float) height * scale );

        return getScaledInstance( source, newWidth, newHeight );
    }
}
