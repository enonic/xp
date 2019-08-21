package com.enonic.xp.lib.auditlog;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.auditlog.AuditLog;
import com.enonic.xp.auditlog.AuditLogParams;
import com.enonic.xp.auditlog.AuditLogs;
import com.enonic.xp.auditlog.FindAuditLogParams;
import com.enonic.xp.auditlog.FindAuditLogResult;

import static org.junit.Assert.*;

public class FindAuditLogHandlerTest
    extends BaseAuditLogHandlerTest
{
    public void mockCreateLog()
    {
        AuditLog mocklog = auditLogBuilder( AuditLogParams.create().
            type( "testlog" ).build() ).
            source( "testbundle" ).
            message( "Fetched message" ).
            build();

        Mockito.when( this.auditLogService.find( Mockito.any( FindAuditLogParams.class ) ) ).
            thenReturn( FindAuditLogResult.create().
                hits( AuditLogs.from( mocklog ) ).
                total( 2L ).
                build() );
    }

    @Test
    public void testExample()
    {
        mockCreateLog();
        assertNotNull( runScript( "/lib/xp/examples/auditlog/find.js" ) );
    }
}
