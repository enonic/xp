package com.enonic.xp.core.impl.image;

import java.awt.image.BufferedImage;

public interface ImageFunction
{
    BufferedImage apply( BufferedImage source );
}
