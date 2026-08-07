package com.enonic.xp.core.nodb;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

import org.jspecify.annotations.Nullable;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.storage.spi.IndexDocumentRecord;
import com.enonic.xp.storage.spi.IndexMapping;
import com.enonic.xp.storage.spi.IndexSettings;
import com.enonic.xp.storage.spi.NodeSearchIndex;
import com.enonic.xp.storage.spi.ReturnFields;
import com.enonic.xp.storage.spi.ReturnValues;
import com.enonic.xp.storage.spi.SearchPreference;
import com.enonic.xp.storage.spi.SearchRequest;
import com.enonic.xp.storage.spi.SearchResult;
import com.enonic.xp.storage.spi.UpdateIndexSettings;

/**
 * Phase 4 Gate F (nodb/BUILD-PHASE-4.md): the nodb-mode {@link NodeSearchIndex} every itest
 * fixture wires, wrapping the tenant's real {@code NodbNodeSearchIndex} and adding exactly one
 * thing — a repository-less {@link #refreshAll()} that the suite's static
 * {@code AbstractElasticsearchIntegrationTest#refresh()} can call.
 * <p>
 * <b>Why this exists.</b> ~40 itest classes make their writes searchable by calling the
 * {@code protected static refresh()} inherited from {@code AbstractElasticsearchIntegrationTest},
 * which is a GLOBAL, Elasticsearch-client-level refresh of every index. It knows nothing about a
 * NoDB/OpenSearch backend, so in nodb mode each of those calls refreshed the wrong engine and the
 * test read an unrefreshed index — the same class of error that made Gate C's corpus compare ES
 * with itself and Gate E's eight aggregation classes measure zero buckets (nodb/FINDINGS.md).
 * Gate E fixed its eight classes one at a time; at suite scale that is 40 opportunities to miss
 * one, so the fix belongs in the single method they all call. Making {@code refresh()}
 * mode-correct needs a backend-agnostic "make everything I wrote visible", and the SPI's
 * {@link #refresh(RepositoryId)} is deliberately per-repository — hence this wrapper.
 * <p>
 * <b>The recorded set is writes, not repositories.</b> {@code NodbNodeSearchIndex#refresh}
 * awaits the highest outbox sequence THIS instance produced for the given repository (Gate B
 * decision 7) and returns without an RPC for a repository it never wrote to. Recording exactly
 * the repositories {@link #index}/{@link #delete} touched therefore makes {@link #refreshAll()}
 * the faithful equivalent of the ES call — "everything this test could have made stale" — while
 * costing one RPC per repository actually written rather than one per repository that exists.
 */
public final class NodbItestSearchIndex
    implements NodeSearchIndex
{
    /**
     * The instance the suite's static {@code refresh()} delegates to. Itest classes run
     * sequentially in one JVM and each builds its wiring in {@code @BeforeEach}, so the most
     * recently constructed wrapper is always the one belonging to the running test.
     */
    private static final AtomicReference<NodbItestSearchIndex> CURRENT = new AtomicReference<>();

    private final NodeSearchIndex delegate;

    private final Set<RepositoryId> written = ConcurrentHashMap.newKeySet();

    private NodbItestSearchIndex( final NodeSearchIndex delegate )
    {
        this.delegate = delegate;
    }

    /** Wraps {@code tenant}'s search index and makes it the target of {@link #refreshCurrent()}. */
    public static NodbItestSearchIndex of( final NodbTenant tenant )
    {
        final NodbItestSearchIndex wrapper = new NodbItestSearchIndex( tenant.nodeSearchIndex() );
        CURRENT.set( wrapper );
        return wrapper;
    }

    /**
     * Refreshes every repository the running test has written to, or does nothing if no nodb-mode
     * fixture has been built yet in this JVM (a nodb-mode class whose {@code @BeforeAll} refreshes
     * before any {@code @BeforeEach} has run has, by construction, written nothing).
     */
    public static void refreshCurrent()
    {
        final NodbItestSearchIndex current = CURRENT.get();
        if ( current != null )
        {
            current.refreshAll();
        }
    }

    public void refreshAll()
    {
        written.forEach( delegate::refresh );
    }

    @Override
    public SearchResult search( final SearchRequest searchRequest )
    {
        return delegate.search( searchRequest );
    }

    @Override
    public void index( final RepositoryId repositoryId, final Branch branch, final IndexDocumentRecord doc )
    {
        written.add( repositoryId );
        delegate.index( repositoryId, branch, doc );
    }

    @Override
    public void delete( final RepositoryId repositoryId, final Branch branch, final Collection<String> nodeIds )
    {
        written.add( repositoryId );
        delegate.delete( repositoryId, branch, nodeIds );
    }

    @Override
    public ReturnValues get( final RepositoryId repositoryId, final Branch branch, final String nodeId, final ReturnFields returnFields,
                             final @Nullable SearchPreference searchPreference )
    {
        return delegate.get( repositoryId, branch, nodeId, returnFields, searchPreference );
    }

    @Override
    public void refresh( final RepositoryId repositoryId )
    {
        delegate.refresh( repositoryId );
    }

    @Override
    public void createIndex( final RepositoryId repositoryId, final IndexSettings settings, final IndexMapping mapping )
    {
        delegate.createIndex( repositoryId, settings, mapping );
    }

    @Override
    public void deleteIndex( final RepositoryId repositoryId )
    {
        delegate.deleteIndex( repositoryId );
    }

    @Override
    public boolean indexExists( final RepositoryId repositoryId )
    {
        return delegate.indexExists( repositoryId );
    }

    @Override
    public void updateSettings( final RepositoryId repositoryId, final UpdateIndexSettings settings )
    {
        delegate.updateSettings( repositoryId, settings );
    }
}
