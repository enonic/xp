package com.enonic.xp.admin.impl.rest;

public @interface AdminRestConfig
{
    String contentTypePatternMode() default "MATCH";
}
