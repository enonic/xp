package com.enonic.xp.impl.task;

public @interface TaskConfig
{
    boolean distributable_acceptInbound() default true;

    boolean distributable_acceptSystem() default true;

    String clustered_timeout() default "PT5S";
}
