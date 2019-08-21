package com.enonic.xp.core.impl.auditlog;

public @interface AuditLogConfig
{
    boolean enabled() default true;

    boolean outputLogs() default false;
}
