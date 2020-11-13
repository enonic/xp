package com.enonic.xp.impl.task;

public @interface TaskConfig
{
    boolean offload_acceptInbound() default true;

    String clustered_timeout() default "PT5S";
}
