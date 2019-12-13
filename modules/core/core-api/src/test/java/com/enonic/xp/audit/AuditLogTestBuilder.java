package com.enonic.xp.audit;

import java.time.Instant;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.security.PrincipalKey;

public class AuditLogTestBuilder
{
    static AuditLogId id = AuditLogId.from( 123456789 );

    static String type = "testType";

    static Instant time = Instant.now();

    static String source = "testSource";

    static PrincipalKey user = PrincipalKey.ofSuperUser();

    static AuditLogUris objectUris = AuditLogUris.from( "a:b:c", "d:e:f" );

    static PropertyTree data = new PropertyTree();

    static
    {
        data.setBoolean( "testBool", true );
        data.setString( "testString", "myTest" );
    }

    static AuditLog.Builder getBuilder()
    {
        return AuditLog.create().
            id( id ).
            type( type ).
            time( time ).
            source( source ).
            user( user ).
            objectUris( objectUris ).
            data( data );
    }
}
