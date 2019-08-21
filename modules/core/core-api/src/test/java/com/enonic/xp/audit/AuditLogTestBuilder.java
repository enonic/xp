package com.enonic.xp.audit;

import java.net.URI;
import java.time.Instant;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.security.PrincipalKey;

public class AuditLogTestBuilder
{
    static AuditLogId id = AuditLogId.from( 123456789 );

    static String type = "testType";

    static Instant time = Instant.now();

    static String source = "testSource";

    static PrincipalKey user = PrincipalKey.ofSuperUser();

    static String message = "testMessage";

    static ImmutableSet<URI> objectUris = ImmutableSet.<URI>builder().add( URI.create( "a:b:c" ), URI.create( "d:e:f" ) ).build();

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
            message( message ).
            objectUris( objectUris ).
            data( data );
    }
}
