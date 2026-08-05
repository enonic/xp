package com.enonic.xp.core.nodb.corpus;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.core.AbstractNodeTest;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.repo.impl.node.NodeHelper;
import com.enonic.xp.repository.CreateRepositoryParams;
import com.enonic.xp.repository.RepositoryConstants;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.storage.spi.MultiRepoSearchSource;
import com.enonic.xp.storage.spi.ReturnFields;
import com.enonic.xp.storage.spi.SearchResult;
import com.enonic.xp.storage.spi.SearchSource;
import com.enonic.xp.storage.spi.SingleRepoSearchSource;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Gate 0(e) of nodb/BUILD-PHASE-4.md: the golden-query corpus harness.
 * <p>
 * <b>Record</b> the ES-mode baseline:
 * <pre>./gradlew :itest:itest-core:recordNodbCorpus</pre>
 * <b>Diff</b> a run against the committed baseline (this is what Gates C/D/E run):
 * <pre>./gradlew :itest:itest-core:diffNodbCorpus</pre>
 * Both tasks drive this one test class; the mode comes from {@code -Dxp.nodb.corpus.mode}. There
 * is deliberately no per-query test method: the corpus is a data table
 * ({@link GoldenCorpus#all()}), so adding a query later costs one line and no new plumbing.
 * <p>
 * The queries are issued through {@code NodeSearchServiceImpl#query} rather than through
 * {@code FindNodesByQueryCommand}, for one reason: {@code FindNodesByQueryResult} drops
 * {@code maxScore}, and reproducing (or deliberately dropping) the GET_ALL
 * {@code maxScore}-comes-from-the-final-empty-page NaN quirk is an explicit Gate 0(c) item. The SPI
 * {@code SearchResult} carries it, along with per-hit index/type attribution, so the recorder sees
 * exactly what the storage seam returns.
 */
class NodbGoldenCorpusTest
    extends AbstractNodeTest
{
    private static final String MODE_PROPERTY = "xp.nodb.corpus.mode";

    private static final String BASELINE_PROPERTY = "xp.nodb.corpus.baseline";

    private static final String DEFAULT_BASELINE = "src/test/resources/nodb/corpus/es-baseline.json";

    private static final Path REPORT_DIR = Path.of( "build", "nodb-corpus" );

    /** Fixed ids: they end up in the committed artifact, so they must not depend on the clock. */
    private static final RepositoryId REPO_A = RepositoryId.from( "corpus-repo-a" );

    private static final RepositoryId REPO_B = RepositoryId.from( "corpus-repo-b" );

    private static final User USER_A =
        User.create().key( PrincipalKey.ofUser( IdProviderKey.system(), "corpus-user-a" ) ).login( "corpus-user-a" ).build();

    private static final User USER_B =
        User.create().key( PrincipalKey.ofUser( IdProviderKey.system(), "corpus-user-b" ) ).login( "corpus-user-b" ).build();

    NodbGoldenCorpusTest()
    {
        // A shared ES fixture would let a previous class's indices leak into MATCHALL/GET_ALL rows.
        // The corpus records absolute results, so it needs a clean slate.
        super( true );
    }

    @BeforeEach
    void seedCorpus()
    {
        createDefaultRootNode();
        CorpusFixture.seed( this::createNode );
        seedMultiRepo();
        nodeService.refresh( RefreshMode.ALL );
    }

    @Test
    void golden_query_corpus()
        throws IOException
    {
        final String mode = System.getProperty( MODE_PROPERTY, "diff" );
        final List<GoldenQuery> corpus = GoldenCorpus.all();
        final List<QueryOutcome> outcomes = run( corpus );

        Files.createDirectories( REPORT_DIR );
        CorpusArtifact.write( REPORT_DIR.resolve( "actual.json" ), "es", outcomes );

        summarize( corpus, outcomes );

        if ( "record".equals( mode ) )
            {
            record( outcomes );
        }
        else
        {
            diff( outcomes );
        }
    }

    // ------------------------------------------------------------------ modes

    private void record( final List<QueryOutcome> outcomes )
        throws IOException
    {
        final Path baseline = baselinePath();
        CorpusArtifact.write( baseline, "es", outcomes );
        System.out.println( "[corpus] recorded " + outcomes.size() + " queries to " + baseline.toAbsolutePath() );

        // Recording is not a licence to record nothing: a query that silently returns no hits, no
        // buckets and no suggestions proves nothing about either backend.
        final List<String> empty = new ArrayList<>();
        for ( final QueryOutcome outcome : outcomes )
        {
            if ( !outcome.isMeaningful() && !allowEmpty( outcome.id() ) )
            {
                empty.add( outcome.id() );
            }
        }
        assertTrue( empty.isEmpty(), "corpus rows produced no hits, no aggregations and no suggestions, and are not tagged " +
            "allowEmpty -- fix the fixture or the query: " + empty );
    }

    private void diff( final List<QueryOutcome> outcomes )
        throws IOException
    {
        final Path baseline = baselinePath();
        if ( !Files.exists( baseline ) )
        {
            fail( "no baseline at " + baseline.toAbsolutePath() + " -- run `./gradlew :itest:itest-core:recordNodbCorpus` first" );
        }

        final List<CorpusComparator.Delta> deltas = CorpusComparator.compare( CorpusArtifact.read( baseline ), outcomes );

        final List<CorpusComparator.Delta> failures =
            deltas.stream().filter( d -> d.severity() == CorpusComparator.Severity.FAILURE ).toList();
        final List<CorpusComparator.Delta> documented =
            deltas.stream().filter( d -> d.severity() == CorpusComparator.Severity.DOCUMENTED ).toList();

        final Path documentedFile = REPORT_DIR.resolve( "documented-deltas.txt" );
        Files.writeString( documentedFile, documented.isEmpty()
                               ? "no documented deltas\n"
                               : documented.stream().map( CorpusComparator.Delta::toString ).reduce( "", ( a, b ) -> a + b + "\n\n" ),
                           StandardCharsets.UTF_8 );

        System.out.println( "[corpus] diffed " + outcomes.size() + " queries against " + baseline.toAbsolutePath() );
        System.out.println( "[corpus] " + failures.size() + " FAILURE delta(s), " + documented.size() +
                                " DOCUMENTED delta(s) -> " + documentedFile.toAbsolutePath() );
        documented.forEach( d -> System.out.println( "[corpus] " + d ) );

        if ( !failures.isEmpty() )
        {
            fail( failures.size() + " corpus acceptance violation(s):\n" +
                      failures.stream().map( CorpusComparator.Delta::toString ).reduce( "", ( a, b ) -> a + b + "\n\n" ) );
        }
    }

    private void summarize( final List<GoldenQuery> corpus, final List<QueryOutcome> outcomes )
    {
        final Map<String, Integer> byFamily = new LinkedHashMap<>();
        final Map<String, Integer> byAcceptance = new LinkedHashMap<>();
        int meaningful = 0;
        int errored = 0;
        for ( final QueryOutcome outcome : outcomes )
        {
            byFamily.merge( outcome.family(), 1, Integer::sum );
            byAcceptance.merge( outcome.acceptance(), 1, Integer::sum );
            if ( outcome.isMeaningful() )
            {
                meaningful++;
            }
            if ( outcome.error() != null )
            {
                errored++;
            }
        }
        System.out.println( "[corpus] queries=" + corpus.size() + " nonEmpty=" + meaningful + " empty=" + ( outcomes.size() - meaningful ) +
                                " threw=" + errored );
        System.out.println( "[corpus] byAcceptance=" + byAcceptance );
        System.out.println( "[corpus] byFamily=" + byFamily );
        outcomes.stream()
            .filter( o -> !o.isMeaningful() )
            .forEach( o -> System.out.println( "[corpus] EMPTY " + o.id() + " (allowEmpty=" + allowEmpty( o.id() ) + ") totalHits=" +
                                                   o.totalHits() + " error=" + o.error() ) );
    }

    // ------------------------------------------------------------------ execution

    private List<QueryOutcome> run( final List<GoldenQuery> corpus )
    {
        final UnaryOperator<String> sanitizer = sanitizer();
        final List<QueryOutcome> outcomes = new ArrayList<>();

        for ( final GoldenQuery query : corpus )
        {
            try
            {
                final NodeQuery nodeQuery = query.query().get();
                final SearchResult result =
                    NodeHelper.runAsAdmin( () -> searchService.query( nodeQuery, ReturnFields.empty(), source( query.source() ) ) );
                outcomes.add( CorpusRecorder.record( query, result, sanitizer ) );
            }
            catch ( Exception | AssertionError e )
            {
                // A construct that throws today is a fact about the current backend, so it is
                // recorded rather than aborting the run: GAP rows exist precisely to pin such facts.
                outcomes.add( CorpusRecorder.failed( query, e, sanitizer ) );
            }
        }
        return outcomes;
    }

    /**
     * The ACL behaviour under test lives in the search source's principal set, not in the ambient
     * context (the query is issued as admin so the ambient context can never mask a filter bug).
     */
    private SearchSource source( final SourceKind kind )
    {
        return switch ( kind )
        {
            case DEFAULT_USER -> single( testRepoId, WS_DEFAULT, PrincipalKeys.from( TEST_DEFAULT_USER.getKey(), RoleKeys.AUTHENTICATED,
                                                                                     RoleKeys.EVERYONE ) );
            case ADMIN -> single( testRepoId, WS_DEFAULT, PrincipalKeys.from( RoleKeys.ADMIN ) );
            case EMPTY_PRINCIPALS -> single( testRepoId, WS_DEFAULT, PrincipalKeys.empty() );
            case SECRET_USER -> single( testRepoId, WS_DEFAULT, PrincipalKeys.from( CorpusFixture.SECRET_USER, RoleKeys.AUTHENTICATED,
                                                                                   RoleKeys.EVERYONE ) );
            case MULTI_REPO_BOTH_ALLOWED -> MultiRepoSearchSource.create()
                .add( single( REPO_A, RepositoryConstants.MASTER_BRANCH, PrincipalKeys.from( USER_A.getKey() ) ) )
                .add( single( REPO_B, RepositoryConstants.MASTER_BRANCH, PrincipalKeys.from( USER_B.getKey() ) ) )
                .build();
            case MULTI_REPO_ONE_DENIED -> MultiRepoSearchSource.create()
                .add( single( REPO_A, RepositoryConstants.MASTER_BRANCH, PrincipalKeys.from( USER_A.getKey() ) ) )
                .add( single( REPO_B, RepositoryConstants.MASTER_BRANCH, PrincipalKeys.from( USER_A.getKey() ) ) )
                .build();
        };
    }

    private static SingleRepoSearchSource single( final RepositoryId repositoryId, final Branch branch, final PrincipalKeys acl )
    {
        return SingleRepoSearchSource.create().repositoryId( repositoryId ).branch( branch ).acl( acl ).build();
    }

    // ------------------------------------------------------------------ fixture: extra repos

    private void seedMultiRepo()
    {
        createRepo( REPO_A, USER_A );
        createRepo( REPO_B, USER_B );

        createNodeAs( USER_A, REPO_A, "repo-a-node" );
        createNodeAs( USER_B, REPO_B, "repo-b-node" );
    }

    private void createRepo( final RepositoryId repositoryId, final User owner )
    {
        NodeHelper.runAsAdmin( () -> this.repositoryService.createRepository( CreateRepositoryParams.create()
                                                                                 .repositoryId( repositoryId )
                                                                                 .rootPermissions( AccessControlList.create()
                                                                                                       .add( AccessControlEntry.create()
                                                                                                                 .principal(
                                                                                                                     owner.getKey() )
                                                                                                                 .allowAll()
                                                                                                                 .build() )
                                                                                                       .build() )
                                                                                 .build() ) );
    }

    /**
     * The refresh has to happen INSIDE the repository's own context: {@code nodeService.refresh}
     * refreshes the current context's indices only, so refreshing once at the end of the main
     * fixture leaves the two extra repositories unsearchable and both multi-source rows silently
     * return zero hits. Keeping the refresh context-scoped (rather than reaching for the ES-only
     * global {@code refresh()}) is also what keeps the harness usable in nodb mode later.
     */
    private void createNodeAs( final User user, final RepositoryId repositoryId, final String nodeId )
    {
        ContextBuilder.create()
            .repositoryId( repositoryId )
            .branch( RepositoryConstants.MASTER_BRANCH )
            .authInfo( AuthenticationInfo.create().user( user ).principals( user.getKey() ).build() )
            .build()
            .runWith( () -> {
                createNode( CreateNodeParams.create()
                                .setNodeId( NodeId.from( nodeId ) )
                                .name( nodeId )
                                .parent( NodePath.ROOT )
                                .build() );
                nodeService.refresh( RefreshMode.ALL );
            } );
    }

    // ------------------------------------------------------------------ plumbing

    /**
     * {@code AbstractNodeTest} derives {@code testRepoId} from {@code System.currentTimeMillis()},
     * so the physical index name of every single-repo hit changes on every run. Rewriting it to a
     * placeholder is what makes the artifact committable; the fixed multi-repo ids are left alone
     * precisely so attribution stays assertable.
     */
    private UnaryOperator<String> sanitizer()
    {
        final String repo = testRepoId.toString();
        return value -> value == null ? null : value.replace( repo, "<testrepo>" );
    }

    private static Path baselinePath()
    {
        return Path.of( System.getProperty( BASELINE_PROPERTY, DEFAULT_BASELINE ) );
    }

    private static boolean allowEmpty( final String id )
    {
        return GoldenCorpus.all().stream().filter( q -> q.id().equals( id ) ).findFirst().map( GoldenQuery::allowEmpty ).orElse( false );
    }
}
