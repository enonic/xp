package com.enonic.xp.lib.auditlog;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.auditlog.AuditLog;
import com.enonic.xp.auditlog.AuditLogId;
import com.enonic.xp.auditlog.AuditLogParams;

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
        runScript( "/lib/xp/examples/auditlog/get.js" );
    }
}
