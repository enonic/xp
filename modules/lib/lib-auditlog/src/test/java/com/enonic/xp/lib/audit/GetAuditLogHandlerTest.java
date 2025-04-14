package com.enonic.xp.lib.audit;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.audit.AuditLog;
import com.enonic.xp.audit.AuditLogId;
import com.enonic.xp.audit.LogAuditLogParams;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class GetAuditLogHandlerTest
    extends BaseAuditLogHandlerTest
{
    public void mockCreateLog()
    {
        AuditLog mocklog = auditLogBuilder( LogAuditLogParams.create().
            type( "testlog" ).build() ).
            source( "testbundle" ).
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
