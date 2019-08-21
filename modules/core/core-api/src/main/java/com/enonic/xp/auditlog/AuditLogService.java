package com.enonic.xp.auditlog;

import com.google.common.annotations.Beta;

@Beta
public interface AuditLogService
{
    AuditLog log( AuditLogParams params );

    AuditLog get( AuditLogId id );
}
