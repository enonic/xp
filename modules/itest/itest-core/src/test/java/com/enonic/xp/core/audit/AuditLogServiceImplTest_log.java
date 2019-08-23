package com.enonic.xp.core.audit;

import org.junit.Test;

import com.enonic.xp.audit.AuditLog;
import com.enonic.xp.audit.LogAuditLogParams;
import com.enonic.xp.core.impl.audit.AuditLogContext;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.repository.RepositoryNotFoundException;

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

    @Test(expected = RepositoryNotFoundException.class)
    public void log_anonymous()
    {
        LogAuditLogParams params = LogAuditLogParams.create().type( "test" ).build();
        auditLogService.log( params );
    }

    @Test
    public void get()
    {
        final LogAuditLogParams params = LogAuditLogParams.create().type( "test" ).build();

        final AuditLog log1 = AuditLogContext.createAdminContext().callWith( () -> auditLogService.log( params ) );
        final AuditLog log2 = AuditLogContext.createAdminContext().callWith( () -> auditLogService.get( log1.getId() ) );

        assertEquals( log1, log2 );
    }

}
