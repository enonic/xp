package com.enonic.xp.core.nodb;

import java.nio.file.Files;
import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.sql.SQLException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import org.testcontainers.containers.PostgreSQLContainer;

import com.enonic.nodb.engine.TenantContext;
import com.enonic.nodb.engine.TenantProvisioner;
import com.enonic.nodb.server.NodbServer;
import com.enonic.nodb.server.auth.DevKeys;
import com.enonic.nodb.server.auth.JwtIssuer;
import com.enonic.nodb.server.auth.Scope;

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

    private static volatile NodbTestCluster INSTANCE;

    private static final Object LOCK = new Object();

    private final PostgreSQLContainer<?> postgres;

    private final HikariDataSource dataSource;

    private final NodbServer server;

    private final RSAPrivateKey issuerPrivateKey;

    private final RSAPublicKey issuerPublicKey;

    private final AtomicLong tenantSequence = new AtomicLong();

    /** Memoized per concrete test class -- see class javadoc's isolation section. */
    private final Map<Class<?>, NodbTenant> tenantsByClass = new ConcurrentHashMap<>();

    private NodbTestCluster( PostgreSQLContainer<?> postgres, HikariDataSource dataSource, NodbServer server, KeyPair issuerKeyPair )
    {
        this.postgres = postgres;
        this.dataSource = dataSource;
        this.server = server;
        this.issuerPrivateKey = (RSAPrivateKey) issuerKeyPair.getPrivate();
        this.issuerPublicKey = (RSAPublicKey) issuerKeyPair.getPublic();
    }

    /** True when {@code -Dxp.itest.storage=nodb} was passed to this JVM. */
    public static boolean isEnabled()
    {
        return BACKEND_NODB.equals( System.getProperty( SYSTEM_PROPERTY ) );
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

            KeyPair issuerKeyPair = DevKeys.loadOrGenerate( Files.createTempDirectory( "nodb-itest-dev-keys" ) );
            NodbServer server = NodbServer.create( 0, dataSource, (RSAPublicKey) issuerKeyPair.getPublic() );

            return new NodbTestCluster( postgres, dataSource, server, issuerKeyPair );
        }
        catch ( Exception e )
        {
            throw new IllegalStateException( "Failed to start the shared NoDB itest cluster", e );
        }
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

        return new NodbTenant( tenantId, client, new NodbNodeStore( client ), new NodbRepositoryStorageAdmin( client ) );
    }

    private void shutdown()
    {
        // NodbServer#shutdown() already closes the HikariDataSource it was constructed
        // with (same instance as this.dataSource) -- do not close it a second time here.
        server.shutdown();
        postgres.stop();
    }
}
