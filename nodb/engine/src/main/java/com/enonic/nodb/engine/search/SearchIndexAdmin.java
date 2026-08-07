package com.enonic.nodb.engine.search;

import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.nodb.engine.TenantContext;
import com.enonic.nodb.engine.Tx;
import com.enonic.nodb.engine.model.RepoRef;
import com.enonic.nodb.engine.store.RepoKeys;

/**
 * Per-repo search index lifecycle: create (with the ported mappings/analyzers, alias
 * {@code <tenant>-<repo>} → {@code <tenant>-<repo>+g1}), exists, delete.
 *
 * <p>The generational structure is in place from the first index even though nothing flips a
 * generation until Gate G's rebuild drill. That is a deliberate ordering choice: the alternative
 * (create {@code <tenant>-<repo>} as a plain index now, introduce the alias later) would make the
 * first rebuild a migration of every existing index rather than an alias swap, and "search is
 * disposable" is the property the whole design leans on.
 *
 * <p>Both halves of an index's identity are written in one place: OpenSearch gets the index and
 * the alias, {@code search_index} gets the authoritative alias→generation row with the template
 * and projection versions that built it.
 */
public final class SearchIndexAdmin
{
    private static final Logger LOG = LoggerFactory.getLogger( SearchIndexAdmin.class );

    private final DataSource dataSource;

    private final OpenSearchClient client;

    private final IndexTemplate template;

    public SearchIndexAdmin( DataSource dataSource, OpenSearchClient client )
    {
        this( dataSource, client, IndexTemplate.load() );
    }

    public SearchIndexAdmin( DataSource dataSource, OpenSearchClient client, IndexTemplate template )
    {
        this.dataSource = dataSource;
        this.client = client;
        this.template = template;
    }

    /**
     * Creates the repo's first generation and points the alias at it. Idempotent: an existing
     * alias is left alone, so repo provisioning can be retried and a pre-Phase-4 repo can be
     * given an index without a separate "does it already have one" dance at the call site.
     */
    public String createIndex( TenantContext tenant, String repoId )
        throws SQLException
    {
        String alias = SearchIndexNames.alias( tenant, repoId );
        if ( client.aliasExists( alias ) )
        {
            LOG.debug( "Search index for alias {} already exists", alias );
            return alias;
        }

        long repoKey = resolveRepoKey( tenant, repoId );
        int generation = nextGeneration( tenant, repoKey );
        String indexName = SearchIndexNames.physicalFromAlias( alias, generation );

        client.createIndex( indexName, template.createIndexBody( alias, client.config() ) );

        Tx.inTenantTx( dataSource, tenant, connection -> {
            SearchIndexStore.record( connection, repoKey,
                                     new SearchIndexStore.SearchIndexRecord( generation, alias, indexName, template.templateVersion(),
                                                                            IndexDocumentProjection.VERSION,
                                                                            SearchIndexStore.STATE_LIVE ) );
            return null;
        } );

        LOG.info( "Created search index {} behind alias {} (template v{}, projection v{})", indexName, alias, template.templateVersion(),
                  IndexDocumentProjection.VERSION );
        return alias;
    }

    /** Whether the repo's alias resolves to an index. The alias, never a physical name. */
    public boolean indexExists( TenantContext tenant, String repoId )
    {
        return client.aliasExists( SearchIndexNames.alias( tenant, repoId ) );
    }

    /**
     * Deletes every generation of the repo's index and forgets its metadata. Deleting through the
     * alias is what makes this correct mid-rebuild: a BUILDING generation carries no alias, so it
     * is found via {@code search_index} rather than via OpenSearch, and dropping a repo halfway
     * through a rebuild does not leave an orphan index behind.
     */
    public void deleteIndex( TenantContext tenant, String repoId )
        throws SQLException
    {
        String alias = SearchIndexNames.alias( tenant, repoId );

        List<String> recorded;
        Long repoKey = tryResolveRepoKey( tenant, repoId );
        if ( repoKey != null )
        {
            recorded = Tx.inTenantTx( dataSource, tenant, connection -> SearchIndexStore.list( connection, repoKey )
                .stream()
                .map( SearchIndexStore.SearchIndexRecord::indexName )
                .toList() );
        }
        else
        {
            // The repository row is already gone (repo delete drops it), so the metadata went
            // with it via ON DELETE CASCADE. Fall back to whatever the alias still resolves to.
            recorded = List.of();
        }

        List<String> toDelete = recorded.isEmpty() ? client.indicesForAlias( alias ) : recorded;
        for ( String indexName : toDelete )
        {
            if ( client.indexExists( indexName ) )
            {
                client.deleteIndex( indexName );
                LOG.info( "Deleted search index {}", indexName );
            }
        }

        if ( repoKey != null )
        {
            Tx.inTenantTx( dataSource, tenant, connection -> {
                SearchIndexStore.deleteAll( connection, repoKey );
                return null;
            } );
        }
    }

    /** The physical index the alias points at right now, or {@code null}. */
    public String liveIndexName( TenantContext tenant, String repoId )
        throws SQLException
    {
        Long repoKey = tryResolveRepoKey( tenant, repoId );
        if ( repoKey == null )
        {
            return null;
        }
        SearchIndexStore.SearchIndexRecord live = Tx.inTenantTx( dataSource, tenant, connection -> SearchIndexStore.live( connection,
                                                                                                                         repoKey ) );
        return live == null ? null : live.indexName();
    }

    /** Every non-system index of one tenant, by the {@code <tenant>-} prefix. */
    public List<String> listTenantIndices( TenantContext tenant )
    {
        String prefix = SearchIndexNames.tenantPrefix( tenant );
        return client.listIndices().stream().filter( name -> name.startsWith( prefix ) ).toList();
    }

    /**
     * Deletes every index of one tenant, all repositories and all generations, and returns how many
     * were removed. Phase 4 Gate F: a tenant that goes away must take its search indices with it.
     * PostgreSQL already had this ({@code dropTenant} drops the schema); OpenSearch did not, so every
     * dropped tenant leaked its indices -- and index metadata is heap. The itest suite proved it the
     * hard way: provisioning a tenant per test method filled the container's 512 MB heap until the
     * parent circuit breaker rejected requests with HTTP 429. Deliberately driven off the
     * {@code <tenant>-} prefix rather than off {@code search_index}, because this must work when the
     * tenant's SCHEMA is already gone -- which is exactly when it is needed.
     */
    public int deleteTenantIndices( TenantContext tenant )
    {
        int deleted = 0;
        for ( String indexName : listTenantIndices( tenant ) )
        {
            client.deleteIndex( indexName );
            deleted++;
        }
        if ( deleted > 0 )
        {
            LOG.info( "Deleted {} search index(es) of tenant {}", deleted, tenant.tenantId() );
        }
        return deleted;
    }

    private int nextGeneration( TenantContext tenant, long repoKey )
        throws SQLException
    {
        int highest = Tx.inTenantTx( dataSource, tenant, connection -> SearchIndexStore.maxGeneration( connection, repoKey ) );
        return highest == 0 ? SearchIndexNames.FIRST_GENERATION : highest + 1;
    }

    private long resolveRepoKey( TenantContext tenant, String repoId )
        throws SQLException
    {
        return Tx.inTenantTx( dataSource, tenant, connection -> RepoKeys.resolve( connection, new RepoRef( repoId ) ) );
    }

    private Long tryResolveRepoKey( TenantContext tenant, String repoId )
        throws SQLException
    {
        try
        {
            return resolveRepoKey( tenant, repoId );
        }
        catch ( com.enonic.nodb.engine.store.UnknownRepoException e )
        {
            return null;
        }
    }
}
