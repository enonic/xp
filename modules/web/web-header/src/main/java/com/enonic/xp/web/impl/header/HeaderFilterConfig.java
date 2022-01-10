package com.enonic.xp.web.impl.header;

public @interface HeaderFilterConfig
{
    String headerConfig() default "set X-Frame-Options: DENY,set X-XSS-Protection: 1; mode=block,set X-Content-Type-Options: nosniff";
}
