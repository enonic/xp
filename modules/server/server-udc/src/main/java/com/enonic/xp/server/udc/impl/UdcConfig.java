package com.enonic.xp.server.udc.impl;

public @interface UdcConfig
{
    boolean enabled() default true;

    String url() default "http://udc.enonic.com/collect";
}
