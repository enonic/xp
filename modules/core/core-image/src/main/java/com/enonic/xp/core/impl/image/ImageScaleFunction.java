package com.enonic.xp.core.impl.image;

import java.awt.image.BufferedImage;

public interface ImageScaleFunction
{
    BufferedImage scale( BufferedImage source );
}
