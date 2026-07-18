package com.enonic.nodb.engine.store;

import java.sql.SQLException;

/**
 * Thrown by {@link RepoKeys#resolve} when no {@code repository} row matches the caller's
 * external repo id within the current tenant schema. A dedicated type (rather than a plain
 * {@link SQLException} distinguished by message text) so the gRPC layer can map this outcome
 * to {@code Status.NOT_FOUND} — which the XP-side nodb-client translates to {@code
 * StorageIndexNotFoundException} (Phase 1 Gate A, BUILD-PHASE-1.md reconciliation table) —
 * structurally, not by matching a substring of {@link #getMessage()}.
 */
public final class UnknownRepoException
    extends SQLException
{
    public UnknownRepoException( String repoId )
    {
        super( "Unknown repo id: " + repoId );
    }
}
