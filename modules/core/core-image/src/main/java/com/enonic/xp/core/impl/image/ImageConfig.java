package com.enonic.xp.core.impl.image;

public @interface ImageConfig
{
    int scale_maxDimension() default 8000;

    int filters_maxTotal() default 25;

    String memoryLimit() default "10%";

    String progressive() default "jpeg";

    // ImageIO preserves existing output. ImageMagic uses the bundled native output encoder.
    String encoding_backend() default "ImageIO";

    // Source decoding is independent of output encoding and transformations.
    String decoding_backend() default "ImageIO";

    String decoding_maxBytes() default "256mb";

    String transformation_backend() default "ImageIO";

    // Shared limits for all native processing stages.
    int processing_maxConcurrent() default 2;

    int processing_maxQueue() default 8;

    int processing_queueTimeoutSeconds() default 5;

    int processing_timeoutSeconds() default 30;

    long processing_maxPixels() default 40_000_000;
}
