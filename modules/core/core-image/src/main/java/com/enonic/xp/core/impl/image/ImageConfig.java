package com.enonic.xp.core.impl.image;

public @interface ImageConfig
{
    int scale_maxDimension() default 8000;

    int filters_maxTotal() default 25;

    String memoryLimit() default "10%";

    String progressive() default "jpeg";
}
