package com.enonic.xp.core.impl.image;

public @interface ImageConfig
{
    int scale_maxDimension() default 8000;

    int filters_maxTotal() default 25;

    String memoryLimit() default "10%";

    String progressive() default "jpeg";

    // ImageIO preserves existing output. ImageMagic uses the bundled native output encoder.
    String encoding_backend() default "ImageIO";

    // Source decoding is independent of output encoding. Transformations use XP's image pipeline.
    String decoding_backend() default "ImageIO";

    long decoding_maxBytes() default 67_108_864;

    int encoding_maxConcurrent() default 2;

    int encoding_maxQueue() default 8;

    int encoding_queueTimeoutSeconds() default 5;

    int encoding_timeoutSeconds() default 30;

    long encoding_maxPixels() default 40_000_000;
}
