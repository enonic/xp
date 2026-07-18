package com.enonic.nodb.server.auth;

import java.util.Set;

import com.auth0.jwt.exceptions.JWTVerificationException;

import io.grpc.Context;
import io.grpc.Contexts;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.Status;

/**
 * The one place tenant identity enters the server (DESIGN.md §7.2): reads the
 * {@code authorization: Bearer <jwt>} header, verifies it, and resolves it to a {@link
 * TenantPrincipal} placed in the gRPC {@link Context} — never trusting anything from the
 * request payload. Also enforces the two-scope model (DESIGN.md §7.3): management RPCs
 * (repo/branch lifecycle today; snapshots/bulk-transfer would join this set if
 * implemented) require {@code operator} scope, every other RPC accepts {@code runtime} or
 * {@code operator}.
 *
 * <p>Missing/invalid/expired/wrong-audience token, or a syntactically invalid tenant
 * claim &rarr; {@link Status#UNAUTHENTICATED}. Valid token, wrong scope for a management
 * RPC &rarr; {@link Status#PERMISSION_DENIED}.
 */
public final class TenantAuthInterceptor
    implements ServerInterceptor
{
    /** Service impls read the resolved identity for the current call via this key. */
    public static final Context.Key<TenantPrincipal> PRINCIPAL_KEY = Context.key( "nodb-tenant-principal" );

    private static final Metadata.Key<String> AUTHORIZATION =
        Metadata.Key.of( "authorization", Metadata.ASCII_STRING_MARSHALLER );

    /**
     * Full gRPC method names ({@code <service>/<method>}) that require operator scope.
     * Everything else on a registered service accepts runtime or operator.
     */
    private static final Set<String> MANAGEMENT_METHODS =
        Set.of( "enonic.nodb.v1.RepositoryAdmin/CreateRepository", "enonic.nodb.v1.RepositoryAdmin/DeleteRepository" );

    private final JwtVerifier jwtVerifier;

    public TenantAuthInterceptor( JwtVerifier jwtVerifier )
    {
        this.jwtVerifier = jwtVerifier;
    }

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall( ServerCall<ReqT, RespT> call, Metadata headers,
                                                                    ServerCallHandler<ReqT, RespT> next )
    {
        String header = headers.get( AUTHORIZATION );
        if ( header == null || !header.startsWith( "Bearer " ) )
        {
            call.close( Status.UNAUTHENTICATED.withDescription( "Missing bearer token" ), new Metadata() );
            return noopListener();
        }

        String token = header.substring( "Bearer ".length() ).trim();
        TenantPrincipal principal;
        try
        {
            principal = jwtVerifier.verify( token );
            // Fails fast on a syntactically invalid tenant claim (TenantContext's own
            // validation) rather than deferring to whatever store call happens first.
            principal.tenantContext();
        }
        catch ( JWTVerificationException | IllegalArgumentException e )
        {
            call.close( Status.UNAUTHENTICATED.withDescription( "Invalid token: " + e.getMessage() ), new Metadata() );
            return noopListener();
        }

        String fullMethodName = call.getMethodDescriptor().getFullMethodName();
        if ( MANAGEMENT_METHODS.contains( fullMethodName ) && principal.scope() != Scope.OPERATOR )
        {
            call.close( Status.PERMISSION_DENIED.withDescription( "Operator scope required for " + fullMethodName ), new Metadata() );
            return noopListener();
        }

        Context context = Context.current().withValue( PRINCIPAL_KEY, principal );
        return Contexts.interceptCall( context, call, headers, next );
    }

    private static <ReqT> ServerCall.Listener<ReqT> noopListener()
    {
        return new ServerCall.Listener<>()
        {
        };
    }
}
