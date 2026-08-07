package com.enonic.xp.core.nodb;

import com.enonic.xp.repo.impl.index.IndexServiceInternal;

/**
 * Phase 4 Gate F (nodb/BUILD-PHASE-4.md): the {@link IndexServiceInternal} nodb-mode itests get
 * instead of {@code IndexServiceInternalImpl}, which needs a live Elasticsearch {@code Client}.
 * <p>
 * {@link IndexServiceInternal} is deliberately the one backend-internal interface that never
 * crossed the storage SPI (Phase 0 Gate C): it is raw-Elasticsearch cluster vocabulary —
 * health, master election, and closing/opening/deleting indices by raw name — which NoDB has no
 * counterpart for. The four methods below are therefore answered from what is TRUE of the nodb
 * stack, not stubbed to make callers happy:
 * <ul>
 * <li>{@code isMaster} — a single XP process owns its NoDB tenant outright; there is no cluster
 * to be elected in. (Multi-instance coordination is Phase 6; the indexer's checkpoint
 * {@code GREATEST} semantics already make a second instance safe, just wasteful.)</li>
 * <li>{@code waitForYellowStatus} — readiness of Postgres and OpenSearch is established before
 * {@link NodbTestCluster#get()} returns, so by the time any caller asks, the answer is yes.</li>
 * <li>{@code closeIndices}/{@code openIndices} — the only caller is
 * {@code IndexServiceImpl#updateIndexSettings} bracketing a settings update, and settings
 * updates are a documented no-op on the nodb backend ({@code NodbNodeSearchIndex#updateSettings}:
 * replicas and refresh_interval are the search backend's own concern). Bracketing a no-op is a
 * no-op.</li>
 * <li>{@code deleteIndices} — snapshot/restore only ({@code SnapshotRestoreExecutor}), which is
 * explicitly ES-only and out of Phase 4's scope, so it throws rather than silently doing
 * nothing: a caller that reaches it is a caller running the wrong backend.</li>
 * </ul>
 */
public final class NodbIndexServiceInternal
    implements IndexServiceInternal
{
    @Override
    public void deleteIndices( final String... indexNames )
    {
        throw new UnsupportedOperationException(
            "deleting indices by raw name is Elasticsearch snapshot/restore vocabulary and has no nodb equivalent" );
    }

    @Override
    public void closeIndices( final String... indices )
    {
        // No-op -- see class javadoc.
    }

    @Override
    public void openIndices( final String... indices )
    {
        // No-op -- see class javadoc.
    }

    @Override
    public boolean waitForYellowStatus( final String... indexNames )
    {
        return true;
    }

    @Override
    public boolean isMaster()
    {
        return true;
    }
}
