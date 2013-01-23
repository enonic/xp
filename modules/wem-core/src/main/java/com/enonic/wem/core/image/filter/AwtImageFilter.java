/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.wem.core.image.filter;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageProducer;

import com.enonic.wem.core.image.ImageHelper;

public final class AwtImageFilter
    implements ImageFilter
{
    private final java.awt.image.ImageFilter filter;

    public AwtImageFilter( java.awt.image.ImageFilter filter )
    {
        this.filter = filter;
    }

    public BufferedImage filter( BufferedImage source )
    {
        ImageProducer producer = new FilteredImageSource( source.getSource(), this.filter );
        return convert( Toolkit.getDefaultToolkit().createImage( producer ) );
    }

    private BufferedImage convert( Image image )
    {
        BufferedImage bufferedImage = ImageHelper.createImage( image.getWidth( null ), image.getHeight( null ), true );
        Graphics2D g = bufferedImage.createGraphics();
        g.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        g.drawImage( image, 0, 0, null );
        g.dispose();
        return bufferedImage;
    }
}
