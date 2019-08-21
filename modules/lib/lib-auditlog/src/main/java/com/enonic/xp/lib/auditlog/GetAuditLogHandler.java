package com.enonic.xp.lib.auditlog;

import com.enonic.xp.auditlog.AuditLogId;
import com.enonic.xp.lib.auditlog.mapper.AuditLogMapper;

public class GetAuditLogHandler
    extends BaseAuditLogHandler
{
    private AuditLogId id;

    @Override
    protected Object doExecute()
    {
        return new AuditLogMapper( this.auditLogService.get( id ) );
    }

    public void setId( final String id )
    {
        this.id = id != null ? AuditLogId.from( id ) : null;
    }
}
