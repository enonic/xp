package com.enonic.xp.server.udc.impl;

public @interface UdcConfig
{
    boolean enabled() default false;

    String url() default "http://udc.enonic.com/collect";

    long delay() default 10 * 60 * 1000; // 10 minutes

    long interval() default 24 * 60 * 60 * 1000; // 24 hours
}
