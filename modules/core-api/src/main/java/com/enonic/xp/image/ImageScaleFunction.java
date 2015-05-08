package com.enonic.xp.image;

import java.awt.image.BufferedImage;

public interface ImageScaleFunction
{
    BufferedImage scale( BufferedImage source );
}
