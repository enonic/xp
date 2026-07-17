package com.enonic.nodb.server.auth;

import java.nio.file.Path;
import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;

/**
 * The "trivial dev issuer" CLI (DESIGN.md §9 Phase 1 gate: "trivial local issuer"). Mints
 * an RS256 token for a tenant/scope pair, generating the dev keypair on first use.
 * Standalone equivalent of {@code nodb token --tenant t1}; a real deployment replaces this
 * with the control plane, which is why all the actual issuing logic lives in {@link
 * JwtIssuer} rather than here.
 *
 * <pre>
 *   java -cp ... com.enonic.nodb.server.auth.NodbTokenTool --tenant acme --scope runtime [--subject svc:xp] [--keys-dir DIR] [--ttl-minutes N]
 * </pre>
 */
public final class NodbTokenTool
{
    private NodbTokenTool()
    {
    }

    public static void main( String[] args )
    {
        String tenant = null;
        String scopeArg = null;
        String subject = "dev";
        String keysDir = System.getenv().getOrDefault( "NODB_KEYS_DIR", "./.nodb-dev-keys" );
        long ttlMinutes = 60;

        for ( int i = 0; i < args.length; i++ )
        {
            switch ( args[i] )
            {
                case "--tenant" -> tenant = args[++i];
                case "--scope" -> scopeArg = args[++i];
                case "--subject" -> subject = args[++i];
                case "--keys-dir" -> keysDir = args[++i];
                case "--ttl-minutes" -> ttlMinutes = Long.parseLong( args[++i] );
                default -> throw new IllegalArgumentException( "Unknown argument: " + args[i] );
            }
        }

        if ( tenant == null || scopeArg == null )
        {
            System.err.println( "Usage: NodbTokenTool --tenant <t> --scope <runtime|operator> [--subject s] [--keys-dir dir]" );
            System.exit( 2 );
            return;
        }

        Scope scope = Scope.fromClaim( scopeArg );
        KeyPair keyPair = DevKeys.loadOrGenerate( Path.of( keysDir ) );
        String token = JwtIssuer.mint( (RSAPrivateKey) keyPair.getPrivate(), (RSAPublicKey) keyPair.getPublic(), tenant, scope, subject,
                                        Duration.ofMinutes( ttlMinutes ) );
        System.out.println( token );
    }
}
