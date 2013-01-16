package com.enonic.cms.core.image.filter.effect;

import java.awt.image.BufferedImage;

public final class ScaleBlockFilter
        extends BaseImageFilter
{

    private final int width;

    private final int height;

    private final float xOffset;

    private final float yOffset;

    public ScaleBlockFilter( int width, int height, float xOffset, float yOffset )
    {
        this.width = width;
        this.height = height;
        this.xOffset = Math.max( Math.min( xOffset, 1f ), 0 );
        this.yOffset = Math.max( Math.min( yOffset, 1f ), 0 );
    }

    public BufferedImage filter( BufferedImage source )
    {
        int sourceWidth = source.getWidth();
        int sourceHeight = source.getHeight();

        float ratio = (float) sourceWidth / (float) sourceHeight;
        float scale = 1f;

        int newWidth = this.width;
        int newHeight = this.height;

        if ( ratio > 1 )
        {
            scale = ratio;
            newHeight = (int) ( newWidth / scale );
        }
        else
        {
            scale = 1 / ratio;
            newWidth = (int) ( newHeight / scale );
        }

        int viewWidth = this.width;
        if ( newWidth < this.width )
        {
            newWidth = this.width;
            newHeight = (int) ( newWidth * scale );
            viewWidth = newWidth;
        }

        int viewHeight = this.height;
        if ( newHeight < this.height )
        {
            newHeight = this.height;
            newWidth = (int) ( newHeight * scale );
            viewHeight = newHeight;
        }

        int widthDiff = newWidth - viewWidth;
        int widthOffset = (int) ( widthDiff * this.xOffset );

        int heightDiff = newHeight - viewHeight;
        int heightOffset = (int) ( heightDiff * this.yOffset );

        BufferedImage targetImage = getScaledInstance( source, newWidth, newHeight );
        return targetImage.getSubimage( widthOffset, heightOffset, viewWidth, viewHeight );
    }

}