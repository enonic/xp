package com.enonic.xp.core.nodb;

import java.net.URI;
import java.nio.file.Files;
import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.sql.SQLException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import javax.sql.DataSource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MinIOContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import com.enonic.nodb.engine.TenantContext;
import com.enonic.nodb.engine.TenantProvisioner;
import com.enonic.nodb.engine.binary.BinaryStore;
import com.enonic.nodb.engine.search.OpenSearchConfig;
import com.enonic.nodb.server.NodbServer;
import com.enonic.nodb.server.auth.DevKeys;
import com.enonic.nodb.server.auth.JwtIssuer;
import com.enonic.nodb.server.auth.Scope;

import com.enonic.xp.storage.nodb.NodbNodeSearchIndex;
import com.enonic.xp.storage.nodb.NodbNodeStore;
import com.enonic.xp.storage.nodb.NodbRepositoryStorageAdmin;
import com.enonic.xp.storage.nodb.NodbStorageClient;

/**
 * Phase 1 Gate C (nodb/BUILD-PHASE-1.md): boots ONE real NoDB stack -- a
 * {@code postgres:17} testcontainer, an RSA dev-issuer keypair, and a real
 * {@link NodbServer} (the actual nodb/server + nodb/engine classes, not a stub -- see this
 * module's build.gradle for the files() dependency on nodb's built jars) -- and shares it
 * across every itest class in the JVM. This mirrors {@code nodb/bench}'s
 * {@code BenchEnvironment}, minus the nodb-native {@code NodbClient}: this fixture connects
 * with XP's OWN Gate B client ({@link NodbNodeStore}/{@link NodbRepositoryStorageAdmin}
 * over {@link NodbStorageClient}) instead, since exercising that client end-to-end against
 * the real server is the whole point of this gate.
 *
 * <p><b>Phase 2 Gate C addition (nodb/BUILD-PHASE-2.md):</b> also starts a {@code minio/minio}
 * testcontainer and a real {@link BinaryStore} (the same nodb/engine class production uses,
 * constructed the same way {@code BinariesServiceIntegrationTest} in nodb/server does --
 * MinIO's S3-compatible endpoint, path-style addressing, no STS client so {@code presignGet}
 * exercises its base-credential fallback branch, same documented test limitation as that
 * gate) wired into the shared {@link NodbServer} via its 4-arg {@code create} overload, so
 * every binary write/read RPC ({@code PutBinary}/{@code GetBinary}/{@code BinaryExists}/
 * {@code DeleteBinary}/{@code PresignGet}) the server handles for this JVM's itests actually
 * lands on real S3-compatible storage, not {@link BinaryStore#fromEnv()}'s environment-
 * dependent fallback. {@link #s3Client()}/{@link #s3Bucket()} expose the same raw S3 client
 * for itests that need ground truth independent of NoDB's own reported success (e.g. "the
 * object physically exists under the tenant prefix"), mirroring
 * {@code BinariesServiceIntegrationTest}'s own assertions one layer up, through XP's
 * {@code BlobStore} SPI instead of raw gRPC stubs.
 *
 * <p><b>Isolation:</b> ONE Postgres container + ONE NodbServer per JVM (starting either is
 * the expensive part -- container startup, migrations); one tenant (Postgres schema) PER
 * TEST CLASS, shared across that class's test methods ({@link #tenantForClass}), memoized
 * by concrete test class. This deliberately matches the ES side's own reset granularity
 * ({@code AbstractNodeTest}'s {@code deleteAllIndices()} runs once per class in
 * {@code @BeforeAll}, never per method) rather than going finer -- an earlier per-method
 * design was tried and found to violate an invariant {@code SystemRepoInitializer} depends
 * on: {@code RepositoryCreator#isInitialized} checks BOTH the storage side (nodb, would be
 * fresh every method) AND the search side ({@code NodeSearchIndex}/ES, which this fixture
 * intentionally leaves untouched -- hybrid mode's search half stays on the shared ES
 * cluster per the work order's scope constraint #1). With per-method storage resets but
 * per-class search persistence, the second method's {@code bootstrap()} would see
 * "search says system-repo exists, storage says it doesn't", attempt to recreate it, and
 * fail with {@code RepositoryAlreadyExistsException} from the search-index half of
 * {@code NodeRepositoryServiceImpl#create}. Per-class tenant reuse keeps storage and
 * search evolving at the SAME granularity (both persist across a class's methods, both
 * reset only at the next class's {@code deleteAllIndices()}), exactly like default
 * (elasticsearch) mode -- restoring the invariant {@code isInitialized()} relies on.
 * {@link #freshTenant()} remains available for tests that deliberately want an
 * ADDITIONAL, fully isolated tenant beyond the fixture's own (e.g. the cross-tenant spot
 * check, which needs two tenants that are neither of the fixture's).
 *
 * <p><b>Phase 4 Gate F: per-METHOD tenants become available, and correct.</b> The paragraph
 * above rejected them for one specific reason -- storage reset per method while SEARCH persisted
 * per class, so {@code RepositoryCreator#isInitialized} saw the two halves disagree. Gate F
 * removes that asymmetry: nodb mode owns BOTH halves, so a fresh tenant resets storage and search
 * together (new Postgres schema, new OpenSearch indices) and {@code isInitialized()} sees a
 * consistently empty world -- exactly what a per-method {@code deleteAllIndices()} gives the ES
 * side. {@link #perMethodTenant()} is therefore the nodb equivalent of {@code AbstractNodeTest}'s
 * {@code clearBeforeEach}, which is what unblocks {@code FindNodesByMultiRepoQueryCommandTest}
 * (fixed-name {@code repo1}/{@code repo2} recreated per method) and
 * {@code VersionTableVacuumTaskTest} -- the two fixture-granularity exclusions Gates C/D carried
 * here. It stays tied to {@code clearBeforeEach} rather than becoming the default because
 * provisioning a schema per method is exactly the cost that flag already warns about.
 *
 * <p>Enabled via the {@code xp.itest.storage=nodb} system property (see
 * {@link #isEnabled()}); {@code AbstractNodeTest} is the only caller. Never torn down
 * mid-suite -- a JVM shutdown hook stops the container/server once, when the test JVM
 * exits (Gradle forks one JVM per test task run, so this is scoped correctly without any
 * itest class needing to know about teardown).
 */
