package com.enonic.xp.core.impl.image;

import java.awt.image.BufferedImage;

public interface ImageFilter
{
    BufferedImage filter( BufferedImage source );
}
