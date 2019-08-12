package com.enonic.xp.core.auditlog;

import org.junit.Test;

import com.enonic.xp.auditlog.AuditLog;
import com.enonic.xp.auditlog.AuditLogParams;

import static org.junit.Assert.*;

public class AuditLogServiceImplTest_log
    extends AbstractAuditLogServiceTest
{

    @Override
    public void setUp()
        throws Exception
    {
        super.setUp();
    }

    @Test
    public void log_and_get()
    {
        AuditLogParams params = AuditLogParams.create().type( "test" ).build();

        AuditLog log1 = auditLogService.log( params );
        AuditLog log2 = auditLogService.get( log1.getId() );

        assertEquals( log1, log2 );
    }

}