public final class NodbTestCluster
{
    private static final String SYSTEM_PROPERTY = "xp.itest.storage";

    private static final String BACKEND_NODB = "nodb";

    /**
     * Phase 4 Gate B introduced this as an opt-in ({@code -Dxp.itest.opensearch=true}): an
     * OpenSearch container handed to the {@link NodbServer} so the {@code NodeSearch} RPCs answer
     * for real instead of UNIMPLEMENTED. It was opt-in because it adds a multi-hundred-megabyte
     * container to every nodb itest run while only the query itests needed it, and Gate B recorded
     * that "Gate F is where it stops being optional".
     * <p>
     * <b>Phase 4 Gate F: {@code xp.itest.storage=nodb} now IMPLIES the search backend</b> — the
     * property is inverted into an escape hatch, {@code -Dxp.itest.opensearch=false}, and nothing
     * in the build passes it. The reason is not convenience: with embedded Elasticsearch gone from
     * nodb mode there is no other search backend to fall back to, so a nodb-mode run without
     * OpenSearch is not a cheaper run, it is a run with no search at all. Leaving the flag opt-in
     * would mean the DEFAULT nodb invocation silently measured the hybrid wiring — precisely the
     * "harness pointed at the wrong engine" failure this phase hit three times (nodb/FINDINGS.md).
     * The escape hatch survives only for storage-only debugging of the SPI layer: with it, nodb
     * mode has NO search backend (the {@code NodeSearch} RPCs answer UNIMPLEMENTED) rather than
     * falling back to Elasticsearch.
     */
    private static final String OPENSEARCH_PROPERTY = "xp.itest.opensearch";

    /** STOCK image, pinned to the minor the managed service runs (D8: no analysis-icu needed). */
    private static final String OPENSEARCH_IMAGE = "opensearchproject/opensearch:3.7.0";

    private static volatile NodbTestCluster INSTANCE;

    private static final Object LOCK = new Object();

    private static final String MINIO_BUCKET = "xp-itest-nodb-binaries";

    private final PostgreSQLContainer<?> postgres;

    private final HikariDataSource dataSource;

    private final MinIOContainer minio;

    private final S3Client s3Client;

    private final S3Presigner s3Presigner;

    private final BinaryStore binaryStore;

    private final GenericContainer<?> opensearch;

    private final NodbServer server;

    private final RSAPrivateKey issuerPrivateKey;

    private final RSAPublicKey issuerPublicKey;

    private final AtomicLong tenantSequence = new AtomicLong();

