package com.enonic.xp.storage.nodb;

import java.util.function.Supplier;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;

import com.enonic.xp.storage.spi.StorageIndexExistsException;
import com.enonic.xp.storage.spi.StorageIndexNotFoundException;

/**
 * gRPC status -&gt; SPI exception mapping, per the table documented in
 * {@code nodb/proto/nodb.proto}'s "gRPC status contract (Phase 1 Gate A)" comment block:
 *
 * <pre>
 * NOT_FOUND on a repo-scoped op (unknown repo id)        -&gt; StorageIndexNotFoundException
 * ALREADY_EXISTS on CreateRepository (duplicate repo id) -&gt; StorageIndexExistsException
 * NOT_FOUND on a point-get (GetBranchEntry/GetVersion/
 *   GetCommit/GetPayload)                                -&gt; null (see {@link #pointGet})
 * UNAUTHENTICATED / PERMISSION_DENIED / UNAVAILABLE       -&gt; NodbClientException (fail
 *                                                            fast, no retry, Gate B policy)
 * anything else (INTERNAL, etc.)                          -&gt; NodbClientException
 * </pre>
 */
final class NodbStatusMapper
{
    private NodbStatusMapper()
    {
    }

    /** Runs a repo/branch/version/commit-scoped call, translating NOT_FOUND/ALREADY_EXISTS to the SPI's exceptions. */
    static <T> T repoScoped( final Supplier<T> call )
    {
        try
        {
            return call.get();
        }
        catch ( StatusRuntimeException e )
        {
            throw translate( e );
        }
    }

    static void repoScopedVoid( final Runnable call )
    {
        try
        {
            call.run();
        }
        catch ( StatusRuntimeException e )
        {
            throw translate( e );
        }
    }

    /**
     * Runs a point-get call (GetBranchEntry/GetVersion/GetCommit/GetPayload): NOT_FOUND
     * means "no such row", translated to {@code null} per the SPI's {@code @Nullable}
     * return contract, not an exception -- regardless of whether the underlying cause was
     * an unknown repo id or simply a missing node/version/commit (the proto's status
     * contract deliberately does not distinguish the two for these RPCs).
     */
    static <T> T pointGet( final Supplier<T> call )
    {
        try
        {
            return call.get();
        }
        catch ( StatusRuntimeException e )
        {
            if ( e.getStatus().getCode() == Status.Code.NOT_FOUND )
            {
                return null;
            }
            throw translate( e );
        }
    }

    /**
     * Runs an existence-check call (ExistsBranchEntry/RepositoryExists), treating NOT_FOUND
     * defensively as "does not exist" rather than an error -- the proto's own comments are
     * not fully consistent on whether RepositoryExists can ever surface NOT_FOUND (the RPC
     * doc says it "returns a boolean rather than throwing", but the status-contract table
     * lists RepositoryExists among the ops where an unknown repo id throws NOT_FOUND); this
     * treats either behavior as "false", which is correct either way.
     */
    static boolean existsCheck( final Supplier<Boolean> call )
    {
        try
        {
            return call.get();
        }
        catch ( StatusRuntimeException e )
        {
            if ( e.getStatus().getCode() == Status.Code.NOT_FOUND )
            {
                return false;
            }
            throw translate( e );
        }
    }

    private static RuntimeException translate( final StatusRuntimeException e )
    {
        final Status status = e.getStatus();
        final String message = status.getDescription() != null ? status.getDescription() : e.getMessage();

        return switch ( status.getCode() )
        {
            case NOT_FOUND -> new StorageIndexNotFoundException( message, e );
            case ALREADY_EXISTS -> new StorageIndexExistsException( message, e );
            case UNAUTHENTICATED, PERMISSION_DENIED, UNAVAILABLE ->
                new NodbClientException( "NoDB connection failure (" + status.getCode() + "): " + message, e );
            default -> new NodbClientException( "NoDB call failed (" + status.getCode() + "): " + message, e );
        };
    }
}
