package com.enonic.xp.core.impl.image;

public @interface ImageConfig
{
    int scale_maxDimension() default 8000;

    int filters_maxTotal() default 25;

    String memoryLimit() default "10%";

    String progressive() default "jpeg";

    // An empty executable disables modern-format encoding until explicitly configured.
    String encoding_executable() default "";

    int encoding_maxConcurrent() default 2;

    int encoding_maxQueue() default 8;

    int encoding_queueTimeoutSeconds() default 5;

    int encoding_timeoutSeconds() default 30;

    long encoding_maxPixels() default 40_000_000;
}
