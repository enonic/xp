package com.enonic.xp.core.impl.audit.config;

public interface AuditLogConfig
{
    boolean isEnabled();

    boolean isOutputLogs();

    String ageThreshold();
}
