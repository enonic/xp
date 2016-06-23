package com.enonic.xp.server.udc.impl;

public @interface UdcConfig
{
    boolean enabled() default true;

    String url() default "https://udc.enonic.com/collect";
}