    /** Memoized per concrete test class -- see class javadoc's isolation section. */
    private final Map<Class<?>, NodbTenant> tenantsByClass = new ConcurrentHashMap<>();

    private NodbTestCluster( PostgreSQLContainer<?> postgres, HikariDataSource dataSource, MinIOContainer minio, S3Client s3Client,
                              S3Presigner s3Presigner, BinaryStore binaryStore, GenericContainer<?> opensearch, NodbServer server,
                              KeyPair issuerKeyPair )
    {
        this.opensearch = opensearch;
        this.postgres = postgres;
        this.dataSource = dataSource;
        this.minio = minio;
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
        this.binaryStore = binaryStore;
        this.server = server;
        this.issuerPrivateKey = (RSAPrivateKey) issuerKeyPair.getPrivate();
        this.issuerPublicKey = (RSAPublicKey) issuerKeyPair.getPublic();
    }

    /** True when {@code -Dxp.itest.storage=nodb} was passed to this JVM. */
    public static boolean isEnabled()
    {
        return BACKEND_NODB.equals( System.getProperty( SYSTEM_PROPERTY ) );
    }

    /**
     * True when the shared stack also carries an OpenSearch backend: implied by nodb mode as of
     * Gate F, and switched off only by an explicit {@code -Dxp.itest.opensearch=false} -- see
     * {@link #OPENSEARCH_PROPERTY}.
     */
    public static boolean isSearchEnabled()
    {
        return isEnabled() && !"false".equalsIgnoreCase( System.getProperty( OPENSEARCH_PROPERTY, "true" ) );
    }

    /** Lazily starts the shared stack on first use; a no-op on every subsequent call in this JVM. */
    public static NodbTestCluster get()
    {
        NodbTestCluster instance = INSTANCE;
        if ( instance != null )
        {
            return instance;
        }
        synchronized ( LOCK )
        {
            if ( INSTANCE == null )
            {
                INSTANCE = start();
                Runtime.getRuntime().addShutdownHook( new Thread( INSTANCE::shutdown, "nodb-test-cluster-shutdown" ) );
            }
            return INSTANCE;
        }
    }

    private static NodbTestCluster start()
    {
        try
        {
            PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>( "postgres:17" );
            postgres.start();

            HikariConfig hikariConfig = new HikariConfig();
            hikariConfig.setJdbcUrl( postgres.getJdbcUrl() );
            hikariConfig.setUsername( postgres.getUsername() );
            hikariConfig.setPassword( postgres.getPassword() );
            hikariConfig.setMaximumPoolSize( 16 );
            HikariDataSource dataSource = new HikariDataSource( hikariConfig );

            // Phase 2 Gate C: same MinIO image / construction shape as nodb/server's own
            // BinariesServiceIntegrationTest -- a real S3-compatible object store, not a
            // stub, backing every binary itest run in this JVM.
            MinIOContainer minio = new MinIOContainer( "minio/minio:RELEASE.2024-11-07T00-52-20Z" );
            minio.start();

            Region region = Region.US_EAST_1;
            URI endpoint = URI.create( minio.getS3URL() );
            StaticCredentialsProvider credentials =
                StaticCredentialsProvider.create( AwsBasicCredentials.create( minio.getUserName(), minio.getPassword() ) );
            S3Configuration serviceConfiguration = S3Configuration.builder().pathStyleAccessEnabled( true ).build();

            S3Client s3Client = S3Client.builder()
                .region( region )
                .endpointOverride( endpoint )
                .credentialsProvider( credentials )
                .serviceConfiguration( serviceConfiguration )
                .build();
            s3Client.createBucket( CreateBucketRequest.builder().bucket( MINIO_BUCKET ).build() );

            S3Presigner s3Presigner = S3Presigner.builder()
                .region( region )
                .endpointOverride( endpoint )
                .credentialsProvider( credentials )
                .serviceConfiguration( serviceConfiguration )
                .build();

            // No STS client/role ARN, same documented Gate A test limitation as
            // BinariesServiceIntegrationTest: presignGet exercises its base-credential
            // fallback branch here, not the STS-scoped production path.
            BinaryStore binaryStore =
                new BinaryStore( s3Client, s3Presigner, null, null, MINIO_BUCKET, region, endpoint, serviceConfiguration );

            GenericContainer<?> opensearch = null;
            OpenSearchConfig openSearchConfig = OpenSearchConfig.fromEnv();
            if ( isSearchEnabled() )
            {
                opensearch = new GenericContainer<>( OPENSEARCH_IMAGE ).withExposedPorts( 9200 )
                    .withEnv( "discovery.type", "single-node" )
                    .withEnv( "DISABLE_SECURITY_PLUGIN", "true" )
                    .withEnv( "DISABLE_INSTALL_DEMO_CONFIG", "true" )
                    .withEnv( "OPENSEARCH_JAVA_OPTS", "-Xms512m -Xmx512m" )
                    .waitingFor( Wait.forHttp( "/_cluster/health?wait_for_status=yellow&timeout=30s" )
                                     .forPort( 9200 )
                                     .forStatusCode( 200 )
                                     .withStartupTimeout( Duration.ofMinutes( 4 ) ) );
                opensearch.start();

                // refresh_interval -1, same reasoning as nodb's own container tests: with the
                // production 1s interval a path that forgets to await a refresh still passes on
                // a timer, and the refresh contract silently stops being tested.
                openSearchConfig =
                    OpenSearchConfig.of( "http://" + opensearch.getHost() + ":" + opensearch.getMappedPort( 9200 ) )
                        .withRefreshInterval( "-1" );
            }

            KeyPair issuerKeyPair = DevKeys.loadOrGenerate( Files.createTempDirectory( "nodb-itest-dev-keys" ) );
            NodbServer server =
                NodbServer.create( 0, dataSource, (RSAPublicKey) issuerKeyPair.getPublic(), binaryStore, openSearchConfig );

            return new NodbTestCluster( postgres, dataSource, minio, s3Client, s3Presigner, binaryStore, opensearch, server,
                                        issuerKeyPair );
        }
        catch ( Exception e )
        {
            throw new IllegalStateException( "Failed to start the shared NoDB itest cluster", e );
        }
    }

