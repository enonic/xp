package com.enonic.xp.core.impl.image;

import java.awt.image.BufferedImage;
import java.util.function.Function;

public interface ImageFunction
    extends Function<BufferedImage, BufferedImage>
{
    BufferedImage apply( BufferedImage source );
}
