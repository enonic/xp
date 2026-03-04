package com.enonic.xp.audit;

public interface AuditLogService
{
    AuditLog log( LogAuditLogParams params );

    AuditLog get( AuditLogId id );

    FindAuditLogResult find( FindAuditLogParams params );

    CleanUpAuditLogResult cleanUp( CleanUpAuditLogParams params );
}
