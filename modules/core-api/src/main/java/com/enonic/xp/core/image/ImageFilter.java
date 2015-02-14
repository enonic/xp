package com.enonic.xp.core.image;

import java.awt.image.BufferedImage;

public interface ImageFilter
{
    public BufferedImage filter( BufferedImage source );
}
