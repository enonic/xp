package com.enonic.xp.core.nodb.corpus;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.core.AbstractNodeTest;
import com.enonic.xp.data.PropertyTree;
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

    /**
     * Subset selectors (Gate 0(e)'s recorded follow-up: a full run is ~45 minutes, which is too
     * slow for the per-batch verification Gates C/D/E actually need).
     * <p>
     * {@code families} and {@code acceptance} are comma-separated, case-insensitive, and AND
     * together; {@code ids} names exact query ids and wins over both. An unmatched selector fails
     * the run rather than silently executing nothing — a gate that "passes" because a typo
     * selected zero rows is worse than no gate.
     */
    private static final String FAMILIES_PROPERTY = "xp.nodb.corpus.families";

    private static final String ACCEPTANCE_PROPERTY = "xp.nodb.corpus.acceptance";

    private static final String IDS_PROPERTY = "xp.nodb.corpus.ids";

    /**
     * Ids to drop from whatever the other selectors chose. Needed because a family is not the same
     * thing as a translation batch: a handful of rows live in a batch-1 family while exercising a
     * later batch's construct ({@code FILTER-06-post-filter} carries an AGGREGATION, the two
     * {@code SORT-0[89]-geodistance} rows are geo sorts). Naming them explicitly is how a
     * batch-1 run stays a batch-1 run without re-labelling the corpus around the gate schedule.
     */
    private static final String EXCLUDE_PROPERTY = "xp.nodb.corpus.exclude";

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
        final List<GoldenQuery> corpus = selected();
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

    // ------------------------------------------------------------------ selection

    /**
     * The corpus rows this run executes. A filtered run is a SUBSET operation on both sides: the
     * diff compares only the selected ids (see {@link #diff}) and a filtered record MERGES into the
     * committed baseline instead of replacing it, so adding one row costs one short ES-mode run
     * rather than a 45-minute full re-record.
     */
    private static List<GoldenQuery> selected()
    {
        final Set<String> ids = csv( IDS_PROPERTY );
        final Set<String> families = csv( FAMILIES_PROPERTY );
        final Set<String> acceptances = csv( ACCEPTANCE_PROPERTY );
        final Set<String> excluded = csv( EXCLUDE_PROPERTY );

        final List<GoldenQuery> all = GoldenCorpus.all();
        if ( ids.isEmpty() && families.isEmpty() && acceptances.isEmpty() && excluded.isEmpty() )
        {
            return all;
        }

        final List<GoldenQuery> selected = all.stream()
            .filter( q -> ids.isEmpty() ? matches( q, families, acceptances ) : ids.contains( q.id().toLowerCase( Locale.ROOT ) ) )
            .filter( q -> !excluded.contains( q.id().toLowerCase( Locale.ROOT ) ) )
            .toList();

        assertTrue( !selected.isEmpty(), "the corpus filter selected NO rows (ids=" + ids + " families=" + families + " acceptance=" +
            acceptances + " exclude=" + excluded + ") -- a gate that runs zero queries is not a gate" );

        System.out.println( "[corpus] filter selected " + selected.size() + " of " + all.size() + " rows (ids=" + ids + " families=" +
                                families + " acceptance=" + acceptances + " exclude=" + excluded + ")" );
        return selected;
    }

    private static boolean matches( final GoldenQuery query, final Set<String> families, final Set<String> acceptances )
    {
        return ( families.isEmpty() || families.contains( query.family().toLowerCase( Locale.ROOT ) ) ) &&
            ( acceptances.isEmpty() || acceptances.contains( query.acceptance().name().toLowerCase( Locale.ROOT ) ) );
    }

    private static Set<String> csv( final String property )
    {
        final String value = System.getProperty( property );
        if ( value == null || value.isBlank() )
        {
            return Set.of();
        }
        return Arrays.stream( value.split( "," ) ).map( String::trim ).filter( s -> !s.isEmpty() ).map( s -> s.toLowerCase( Locale.ROOT ) ).collect(
            Collectors.toUnmodifiableSet() );
    }

    // ------------------------------------------------------------------ modes

    private void record( final List<QueryOutcome> outcomes )
        throws IOException
    {
        final Path baseline = baselinePath();
        CorpusArtifact.write( baseline, "es", merged( baseline, outcomes ) );
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

    /**
     * A filtered record must not truncate the committed baseline. Rows the run did not execute are
     * carried over verbatim; rows it did execute replace their previous recording.
     */
    private static List<QueryOutcome> merged( final Path baseline, final List<QueryOutcome> outcomes )
        throws IOException
    {
        if ( !Files.exists( baseline ) )
        {
            return outcomes;
        }
        final Map<String, QueryOutcome> byId = new LinkedHashMap<>();
        CorpusArtifact.read( baseline ).forEach( outcome -> byId.put( outcome.id(), outcome ) );
        final int carried = byId.size();
        outcomes.forEach( outcome -> byId.put( outcome.id(), outcome ) );
        System.out.println(
            "[corpus] merged " + outcomes.size() + " recorded row(s) into " + carried + " existing baseline row(s) -> " + byId.size() );
        return List.copyOf( byId.values() );
    }

    private void diff( final List<QueryOutcome> outcomes )
        throws IOException
    {
        final Path baseline = baselinePath();
        if ( !Files.exists( baseline ) )
        {
            fail( "no baseline at " + baseline.toAbsolutePath() + " -- run `./gradlew :itest:itest-core:recordNodbCorpus` first" );
        }

        // A filtered run diffs only what it executed: comparing against the whole baseline would
        // report every unselected row as "missing from this run" and drown the real deltas.
        final Set<String> executed = outcomes.stream().map( QueryOutcome::id ).collect( Collectors.toSet() );
        final List<QueryOutcome> expected =
            CorpusArtifact.read( baseline ).stream().filter( outcome -> executed.contains( outcome.id() ) ).toList();

        // Which rows declare an ORDER BY -- read from the corpus TABLE, not from the artifact, so
        // teaching the comparator D7's "EXACT applies to DETERMINISTIC SORTS" needed no re-record.
        CorpusComparator.orderedRows( selected().stream()
                                          .filter( q -> !q.query().get().getOrderBys().isEmpty() )
                                          .map( GoldenQuery::id )
                                          .collect( Collectors.toSet() ) );

        // …and which of those sort by a value the ENGINE computes rather than one XP pre-encoded.
        // Same source and same reason: the comparator narrowing is about what the value IS, and
        // that is a property of the corpus row's query, not of the recording.
        CorpusComparator.engineComputedSortRows( selected().stream()
                                                     .filter( q -> q.query()
                                                         .get()
                                                         .getOrderBys()
                                                         .stream()
                                                         .anyMatch( NodbGoldenCorpusTest::isGeoDistanceOrder ) )
                                                     .map( GoldenQuery::id )
                                                     .collect( Collectors.toSet() ) );

        final List<CorpusComparator.Delta> deltas = CorpusComparator.compare( expected, outcomes );

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

    /** Both order-by forms a {@code geoDistance} sort can arrive in: the NoQL function and the DSL. */
    private static boolean isGeoDistanceOrder( final com.enonic.xp.query.expr.OrderExpr order )
    {
        if ( order instanceof com.enonic.xp.query.expr.DynamicOrderExpr dynamic )
        {
            return "geoDistance".equals( dynamic.getFunction().getName() );
        }
        if ( order instanceof com.enonic.xp.query.expr.DslOrderExpr dsl )
        {
            return "geoDistance".equals( dsl.getType() ) || dsl.getLat() != null;
        }
        return false;
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

        // `onlyInRepoA` exists in repo A and in NO other repository -- that asymmetry is the whole
        // point of SOURCE-03: a multi-index sort on a field one index has never seen is exactly
        // where the hardcoded unmapped_type: long becomes a real failure (Gate 0 item 4).
        final PropertyTree repoAData = new PropertyTree();
        repoAData.addString( "onlyInRepoA", "aaa" );

        createNodeAs( USER_A, REPO_A, "repo-a-node", repoAData );
        createNodeAs( USER_B, REPO_B, "repo-b-node", new PropertyTree() );
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
    private void createNodeAs( final User user, final RepositoryId repositoryId, final String nodeId, final PropertyTree data )
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
                                .data( data )
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
