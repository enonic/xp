package com.enonic.xp.lib.audit;

import java.time.Instant;

import org.mockito.Mockito;

import com.enonic.xp.audit.AuditLog;
import com.enonic.xp.audit.AuditLogId;
import com.enonic.xp.audit.AuditLogService;
import com.enonic.xp.audit.LogAuditLogParams;
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

    protected AuditLog.Builder auditLogBuilder( LogAuditLogParams p )
    {
        return AuditLog.create().
            id( AuditLogId.from( "90b976f7-55ab-48ef-acb8-e7c6f0744442" ) ).
            type( p.getType() ).
            time( Instant.ofEpochMilli( 1565599442767L ) ).
            source( p.getSource() ).
            user( p.getUser() ).
            objectUris( p.getObjectUris() ).
            data( p.getData() );
    }
}