    /**
     * Raw S3 client against the shared MinIO container -- for itests that need ground truth
     * independent of what NoDB itself reports (Gate C's invariant/isolation itests): e.g.
     * confirming an object physically exists (or does not) under a given key/prefix, the
     * same style of assertion {@code BinariesServiceIntegrationTest} (nodb/server) makes
     * directly against S3 rather than only through the RPC surface under test.
     */
    public S3Client s3Client()
    {
        return s3Client;
    }

    /** The bucket every tenant's binaries share (isolated from one another by key prefix, {@code <tenantId>/binary/<hash>}). */
    public String s3Bucket()
    {
        return MINIO_BUCKET;
    }

    /**
     * Phase 3 Gate C addition (nodb/BUILD-PHASE-3.md): raw JDBC access to the shared
     * {@code postgres:17} container for itests that need SQL ground truth independent of
     * what NoDB itself reports over the wire (e.g. "the {@code payload} table in tenant
     * X's own schema has exactly one row for this hash"). Same {@link HikariDataSource}
     * every {@link NodbServer}/{@link TenantProvisioner} call in this JVM already uses --
     * a tenant's schema name is exactly its {@link NodbTenant#tenantId()} (see {@link
     * com.enonic.nodb.engine.TenantContext}'s javadoc), so a caller only needs to
     * schema-qualify its query, e.g. {@code SELECT ... FROM "<tenantId>".payload WHERE
     * hash = ?}. Read-only use expected; this does not run inside {@code Tx.inTenantTx}
     * (no {@code SET LOCAL ROLE}), so it reads with the pooled connection's own (superuser
     * -- see {@code postgres.getUsername()} in {@link #start()}) privileges, deliberately
     * bypassing the tenant-role boundary production traffic is confined to -- acceptable
     * for a test-only ground-truth probe, never something production code should do.
     */
    public DataSource dataSource()
    {
        return dataSource;
    }

    /**
     * The tenant for {@code testClass}, provisioning one via {@link #freshTenant()} on
     * first use and memoizing it for every subsequent call with the same class -- see the
     * class javadoc's isolation section for why this is class-scoped, not per-method.
     */
    public NodbTenant tenantForClass( Class<?> testClass )
    {
        return tenantsByClass.computeIfAbsent( testClass, c -> freshTenant() );
    }

    /**
     * A brand-new tenant for a single test METHOD -- the nodb counterpart of
     * {@code AbstractNodeTest}'s {@code clearBeforeEach} (Phase 4 Gate F; see the class javadoc's
     * isolation section for why this is only correct now that nodb owns the search half too).
     * Identical to {@link #freshTenant()}; it exists as a separate, named entry point because the
     * two callers mean different things -- "an extra tenant to compare against" versus "reset the
     * world between methods" -- and the caller is expected to {@code close()} it in its teardown,
     * which the class-scoped tenant must never do.
     */
    public NodbTenant perMethodTenant()
    {
        return freshTenant();
    }

