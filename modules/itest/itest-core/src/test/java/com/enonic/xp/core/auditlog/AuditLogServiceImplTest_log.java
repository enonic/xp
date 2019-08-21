package com.enonic.xp.core.auditlog;

import org.junit.Test;

import com.enonic.xp.auditlog.AuditLog;
import com.enonic.xp.auditlog.AuditLogParams;
import com.enonic.xp.core.impl.auditlog.AuditLogContext;
import com.enonic.xp.node.NodeNotFoundException;

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

    @Test(expected = NodeNotFoundException.class)
    public void get_anonymous()
    {
        AuditLogParams params = AuditLogParams.create().type( "test" ).build();

        AuditLog log = auditLogService.log( params );
        auditLogService.get( log.getId() );
    }

    @Test
    public void get()
    {
        AuditLogParams params = AuditLogParams.create().type( "test" ).build();

        AuditLog log1 = auditLogService.log( params );
        AuditLog log2 = AuditLogContext.createAdminContext().callWith( () -> auditLogService.get( log1.getId() ) );

        assertEquals( log1, log2 );
    }

}
