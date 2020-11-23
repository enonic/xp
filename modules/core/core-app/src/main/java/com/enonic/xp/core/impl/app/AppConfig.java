package com.enonic.xp.core.impl.app;

public @interface AppConfig
{
    String filter() default "*";
}
