package com.enonic.xp.lib.auditlog;

import java.time.Instant;

import org.mockito.Mockito;

import com.enonic.xp.auditlog.AuditLog;
import com.enonic.xp.auditlog.AuditLogId;
import com.enonic.xp.auditlog.AuditLogParams;
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

    protected AuditLog.Builder auditLogBuilder( AuditLogParams p )
    {
        return AuditLog.create().
            id( AuditLogId.from( "90b976f7-55ab-48ef-acb8-e7c6f0744442" ) ).
            type( p.getType() ).
            time( Instant.ofEpochMilli( 1565599442767L ) ).
            source( p.getSource() ).
            user( p.getUser() ).
            message( p.getMessage() ).
            objectUris( p.getObjectUris() ).
            data( p.getData() );
    }
}
