package com.enonic.xp.web.impl.serializer;

public @interface WebSerializerConfig
{
    long DEFAULT_MAX_REQUEST_BODY_SIZE = 10L * 1024 * 1024;

    /**
     * Maximum size in bytes of a text or JSON request body read into memory.
     */
    long http_maxRequestBodySize() default DEFAULT_MAX_REQUEST_BODY_SIZE;
}
