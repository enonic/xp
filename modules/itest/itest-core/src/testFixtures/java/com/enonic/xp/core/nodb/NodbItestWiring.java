package com.enonic.xp.core.nodb;

import org.elasticsearch.client.Client;

import com.enonic.xp.blob.BlobStore;
import com.enonic.xp.repo.impl.elasticsearch.IndexServiceInternalImpl;
import com.enonic.xp.repo.impl.elasticsearch.search.NodeSearchIndexImpl;
import com.enonic.xp.repo.impl.elasticsearch.search.SearchDaoImpl;
import com.enonic.xp.repo.impl.elasticsearch.storage.ElasticsearchNodeStore;
import com.enonic.xp.repo.impl.elasticsearch.storage.StorageDaoImpl;
import com.enonic.xp.repo.impl.index.IndexServiceInternal;
import com.enonic.xp.storage.nodb.NodbBinaryBlobStore;
import com.enonic.xp.storage.spi.NodeSearchIndex;
import com.enonic.xp.storage.spi.NodeStore;
import com.enonic.xp.storage.spi.RepositoryStorageAdmin;

/**
 * Phase 4 Gate F (nodb/BUILD-PHASE-4.md): the four backend-dependent roles an itest needs, wired
 * for whichever mode the JVM is running in — for the four itests that build their OWN service
 * graph instead of inheriting {@code AbstractNodeTest}'s ({@code SecurityServiceImplTest},
 * {@code DynamicSchemaServiceImplTest}, {@code AbstractContentServiceTest},
 * {@code AbstractIssueServiceTest}).
 * <p>
 * <b>Why it exists.</b> Those four hand-rolled graphs are where this phase's standing failure
 * mode kept hiding: {@code SecurityServiceImplTest} and {@code DynamicSchemaServiceImplTest} had
 * NO mode branch at all, so a nodb-mode run exercised Elasticsearch end to end for both storage
 * and search; the two content fixtures branched storage but wired
 * {@code new NodeSearchIndexImpl( client, … )} unconditionally, so every content query, sort and
 * aggregation Gates B–E called "green in nodb mode" was in fact answered by embedded
 * Elasticsearch. Both are the same error nodb/FINDINGS.md #6 records three times over, and the
 * reason it recurs is that the branch is copied per fixture. One shared factory removes the
 * copies: there is now a single place that decides what "the storage backend" means for an itest,
 * and it cannot disagree with itself.
 * <p>
 * {@link #close()} releases a per-method tenant and does nothing for a class-scoped one — the
 * distinction {@code NodbTestCluster} draws, kept here so callers cannot get it wrong.
 */
public final class NodbItestWiring
    implements AutoCloseable
{
    private final NodbTenant tenant;

    private final boolean ownsTenant;

    private final StorageDaoImpl storageDao;

    private final NodeStore nodeStore;

    private final RepositoryStorageAdmin repositoryStorageAdmin;

    private final NodeSearchIndex nodeSearchIndex;

    private final IndexServiceInternal indexServiceInternal;

    private final BlobStore blobStore;

    private NodbItestWiring( final NodbTenant tenant, final boolean ownsTenant, final Client client, final BlobStore delegateBlobStore )
    {
        this.tenant = tenant;
        this.ownsTenant = ownsTenant;

        if ( tenant != null )
        {
            this.storageDao = null;
            this.nodeStore = tenant.nodeStore();
            this.repositoryStorageAdmin = tenant.repositoryStorageAdmin();
            this.nodeSearchIndex = NodbItestSearchIndex.of( tenant );
            this.indexServiceInternal = new NodbIndexServiceInternal();
            this.blobStore = new NodbBinaryBlobStore( delegateBlobStore, tenant.client() );
        }
        else
        {
            final SearchDaoImpl searchDao = new SearchDaoImpl( client );
            final IndexServiceInternalImpl esIndexServiceInternal = new IndexServiceInternalImpl( client );
            this.storageDao = new StorageDaoImpl( client );
            this.nodeStore = new ElasticsearchNodeStore( storageDao, searchDao, delegateBlobStore );
            this.repositoryStorageAdmin = esIndexServiceInternal;
            this.nodeSearchIndex = new NodeSearchIndexImpl( client, searchDao, storageDao );
            this.indexServiceInternal = esIndexServiceInternal;
            this.blobStore = delegateBlobStore;
        }
    }

    /**
     * Wiring for a fixture that resets the world between test methods (an ES-mode
     * {@code deleteAllIndices()} in {@code @BeforeEach}): a fresh nodb tenant per method, which
     * only became correct at Gate F — see {@code NodbTestCluster}'s isolation javadoc.
     */
    public static NodbItestWiring perMethod( final Client client, final BlobStore delegateBlobStore )
    {
        return new NodbItestWiring( NodbTestCluster.isEnabled() ? NodbTestCluster.get().perMethodTenant() : null, true, client,
                                    delegateBlobStore );
    }

    /** Wiring for a fixture whose isolation is per test CLASS ({@code NodbTestCluster#tenantForClass}). */
    public static NodbItestWiring perClass( final Class<?> testClass, final Client client, final BlobStore delegateBlobStore )
    {
        return new NodbItestWiring( NodbTestCluster.isEnabled() ? NodbTestCluster.get().tenantForClass( testClass ) : null, false, client,
                                    delegateBlobStore );
    }

    /** {@code null} in nodb mode: there is no Elasticsearch storage DAO, because there is no client. */
    public StorageDaoImpl storageDao()
    {
        return storageDao;
    }

    public NodeStore nodeStore()
    {
        return nodeStore;
    }

    public RepositoryStorageAdmin repositoryStorageAdmin()
    {
        return repositoryStorageAdmin;
    }

    public NodeSearchIndex nodeSearchIndex()
    {
        return nodeSearchIndex;
    }

    public IndexServiceInternal indexServiceInternal()
    {
        return indexServiceInternal;
    }

    /**
     * The {@link BlobStore} to build {@code BinaryServiceImpl}/{@code NodeVersionServiceImpl} on:
     * the given delegate in default mode, or a {@link NodbBinaryBlobStore} wrapping it in nodb
     * mode, which diverts only the binary segment to NoDB/MinIO.
     */
    public BlobStore blobStore()
    {
        return blobStore;
    }

    /** {@code null} in default mode. */
    public NodbTenant tenant()
    {
        return tenant;
    }

    /**
     * Releases a per-method tenant; a no-op for a class-scoped one (the extension on
     * {@code AbstractElasticsearchIntegrationTest} releases those when the class ends). Goes through
     * {@link NodbTestCluster#release} rather than {@code tenant.close()} so the SERVER-side outbox
     * indexer is stopped too -- closing only the channel leaves a polling thread and its share of the
     * connection pool behind, once per test method.
     */
    @Override
    public void close()
    {
        if ( ownsTenant && tenant != null )
        {
            NodbTestCluster.get().release( tenant );
        }
    }
}
