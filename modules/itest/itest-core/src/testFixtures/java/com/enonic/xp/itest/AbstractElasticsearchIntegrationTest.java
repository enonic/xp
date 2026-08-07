package com.enonic.xp.itest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.elasticsearch.action.admin.indices.refresh.RefreshResponse;
import org.elasticsearch.action.search.SearchAction;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.MoreFiles;
import com.google.common.io.RecursiveDeleteOption;

import com.enonic.xp.core.nodb.NodbItestSearchIndex;
import com.enonic.xp.core.nodb.NodbTestCluster;

/**
 * <b>Phase 4 Gate F (nodb/BUILD-PHASE-4.md): in nodb mode this class starts NO Elasticsearch.</b>
 * The extension below is the single place an embedded node was ever constructed for the itest
 * suites, so mode-branching it is what makes "nodb mode runs zero embedded Elasticsearch" a
 * property of the code rather than of a test's luck. {@link #client} stays {@code null}, and the
 * three static helpers every itest inherits are redirected or refused rather than left to NPE:
 * <ul>
 * <li>{@link #refresh()} goes to the nodb search backend ({@link NodbItestSearchIndex}) instead of
 * the ES client. ~40 classes call it; a no-op here would leave every one of them reading an
 * unrefreshed index with no error at all, which is the failure mode that made Gate E's eight
 * aggregation classes measure zero buckets (nodb/FINDINGS.md).</li>
 * <li>{@link #deleteAllIndices()} becomes a no-op, because tenant scoping already provides what
 * it provides: a class-scoped nodb tenant is a fresh schema and fresh OpenSearch indices, and
 * {@code AbstractNodeTest}'s {@code clearBeforeEach} takes a per-METHOD tenant.</li>
 * <li>{@code -Dxp.itest.opensearch=false} (the storage-only escape hatch) leaves nodb mode with
 * no search backend at all rather than falling back to Elasticsearch, so search operations fail
 * with UNIMPLEMENTED from the {@code NodeSearch} RPC. That is the honest answer for a
 * debugging-only switch; silently reinstating the hybrid wiring is not.</li>
 * <li>{@link #getSnapshotsDir()} throws: snapshot repositories are raw Elasticsearch vocabulary
 * with no nodb counterpart, and a caller reaching it in nodb mode is a test running against the
 * wrong backend -- which must be loud, not empty. {@link #printAllIndexContent} is the one
 * exception, and only because it is pure debug output -- see its own note.</li>
 * </ul>
 * The gate's proof is {@link NoEmbeddedElasticsearchProbe}, asserted once per test class from the
 * same extension callback that used to do the booting.
 */
@Tag("elasticsearch")
@ExtendWith(AbstractElasticsearchIntegrationTest.EmbeddedElasticsearchExtension.class)
public abstract class AbstractElasticsearchIntegrationTest
{
    private static final Logger LOG = LoggerFactory.getLogger( AbstractElasticsearchIntegrationTest.class );

    protected static Client client;

    protected Path getSnapshotsDir()
    {
        requireElasticsearch( "snapshot repositories are Elasticsearch-only (nodb snapshots are out of Phase 4 scope)" );
        return ElasticsearchFixture.server.getSnapshotsDir();
    }

    /**
     * Leftover debug output (8 call sites across the suites, none of them asserted on). In nodb
     * mode it says so and returns rather than throwing: failing 8 tests over a {@code System.out}
     * call would report a backend problem where there is none.
     */
    protected void printAllIndexContent( final String indexName, final String indexType )
    {
        if ( NodbTestCluster.isEnabled() )
        {
            System.out.println( "\n\n---------- CONTENT (unavailable: nodb mode has no Elasticsearch client) ----------\n\n" );
            return;
        }
        String termQuery = "{\n" + "  \"query\": { \"match_all\": {} }\n" + "}";

        SearchRequestBuilder searchRequest = new SearchRequestBuilder( client, SearchAction.INSTANCE ).setSize( 100 )
            .setIndices( indexName )
            .setTypes( indexType )
            .setSource( termQuery )
            .addFields( "_source" );

        final SearchResponse searchResponse = client.search( searchRequest.request() ).actionGet();

        System.out.println( "\n\n---------- CONTENT --------------------------------" );
        System.out.println( searchResponse.toString() );
        System.out.println( "\n\n" );
    }

