package com.enonic.xp.lib.auditlog;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.auditlog.AuditLog;
import com.enonic.xp.auditlog.AuditLogId;
import com.enonic.xp.auditlog.AuditLogParams;

import static org.junit.Assert.*;

public class GetAuditLogHandlerTest
    extends BaseAuditLogHandlerTest
{
    public void mockCreateLog()
    {
        AuditLog mocklog = auditLogBuilder( AuditLogParams.create().
            type( "testlog" ).build() ).
            source( "testbundle" ).
            message( "Fetched message" ).
            build();

        Mockito.when( this.auditLogService.get( Mockito.any( AuditLogId.class ) ) ).thenReturn( mocklog );
    }

    @Test
    public void testExample()
    {
        mockCreateLog();
        assertNotNull( runScript( "/lib/xp/examples/auditlog/get.js" ) );
    }
}
