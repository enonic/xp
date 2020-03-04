package com.enonic.xp.web.session.impl;

public @interface WebSessionStoreConfig
{
    String storeMode() default "non-persistent";

    int savePeriodSeconds() default 0;

    int gracePeriodSeconds() default 3600;
}
