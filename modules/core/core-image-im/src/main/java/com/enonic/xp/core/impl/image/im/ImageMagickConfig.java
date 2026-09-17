package com.enonic.xp.core.impl.image.im;

public @interface ImageMagickConfig
{
    // Absolute path to an ImageMagick executable installed outside XP. When set, it replaces the
    // bundled distribution. Renditions are cached without regard to which executable produced
    // them, so clear the image cache after pointing at a different installation.
    String imagemagick_executable() default "";
}
