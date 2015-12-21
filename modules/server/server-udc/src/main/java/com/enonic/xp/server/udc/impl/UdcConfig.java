package com.enonic.xp.server.udc.impl;

public @interface UdcConfig
{
    boolean enabled() default false;

    String url() default "https://udc.enonic.com";

    long delay() default 10 * 60 * 1000;

    long interval() default 24 * 60 * 60 * 1000;
}
