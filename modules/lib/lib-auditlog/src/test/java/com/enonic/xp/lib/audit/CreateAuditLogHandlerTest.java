package com.enonic.xp.lib.audit;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.audit.AuditLog;
import com.enonic.xp.audit.AuditLogUris;
import com.enonic.xp.audit.LogAuditLogParams;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.security.PrincipalKey;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class CreateAuditLogHandlerTest
    extends BaseAuditLogHandlerTest
{
    public void mockCreateLog()
    {
        PropertyTree data = new PropertyTree();
        data.setString( "custom", "string" );
        data.addDouble( "somevalue", 2.5 );

        AuditLog mocklog1 = auditLogBuilder( LogAuditLogParams.create().
            type( "testlog" ).build() ).
            source( "testbundle" ).
            build();

        AuditLog mocklog2 = auditLogBuilder( LogAuditLogParams.create().
            type( "testlog" ).
            source( "testbundle" ).
            user( PrincipalKey.ofAnonymous() ).
            objectUris( AuditLogUris.from( "some:resource:uri" ) ).
            data( data ).build() ).
            build();

        Mockito.when( this.auditLogService.log( Mockito.any( LogAuditLogParams.class ) ) ).
            thenReturn( mocklog1 ).
            thenReturn( mocklog2 );
    }

    @Test
    void testExample()
    {
        mockCreateLog();
        assertNotNull( runScript( "/lib/xp/examples/auditlog/log.js" ) );
    }
}
