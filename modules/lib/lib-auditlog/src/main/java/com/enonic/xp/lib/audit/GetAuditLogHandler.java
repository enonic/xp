package com.enonic.xp.lib.audit;

import com.enonic.xp.audit.AuditLog;
import com.enonic.xp.audit.AuditLogId;
import com.enonic.xp.lib.audit.mapper.AuditLogMapper;

public class GetAuditLogHandler
    extends BaseAuditLogHandler
{
    private AuditLogId id;

    @Override
    protected Object doExecute()
    {
        final AuditLog auditLog = this.auditLogService.get( id );
        return auditLog == null ? null : new AuditLogMapper( auditLog );
    }

    public void setId( final String id )
    {
        this.id = id != null ? AuditLogId.from( id ) : null;
    }
}