    /**
     * Releases everything the shared stack holds for {@code tenant}: the client's gRPC channel, the
     * server's outbox indexer for that tenant, and the tenant's OpenSearch indices. The Postgres
     * schema is left behind, as {@link NodbTenant#close()} documents -- the container dies at JVM
     * exit anyway.
     * <p>
     * Phase 4 Gate F: neither of the two server-side halves existed before this gate, and the suite
     * found both by failing. (1) One indexer per tenant accumulated for the life of the server --
     * a polling thread plus a share of the 16-connection pool -- and at ~80 tenants
     * {@code awaitRefresh} stopped returning. (2) The tenant's OpenSearch indices were never removed,
     * and index metadata is heap: the container's 512 MB filled up and its parent circuit breaker
     * started answering HTTP 429. Both are shared-cell tenant-teardown gaps, not test artifacts --
     * see {@code NodbServer#dropTenant}.
     */
    public void release( NodbTenant tenant )
    {
        tenant.close();
        server.dropTenant( tenant.tenantId() );
    }

    /**
     * Releases the memoized tenant for {@code testClass}, if any -- called once per test class when
     * it finishes (see {@code AbstractElasticsearchIntegrationTest}'s extension). Without this, a
     * ~150-class suite ends with ~150 live indexer threads for tenants nothing will touch again.
     */
    public void releaseTenantForClass( Class<?> testClass )
    {
        NodbTenant tenant = tenantsByClass.remove( testClass );
        if ( tenant != null )
        {
            release( tenant );
        }
    }

    /**
     * Provisions a brand-new tenant (Postgres schema, idempotent DDL against the shared
     * container) and returns a ready-to-use {@link NodbTenant}: a real gRPC connection
     * (loopback TCP, matching production topology -- {@link NodbServer} binds a real port,
     * same as {@code BenchEnvironment}) authenticated with a freshly-minted RUNTIME-scope
     * token (repository lifecycle is an ordinary runtime operation in XP -- see
     * {@code nodb.proto}'s Phase 1 gate B scope-model correction; a runtime token is
     * therefore correct here, not merely sufficient). Always fresh/never memoized -- see
     * {@link #tenantForClass} for the memoized per-test-class variant
     * {@code AbstractNodeTest} actually uses.
     */
    public NodbTenant freshTenant()
    {
        // TenantContext requires ^[a-z][a-z0-9]{2,30}$ -- at least 3 characters total.
        // Long.toString(n, 36) is a SINGLE character for n in [1,35], so a bare "t" + that
        // digit is only 2 characters and fails validation; the "itest" prefix guarantees
        // the minimum length regardless of how small the sequence value is.
        String tenantId = "itest" + Long.toString( tenantSequence.incrementAndGet(), 36 );
        try
        {
            new TenantProvisioner( dataSource, postgres.getUsername() ).provision( new TenantContext( tenantId ) );
        }
        catch ( SQLException e )
        {
            throw new IllegalStateException( "Failed to provision nodb itest tenant [" + tenantId + "]", e );
        }

        String runtimeToken =
            JwtIssuer.mint( issuerPrivateKey, issuerPublicKey, tenantId, Scope.RUNTIME, "itest-runtime", Duration.ofHours( 1 ) );

        NodbStorageClient client = new NodbStorageClient();
        client.activate( Map.of( "backend", BACKEND_NODB, "nodbEndpoint", "localhost:" + server.getPort(), "nodbToken", runtimeToken ) );

        return new NodbTenant( tenantId, client, new NodbNodeStore( client ), new NodbRepositoryStorageAdmin( client ),
                               new NodbNodeSearchIndex( client ) );
    }

    private void shutdown()
    {
        // NodbServer#shutdown() already closes the HikariDataSource it was constructed
        // with (same instance as this.dataSource) -- do not close it a second time here.
        server.shutdown();
        // BinaryStore#close() closes s3Client/s3Presigner (the same instances this.s3Client
        // exposes for test assertions) -- do not close them a second time here either.
        binaryStore.close();
        minio.stop();
        if ( opensearch != null )
        {
            opensearch.stop();
        }
        postgres.stop();
    }
}
