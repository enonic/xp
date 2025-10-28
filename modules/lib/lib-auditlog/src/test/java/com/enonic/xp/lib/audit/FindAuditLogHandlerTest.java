package com.enonic.xp.lib.audit;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.audit.AuditLog;
import com.enonic.xp.audit.AuditLogs;
import com.enonic.xp.audit.FindAuditLogParams;
import com.enonic.xp.audit.FindAuditLogResult;
import com.enonic.xp.audit.LogAuditLogParams;

import static org.junit.jupiter.api.Assertions.assertNotNull;


class FindAuditLogHandlerTest
    extends BaseAuditLogHandlerTest
{
    public void mockCreateLog()
    {
        AuditLog mocklog = auditLogBuilder( LogAuditLogParams.create().
            type( "testlog" ).build() ).
            source( "testbundle" ).
            build();

        Mockito.when( this.auditLogService.find( Mockito.any( FindAuditLogParams.class ) ) ).
            thenReturn( FindAuditLogResult.create().
                hits( AuditLogs.from( mocklog ) ).
                total( 2L ).
                build() );
    }

    @Test
    void testExample()
    {
        mockCreateLog();
        assertNotNull( runScript( "/lib/xp/examples/auditlog/find.js" ) );
    }
}
