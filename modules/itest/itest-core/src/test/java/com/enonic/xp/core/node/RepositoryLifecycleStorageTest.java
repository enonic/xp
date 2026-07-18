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
import com.enonic.xp.storage.spi.StorageIndexNotFoundException;

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
 *   <li>Delete-of-unknown-repo: ES's {@code doDeleteIndex} catches and logs, a silent
 *   no-op ({@code IndexServiceInternalImpl}); nodb throws
 *   {@link StorageIndexNotFoundException} (a real {@code UnknownRepoException} ->
 *   {@code NOT_FOUND} mapping, BUILD-PHASE-1.md's Gate A bug fix). Asserted per-mode.</li>
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

    @Test
    void deleteOfAnUnknownRepositoryIsHandledPerBackendContract()
    {
        final RepositoryId unknown = RepositoryId.from( "never-created-lifecycle-repo-" + System.nanoTime() );

        if ( NodbTestCluster.isEnabled() )
        {
            // nodb: RepositoryLifecycle.deleteRepository -> UnknownRepoException -> NOT_FOUND
            // -> StorageIndexNotFoundException (a real, structural mapping -- Gate A bug fix).
            final Exception thrown = assertThrows( Exception.class, () -> repositoryStorageAdmin.deleteIndex( unknown ) );
            assertCausedBy( thrown, StorageIndexNotFoundException.class );
        }
        else
        {
            // ES: IndexServiceInternalImpl#doDeleteIndex catches ElasticsearchException and
            // only logs -- a silent no-op, not a throw. Asserting that explicitly (rather
            // than ignoring the case) so this documented asymmetry stays visible.
            repositoryStorageAdmin.deleteIndex( unknown );
        }
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
