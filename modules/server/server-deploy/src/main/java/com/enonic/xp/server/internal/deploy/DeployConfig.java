package com.enonic.xp.server.internal.deploy;

public @interface DeployConfig
{
    long interval() default 1000;
}
