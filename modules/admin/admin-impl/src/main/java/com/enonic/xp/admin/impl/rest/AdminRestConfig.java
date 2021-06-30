package com.enonic.xp.admin.impl.rest;

public @interface AdminRestConfig
{
    String uploadMaxFileSize() default "100mb";

    String contentTypePatternMode() default "MATCH";
}
