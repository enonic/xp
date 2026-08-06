package com.enonic.xp.core.nodb.search;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.core.nodb.NodbTenant;
import com.enonic.xp.core.nodb.NodbTestCluster;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.query.expr.QueryExpr;
import com.enonic.xp.query.parser.QueryParser;
import com.enonic.xp.repo.impl.search.NodeSearchService;
import com.enonic.xp.repo.impl.search.NodeSearchServiceImpl;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.storage.spi.IndexDocumentRecord;
import com.enonic.xp.storage.spi.IndexSettings;
import com.enonic.xp.storage.spi.MultiRepoSearchSource;
import com.enonic.xp.storage.spi.NodeSearchIndex;
import com.enonic.xp.storage.spi.SearchHit;
import com.enonic.xp.storage.spi.SearchResult;
import com.enonic.xp.storage.spi.SingleRepoSearchSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Phase 4 Gate B's end-to-end proof: a NoQL string travels the whole path in nodb mode —
 * <pre>
 *   QueryParser.parse → NodeQuery → NodeSearchServiceImpl → SearchRequest
 *     → the QueryExpr→DSL renderer in XP core
 *     → the nodb client's envelope serializer
 *     → gRPC
 *     → NoDB's server-side DSL translator
 *     → OpenSearch
 *     → SearchResult
 * </pre>
 * with no embedded Elasticsearch anywhere in it.
 *
 * <p>This deliberately does NOT extend {@code AbstractNodeTest}: that fixture keeps the search
 * half on embedded Elasticsearch on purpose, and its {@code bootstrap()} depends on the storage
 * and search sides agreeing about whether the system repository exists. Driving
 * {@link NodeSearchService} directly against a purpose-made repository keeps this test about
 * the query path instead of about fixture bootstrap ordering.
 *
 * <p>Requires {@code -Dxp.itest.storage=nodb -Dxp.itest.opensearch=true}; without the second
 * flag the shared cluster has no search backend and the RPCs answer UNIMPLEMENTED.
 *
 * <p>The {@code elasticsearch} tag is this module's {@code integrationTest} selector rather than
 * a statement about the backend — {@code AbstractElasticsearchIntegrationTest} carries it in nodb
 * mode too. In default mode the assumption above skips this class.
 */
@Tag("elasticsearch")
class NodbSearchWireEndToEndTest
{
    private static final Branch MASTER = Branch.from( "master" );

    private static final PrincipalKeys ADMIN = PrincipalKeys.from( PrincipalKey.from( "role:system.admin" ) );

    private NodbTenant tenant;

    private NodeSearchIndex nodeSearchIndex;

    private NodeSearchService searchService;

    private RepositoryId repositoryId;

    @BeforeEach
    void setUp()
    {
        assumeTrue( NodbTestCluster.isSearchEnabled(),
                    "requires -Dxp.itest.storage=nodb -Dxp.itest.opensearch=true (Phase 4 Gate B)" );

        tenant = NodbTestCluster.get().tenantForClass( getClass() );
        nodeSearchIndex = tenant.nodeSearchIndex();
        searchService = new NodeSearchServiceImpl( nodeSearchIndex );

        repositoryId = RepositoryId.from( "gateb." + System.nanoTime() );
        tenant.repositoryStorageAdmin().createIndex( repositoryId, IndexSettings.from( Map.of() ), Map.of() );
        nodeSearchIndex.createIndex( repositoryId, IndexSettings.from( Map.of() ), null );
    }

    @Test
    void aNoqlTermQueryTravelsTheWholeWireAndComesBackAttributed()
    {
        index( repositoryId, document( "node-1", "Fisk og Ost", 42, "/content/first" ) );
        index( repositoryId, document( "node-2", "Brød og Smør", 7, "/content/second" ) );
        nodeSearchIndex.refresh( repositoryId );

        final SearchResult result = query( repositoryId, "data.title = 'Fisk og Ost'" );

        assertEquals( 1, result.getTotalHits() );
        final SearchHit hit = result.getHits().get( 0 );
        assertEquals( "node-1", hit.getId(), "the composite <nodeId>@<branch> document id must not surface" );
        assertEquals( repositoryId.toString(), hit.getIndexName(), "repo attribution comes from the hit's own field" );
        assertEquals( MASTER.getValue(), hit.getIndexType() );
        assertEquals( "/content/first", hit.getReturnValues().getStringValue( com.enonic.xp.node.NodeIndexPath.PATH ) );
    }

    @Test
    void structuredNoqlFamiliesAllTranslate()
    {
        index( repositoryId, document( "node-1", "alpha", 1, "/content/a" ) );
        index( repositoryId, document( "node-2", "beta", 2, "/content/b" ) );
        index( repositoryId, document( "node-3", "gamma", 3, "/content/c" ) );
        nodeSearchIndex.refresh( repositoryId );

        assertEquals( 3, query( repositoryId, "" ).getTotalHits(), "an empty constraint renders as matchAll" );
        assertEquals( 2, query( repositoryId, "data.count > 1" ).getTotalHits() );
        assertEquals( 2, query( repositoryId, "data.count >= 2" ).getTotalHits() );
        assertEquals( 1, query( repositoryId, "data.count < 2" ).getTotalHits() );
        assertEquals( 2, query( repositoryId, "data.title IN ('alpha', 'beta')" ).getTotalHits() );
        assertEquals( 2, query( repositoryId, "data.title NOT IN ('alpha')" ).getTotalHits() );
        assertEquals( 1, query( repositoryId, "data.title LIKE 'alph*'" ).getTotalHits() );
        assertEquals( 2, query( repositoryId, "data.title != 'alpha'" ).getTotalHits() );
        assertEquals( 1, query( repositoryId, "data.title = 'alpha' AND data.count = 1" ).getTotalHits() );
        assertEquals( 2, query( repositoryId, "data.title = 'alpha' OR data.title = 'beta'" ).getTotalHits() );
        assertEquals( 2, query( repositoryId, "NOT (data.title = 'alpha')" ).getTotalHits() );
        assertEquals( 3, query( repositoryId, "range('data.count', '', '')" ).getTotalHits(), "range with empty bounds is exists" );
        assertEquals( 2, query( repositoryId, "range('data.count', 2, 3, 'true', 'true')" ).getTotalHits() );
    }

