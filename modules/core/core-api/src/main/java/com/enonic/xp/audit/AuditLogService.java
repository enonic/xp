package com.enonic.xp.audit;

import com.google.common.annotations.Beta;

@Beta
public interface AuditLogService
{
    AuditLog log( LogAuditLogParams params );

    AuditLog get( AuditLogId id );

    FindAuditLogResult find( FindAuditLogParams params );
}
