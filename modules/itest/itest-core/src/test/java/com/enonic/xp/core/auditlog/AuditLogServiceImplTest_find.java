package com.enonic.xp.core.auditlog;

import org.junit.Test;

import com.enonic.xp.auditlog.AuditLog;
import com.enonic.xp.auditlog.AuditLogIds;
import com.enonic.xp.auditlog.AuditLogParams;
import com.enonic.xp.auditlog.FindAuditLogParams;
import com.enonic.xp.auditlog.FindAuditLogResult;
import com.enonic.xp.core.impl.auditlog.AuditLogContext;

import static org.junit.Assert.*;

public class AuditLogServiceImplTest_find
    extends AbstractAuditLogServiceTest
{

    @Override
    public void setUp()
        throws Exception
    {
        super.setUp();
    }

    @Test
    public void find_anonymous()
    {
        AuditLogParams params = AuditLogParams.create().type( "test" ).build();
        AuditLog log = auditLogService.log( params );

        FindAuditLogResult result = auditLogService.find( FindAuditLogParams.create().ids( AuditLogIds.from( log.getId() ) ).build() );
        assertEquals( 0L, result.getTotal() );
    }

    @Test
    public void find()
    {
        AuditLogParams params = AuditLogParams.create().type( "test" ).build();
        AuditLog log = auditLogService.log( params );
        FindAuditLogResult result = AuditLogContext.createAdminContext().callWith(
            () -> auditLogService.find( FindAuditLogParams.create().ids( AuditLogIds.from( log.getId() ) ).build() ) );
        assertEquals( 1L, result.getTotal() );
        assertEquals( log, result.getHits().first() );
    }

    @Test
    public void find_none()
    {
        AuditLogParams params = AuditLogParams.create().type( "test" ).build();
        auditLogService.log( params );
        FindAuditLogResult result =
            AuditLogContext.createAdminContext().callWith( () -> auditLogService.find( FindAuditLogParams.create().build() ) );
        assertEquals( 0L, result.getTotal() );
    }

}
