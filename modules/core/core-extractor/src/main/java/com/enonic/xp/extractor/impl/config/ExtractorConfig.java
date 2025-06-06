package com.enonic.xp.extractor.impl.config;

public @interface ExtractorConfig
{
    int body_size_limit() default 500_000;
}
