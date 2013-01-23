package com.enonic.wem.core.image;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

public final class ImageHelper
{
    public static BufferedImage createImage( BufferedImage src, boolean hasAlpha )
    {

        return createImage( src.getWidth(), src.getHeight(), hasAlpha );
    }

    public static BufferedImage createImage( int width, int height, boolean hasAlpha )
    {
        return new BufferedImage( width, height, hasAlpha ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB );
    }

    public static BufferedImage getScaledInstance( BufferedImage img, int targetWidth, int targetHeight )
    {
        int width = Math.max( 1, targetWidth );
        int height = Math.max( 1, targetHeight );

        Image scaledImage = img.getScaledInstance( width, height, Image.SCALE_SMOOTH );
        BufferedImage targetImage = createImage( width, height, true );
        Graphics g = targetImage.createGraphics();
        g.drawImage( scaledImage, 0, 0, null );
        g.dispose();
        return targetImage;
    }
}