    /**
     * "Make everything written so far searchable." In default mode that is a global Elasticsearch
     * refresh; in nodb mode it is the same statement addressed to the nodb search backend (see
     * {@link NodbItestSearchIndex}), and returns {@code null} because there is no
     * {@code RefreshResponse} to give -- no caller in either suite reads the return value.
     */
    protected static RefreshResponse refresh()
    {
        if ( NodbTestCluster.isEnabled() )
        {
            NodbItestSearchIndex.refreshCurrent();
            return null;
        }
        return client.admin().indices().prepareRefresh().execute().actionGet();
    }

    /** No-op in nodb mode: tenant scoping already isolates a class (and a method) -- see the class javadoc. */
    protected static void deleteAllIndices()
    {
        if ( NodbTestCluster.isEnabled() )
        {
            return;
        }
        client.admin().indices().prepareDelete( "_all" ).execute().actionGet();
    }

    private static void requireElasticsearch( final String what )
    {
        if ( NodbTestCluster.isEnabled() )
        {
            throw new UnsupportedOperationException( what + " -- unavailable in nodb mode (Phase 4 Gate F: no embedded Elasticsearch)" );
        }
    }

    static class EmbeddedElasticsearchExtension
        implements BeforeAllCallback, AfterAllCallback
    {
        /**
         * Phase 4 Gate F: releases this class's nodb tenant -- its gRPC channel and, crucially, the
         * server-side outbox indexer thread. A ~150-class suite would otherwise finish holding one
         * live indexer per class for tenants nothing will touch again. Runs from the extension
         * rather than an {@code @AfterAll} because only the extension knows the CONCRETE test class,
         * which is the tenant's key.
         */
        @Override
        public void afterAll( ExtensionContext context )
        {
            if ( NodbTestCluster.isEnabled() )
            {
                NodbTestCluster.get().releaseTenantForClass( context.getRequiredTestClass() );
            }
        }

        @Override
        public void beforeAll( ExtensionContext context )
        {
            if ( NodbTestCluster.isEnabled() )
            {
                // Phase 4 Gate F: nodb mode boots no embedded node -- and proves it, here, at the
                // one point in the suites where a node would have been created. Every test class
                // re-asserts it, so a reintroduced ES construction anywhere fails immediately
                // rather than at whatever later point someone happens to look.
                NoEmbeddedElasticsearchProbe.assertNoEmbeddedElasticsearch(
                    "before test class " + context.getRequiredTestClass().getName() );
                return;
            }
            context.getRoot().getStore( ExtensionContext.Namespace.GLOBAL ).getOrComputeIfAbsent( ElasticsearchFixture.class );
        }
    }

    static class ElasticsearchFixture
        implements AutoCloseable
    {
        static EmbeddedElasticsearchServer server;

        static Path elasticsearchTemporaryFolder;

        ElasticsearchFixture()
            throws IOException
        {
            LOG.info( "Starting up Elasticsearch" );

            elasticsearchTemporaryFolder = Files.createTempDirectory( "elasticsearchFixture" );

            server = new EmbeddedElasticsearchServer( elasticsearchTemporaryFolder );

            client = server.getClient();
        }

        @Override
        public void close()
            throws IOException
        {
            LOG.info( "Shutting down Elasticsearch" );
            if ( client != null )
            {
                client.close();
            }
            if ( server != null )
            {
                server.shutdown();
            }
            MoreFiles.deleteRecursively( elasticsearchTemporaryFolder, RecursiveDeleteOption.ALLOW_INSECURE );
        }
    }

}
