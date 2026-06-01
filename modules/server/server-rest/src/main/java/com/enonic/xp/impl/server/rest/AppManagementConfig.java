package com.enonic.xp.impl.server.rest;

public @interface AppManagementConfig
{
    String installUrl_allowedUrls() default "https://repo.enonic.com/public/*";

    boolean installUrl_checksumRequired() default true;
}
