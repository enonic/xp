package com.enonic.xp.core.impl.audit;

public @interface AuditLogConfig
{
    boolean enabled() default true;

    boolean outputLogs() default false;
}
