package com.enonic.xp.web.impl.header;

public @interface HeaderFilterConfig
{
    String headerConfig() default "set X-Frame-Options: DENY,set X-Content-Type-Options: nosniff";
}