    @Test
    void aFieldSortIsHonouredAcrossTheWire()
    {
        index( repositoryId, document( "node-1", "alpha", 1, "/content/a" ) );
        index( repositoryId, document( "node-2", "beta", 2, "/content/b" ) );
        nodeSearchIndex.refresh( repositoryId );

        assertEquals( List.of( "node-2", "node-1" ), ids( query( repositoryId, "ORDER BY data.title DESC" ) ) );
        assertEquals( List.of( "node-1", "node-2" ), ids( query( repositoryId, "ORDER BY data.title ASC" ) ) );
    }

    @Test
    void aMultiRepoQueryFansOutAndAttributesEachHit()
    {
        final RepositoryId second = RepositoryId.from( "gatebtwo." + System.nanoTime() );
        tenant.repositoryStorageAdmin().createIndex( second, IndexSettings.from( Map.of() ), Map.of() );

        index( repositoryId, document( "node-a", "shared", 1, "/content/a" ) );
        index( second, document( "node-b", "shared", 1, "/content/b" ) );
        nodeSearchIndex.refresh( repositoryId );
        nodeSearchIndex.refresh( second );

        final MultiRepoSearchSource source = MultiRepoSearchSource.create()
            .add( SingleRepoSearchSource.create().repositoryId( repositoryId ).branch( MASTER ).acl( ADMIN ).build() )
            .add( SingleRepoSearchSource.create().repositoryId( second ).branch( MASTER ).acl( ADMIN ).build() )
            .build();

        final SearchResult result = searchService.query( nodeQuery( "data.title = 'shared'" ), source );

        assertEquals( 2, result.getTotalHits() );
        assertEquals( List.of( repositoryId.toString(), second.toString() ),
                      result.getHits().stream().map( SearchHit::getIndexName ).sorted().toList() );
    }

    @Test
    void anEmptyPrincipalSetIsFailClosed()
    {
        index( repositoryId, document( "node-1", "secret", 1, "/content/a" ) );
        nodeSearchIndex.refresh( repositoryId );

        final SingleRepoSearchSource anonymous = SingleRepoSearchSource.create()
            .repositoryId( repositoryId )
            .branch( MASTER )
            .acl( PrincipalKeys.empty() )
            .build();

        assertEquals( 0, searchService.query( nodeQuery( "" ), anonymous ).getTotalHits() );
    }

    /** The client is a serializer: a construct with no wire form is a loud failure, never a guess. */
    @Test
    void anUntranslatedConstructFailsLoudly()
    {
        assertThrows( RuntimeException.class, () -> query( repositoryId, "fulltext('data.title', 'fisk')" ) );
    }

    // --- plumbing --------------------------------------------------------------------

    private List<String> ids( final SearchResult result )
    {
        return result.getHits().stream().map( SearchHit::getId ).toList();
    }

    private SearchResult query( final RepositoryId repositoryId, final String noql )
    {
        final SearchResult result = searchService.query( nodeQuery( noql ), SingleRepoSearchSource.create()
            .repositoryId( repositoryId )
            .branch( MASTER )
            .acl( ADMIN )
            .build() );
        assertNotNull( result );
        return result;
    }

    private NodeQuery nodeQuery( final String noql )
    {
        final QueryExpr queryExpr = QueryParser.parse( noql );
        return NodeQuery.create().query( queryExpr ).size( 100 ).withPath( true ).build();
    }

    private void index( final RepositoryId repositoryId, final IndexDocumentRecord doc )
    {
        nodeSearchIndex.index( repositoryId, MASTER, doc );
    }

    /**
     * The SPI boundary shape core-repo hands to a backend: canonical XP field names with their
     * value-type postfixes, exactly as {@code IndexItems#asValuesMap()} produces them. Built by
     * hand here so that this test is about the query path rather than about document building,
     * which Gate A already proves.
     */
    private static IndexDocumentRecord document( final String nodeId, final String title, final int count, final String path )
    {
        return new IndexDocumentRecord( nodeId, null,
                                        Map.of( "data.title", List.of( title ), "data.title._analyzed", List.of( title ),
                                                "data.title._orderby", List.of( title.toLowerCase() ),
                                                // Every value type is ALSO indexed as text, which is
                                                // why the bare variant exists for a numeric property.
                                                "data.count", List.of( String.valueOf( count ) ), "data.count._number",
                                                List.of( (double) count ), "_path", List.of( path ), "_path._path", List.of( path ),
                                                "_ts._datetime", List.of( Instant.parse( "2026-08-01T00:00:00Z" ) ),
                                                "_permissions.read", List.of( "role:system.everyone" ) ) );
    }
}
