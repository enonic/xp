package com.enonic.xp.image;

import java.awt.image.BufferedImage;

public interface ImageScaleFunction
{
    public BufferedImage scale( BufferedImage source );
}
