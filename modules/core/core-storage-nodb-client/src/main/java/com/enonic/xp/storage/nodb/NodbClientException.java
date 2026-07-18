package com.enonic.xp.storage.nodb;

/**
 * Connection-level / non-SPI-mapped NoDB failures: an unreachable server, an
 * unauthenticated/expired/wrong-audience token, an operator-scope-only RPC called with a
 * runtime-scope token, or any other gRPC status this client does not translate into one of
 * the storage SPI's own exceptions ({@code StorageIndexNotFoundException} /
 * {@code StorageIndexExistsException}). Phase 1 policy (nodb/BUILD-PHASE-1.md Gate B):
 * fail fast with a clear message, no silent retry loop, no automatic fallback to another
 * backend.
 */
public class NodbClientException
    extends RuntimeException
{
    public NodbClientException( final String message )
    {
        super( message );
    }

    public NodbClientException( final String message, final Throwable cause )
    {
        super( message, cause );
    }
}
