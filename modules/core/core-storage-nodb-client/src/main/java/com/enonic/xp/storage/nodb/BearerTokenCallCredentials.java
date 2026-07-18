package com.enonic.xp.storage.nodb;

import java.util.concurrent.Executor;

import io.grpc.CallCredentials;
import io.grpc.Metadata;

/**
 * Attaches {@code authorization: Bearer <jwt>} to every call on a stub, matching the
 * shape {@code com.enonic.nodb.server.auth.TenantAuthInterceptor} verifies server-side
 * (see nodb/server/.../auth/TenantAuthInterceptor.java). Mirrors
 * {@code nodb/client-java}'s {@code NodbClient#bearerToken} anonymous implementation,
 * named here so it is independently unit-testable.
 */
final class BearerTokenCallCredentials
    extends CallCredentials
{
    private static final Metadata.Key<String> AUTHORIZATION = Metadata.Key.of( "authorization", Metadata.ASCII_STRING_MARSHALLER );

    private final String headerValue;

    BearerTokenCallCredentials( final String token )
    {
        this.headerValue = "Bearer " + token;
    }

    @Override
    public void applyRequestMetadata( final RequestInfo requestInfo, final Executor appExecutor, final MetadataApplier applier )
    {
        final Metadata headers = new Metadata();
        headers.put( AUTHORIZATION, headerValue );
        applier.apply( headers );
    }
}
