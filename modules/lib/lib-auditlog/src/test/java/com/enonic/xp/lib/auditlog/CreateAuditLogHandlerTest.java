package com.enonic.xp.lib.auditlog;

import java.net.URI;
import java.time.Instant;

import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.auditlog.AuditLog;
import com.enonic.xp.auditlog.AuditLogId;
import com.enonic.xp.auditlog.AuditLogParams;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.security.PrincipalKey;

public class CreateAuditLogHandlerTest
    extends BaseAuditLogHandlerTest
{
    private AuditLog.Builder create( AuditLogParams p )
    {
        return AuditLog.create().
            id( AuditLogId.from( 123 ) ).
            type( p.getType() ).
            time( Instant.ofEpochMilli( 1565599442767L ) ).
            source( p.getSource() ).
            user( p.getUser() ).
            message( p.getMessage() ).
            objectUris( p.getObjectUris() ).
            data( p.getData() );
    }

    public void mockCreateLog()
    {
        PropertyTree data = new PropertyTree();
        data.setString( "custom", "string" );
        data.addDouble( "somevalue", 2.5 );

        AuditLogParams params1 = AuditLogParams.create().
            type( "testlog" ).
            build();

        AuditLog mocklog1 = create( AuditLogParams.create().
            type( "testlog" ).build() ).
            source( "testbundle" ).
            build();

        AuditLog mocklog2 = create( AuditLogParams.create().
            type( "testlog" ).
            source( "testbundle" ).
            user( PrincipalKey.ofAnonymous() ).
            message( "Audit log message" ).
            objectUris( ImmutableSet.of( URI.create( "some:resource:uri" ) ) ).
            data( data ).build() ).
            build();

        Mockito.when( this.auditLogService.log( Mockito.any( AuditLogParams.class ) ) ).
            thenReturn( mocklog1 ).
            thenReturn( mocklog2 );
    }

    @Test
    public void testExample()
    {
        mockCreateLog();
        runScript( "/lib/xp/examples/auditlog/log.js" );
    }
}
