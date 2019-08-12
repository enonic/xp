package com.enonic.xp.lib.auditlog;

import org.mockito.Mockito;

import com.enonic.xp.auditlog.AuditLogService;
import com.enonic.xp.testing.ScriptTestSupport;

public abstract class BaseAuditLogHandlerTest
    extends ScriptTestSupport
{
    protected AuditLogService auditLogService;

    @Override
    public void initialize()
        throws Exception
    {
        super.initialize();

        this.auditLogService = Mockito.mock( AuditLogService.class );
        addService( AuditLogService.class, this.auditLogService );
    }
}
