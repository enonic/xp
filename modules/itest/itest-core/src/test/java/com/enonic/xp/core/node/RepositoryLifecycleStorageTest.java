package com.enonic.xp.core.node;

import java.util.Map;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import com.enonic.xp.core.AbstractNodeTest;
import com.enonic.xp.core.nodb.NodbTenant;
import com.enonic.xp.core.nodb.NodbTestCluster;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.storage.spi.IndexSettings;
import com.enonic.xp.storage.spi.StorageIndexExistsException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Phase 1 Gate C new storage-only itest (BUILD-PHASE-1.md, Gate 0's proposed list #5):
 * {@code createIndex}/{@code indexExists}/{@code deleteIndex} round-trip via
 * {@code RepositoryStorageAdmin} only -- no node writes at all, no search index touched.
 * <p>
 * Two genuine backend asymmetries surfaced while writing this test (both pre-existing,
 * not introduced here -- documented rather than silently special-cased):
 * <ul>
 *   <li>Double-create: ES wraps the SPI exception in {@code IndexException}
 *   ({@code IndexServiceInternalImpl#doCreateIndex}); nodb throws
 *   {@link StorageIndexExistsException} directly ({@code NodbStatusMapper}). Both honor
 *   the SPI contract (the exception type appears somewhere in the cause chain) -- asserted
 *   via {@link #assertCausedBy} rather than an exact type match.</li>
 *   <li>Delete-of-unknown-repo: WAS an asymmetry (ES silently no-op, nodb
 *   {@code StorageIndexNotFoundException}). <b>Resolved at Phase 4 Gate F</b> -- both backends are
 *   now a silent no-op, because XP's dump/load path depends on it. See
 *   {@link #deleteOfAnUnknownRepositoryIsASilentNoOpOnBothBackends}.</li>
 * </ul>
 * <p>
 * The cross-tenant spot check (Gate C task 4, nodb mode only -- two tenants on one shared
 * NoDB server/Postgres instance, asserting a node written via tenant A is invisible via
 * tenant B) lives here too, since it is fundamentally a repository/tenant LIFECYCLE
 * concern: "tenant" has no ES equivalent in this SPI at all, so it is unconditionally
 * nodb-only (skipped, not failed, outside nodb mode -- unlike the two asymmetries above,
 * which are asserted per-mode rather than skipped).
 */
class RepositoryLifecycleStorageTest
    extends AbstractNodeTest
{
    @Test
    void createIndexExistsDeleteIndexRoundTrip()
    {
        final RepositoryId repoId = RepositoryId.from( "lifecycle-storage-test-" + System.nanoTime() );

        assertFalse( repositoryStorageAdmin.indexExists( repoId ), "must not exist before creation" );

        repositoryStorageAdmin.createIndex( repoId, IndexSettings.from( Map.of() ), Map.of() );
        assertTrue( repositoryStorageAdmin.indexExists( repoId ), "must exist right after creation" );

        // Double-create: SPI-documented StorageIndexExistsException, wrapped by ES.
        final Exception doubleCreate = assertThrows( Exception.class,
                                                       () -> repositoryStorageAdmin.createIndex( repoId, IndexSettings.from( Map.of() ),
                                                                                                  Map.of() ) );
        assertCausedBy( doubleCreate, StorageIndexExistsException.class );

        repositoryStorageAdmin.deleteIndex( repoId );
        assertFalse( repositoryStorageAdmin.indexExists( repoId ), "must not exist after deletion" );
    }

    /**
     * Phase 4 Gate F (nodb/BUILD-PHASE-4.md) turned this from a documented ASYMMETRY into a shared
     * contract, and it is now the same assertion in both modes: deleting a repository that is not
     * there is a successful, silent no-op.
     * <p>
     * Before, ES swallowed it ({@code IndexServiceInternalImpl#doDeleteIndex} catches every
     * {@code ElasticsearchException} and logs) while nodb mapped {@code UnknownRepoException} to
     * NOT_FOUND and threw {@code StorageIndexNotFoundException}. The asymmetry was recorded here
     * rather than resolved -- until the full suite showed that XP DEPENDS on the ES behaviour:
     * {@code DumpServiceImpl}'s load path deletes every repository the entry query lists and then
     * deletes the system repositories by name, so a repository the caller already removed is deleted
     * twice on every load, and 18 of {@code DumpServiceImplTest}'s methods failed on it. The wire
     * still answers NOT_FOUND -- the repository genuinely is gone -- and the SPI adapter decides that
     * for a DELETE this is success ({@code NodbStatusMapper#idempotentDelete}); a non-delete
     * repo-scoped op on an unknown repository still throws.
     */
    @Test
    void deleteOfAnUnknownRepositoryIsASilentNoOpOnBothBackends()
    {
        final RepositoryId unknown = RepositoryId.from( "never-created-lifecycle-repo-" + System.nanoTime() );

        assertDoesNotThrow( () -> repositoryStorageAdmin.deleteIndex( unknown ) );
        assertFalse( repositoryStorageAdmin.indexExists( unknown ), "an idempotent delete must not have created anything" );
    }

    /** Gate C task 4's cross-tenant spot check -- see class javadoc. nodb mode only. */
    @Test
    void crossTenantIsolation_nodeWrittenInTenantAIsInvisibleFromTenantB()
    {
        Assumptions.assumeTrue( NodbTestCluster.isEnabled(), "tenant isolation has no ES-mode equivalent in this SPI" );

        final NodbTenant tenantA = NodbTestCluster.get().freshTenant();
        final NodbTenant tenantB = NodbTestCluster.get().freshTenant();
        assertNotEquals( tenantA.tenantId(), tenantB.tenantId() );

        try
        {
            // Same external repo id in both tenants on purpose: proves isolation is by
            // TENANT (Postgres schema), not merely by repo id happening to differ.
            final RepositoryId repoId = RepositoryId.from( "shared-repo-name" );

            tenantA.repositoryStorageAdmin().createIndex( repoId, IndexSettings.from( Map.of() ), Map.of() );

            assertTrue( tenantA.repositoryStorageAdmin().indexExists( repoId ), "must exist in the tenant that created it" );
            assertFalse( tenantB.repositoryStorageAdmin().indexExists( repoId ),
                         "a repository created in tenant A must be invisible from tenant B" );

            // And the inverse: tenant B can create the SAME repo id independently (proves
            // the two schemas don't collide on the repository table's primary key either).
            tenantB.repositoryStorageAdmin().createIndex( repoId, IndexSettings.from( Map.of() ), Map.of() );
            assertTrue( tenantB.repositoryStorageAdmin().indexExists( repoId ) );
        }
        finally
        {
            tenantA.close();
            tenantB.close();
        }
    }

    private static void assertCausedBy( final Throwable thrown, final Class<? extends Throwable> expected )
    {
        for ( Throwable t = thrown; t != null; t = t.getCause() )
        {
            if ( expected.isInstance( t ) )
            {
                return;
            }
        }
        fail( "expected " + expected.getName() + " somewhere in the cause chain of " + thrown, thrown );
    }
}
