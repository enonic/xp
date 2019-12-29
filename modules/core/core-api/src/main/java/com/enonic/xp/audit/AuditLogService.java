package com.enonic.xp.audit;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public interface AuditLogService
{
    AuditLog log( LogAuditLogParams params );

    AuditLog get( AuditLogId id );

    FindAuditLogResult find( FindAuditLogParams params );
}
