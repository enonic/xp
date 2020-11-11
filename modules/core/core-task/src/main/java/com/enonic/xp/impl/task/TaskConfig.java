package com.enonic.xp.impl.task;

public @interface TaskConfig
{
    boolean offload_acceptInbound() default true;

    String offload_outboundTimeout() default "PT5S";
}
