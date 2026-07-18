package com.enonic.nodb.bench;

import java.nio.file.Files;
import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import org.testcontainers.containers.PostgreSQLContainer;

import com.enonic.nodb.client.NodbClient;
import com.enonic.nodb.engine.TenantContext;
import com.enonic.nodb.engine.TenantProvisioner;
import com.enonic.nodb.proto.v1.CreateRepositoryRequest;
import com.enonic.nodb.server.NodbServer;
import com.enonic.nodb.server.auth.DevKeys;
import com.enonic.nodb.server.auth.JwtIssuer;
import com.enonic.nodb.server.auth.Scope;

/**
 * Bootstraps everything the bench needs to run "for real" (BUILD-SLICE-1.md step 6): a
 * postgres:17 testcontainer, a "bench" tenant provisioned into it, an RSA dev-issuer
 * keypair, a {@link NodbServer} bound to a real loopback TCP port (an ephemeral OS-assigned
 * port — same {@link NodbServer#create} the standalone binary uses, NOT the in-process
 * channel {@code NodbServerIntegrationTest} uses for gate 5 — the whole point of this
 * bench is an honest network+serialization+DB round-trip number, so it has to go over a
 * real socket), a repo+master branch, and a connected {@link NodbClient} holding a runtime
 * token. {@link #close()} tears all of it down in reverse order.
 */
final class BenchEnvironment
    implements AutoCloseable
{
    private final PostgreSQLContainer<?> postgres;

    private final HikariDataSource dataSource;

    private final NodbServer server;

    private final NodbClient client;

    private final String repoId;

    private BenchEnvironment( PostgreSQLContainer<?> postgres, HikariDataSource dataSource, NodbServer server, NodbClient client,
                               String repoId )
    {
        this.postgres = postgres;
        this.dataSource = dataSource;
        this.server = server;
        this.client = client;
        this.repoId = repoId;
    }

    static BenchEnvironment start()
        throws Exception
    {
        PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>( "postgres:17" );
        postgres.start();

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl( postgres.getJdbcUrl() );
        hikariConfig.setUsername( postgres.getUsername() );
        hikariConfig.setPassword( postgres.getPassword() );
        hikariConfig.setMaximumPoolSize( 16 );
        HikariDataSource dataSource = new HikariDataSource( hikariConfig );

        String tenantId = "bench";
        new TenantProvisioner( dataSource, postgres.getUsername() ).provision( new TenantContext( tenantId ) );

        KeyPair issuerKeyPair = DevKeys.loadOrGenerate( Files.createTempDirectory( "nodb-bench-dev-keys" ) );
        NodbServer server = NodbServer.create( 0, dataSource, (RSAPublicKey) issuerKeyPair.getPublic() );

        String operatorToken = JwtIssuer.mint( (RSAPrivateKey) issuerKeyPair.getPrivate(), (RSAPublicKey) issuerKeyPair.getPublic(),
                                                 tenantId, Scope.OPERATOR, "bench-operator", Duration.ofHours( 2 ) );
        String runtimeToken = JwtIssuer.mint( (RSAPrivateKey) issuerKeyPair.getPrivate(), (RSAPublicKey) issuerKeyPair.getPublic(),
                                                tenantId, Scope.RUNTIME, "bench-runtime", Duration.ofHours( 2 ) );

        NodbClient adminClient = NodbClient.connect( "localhost", server.getPort(), operatorToken );
        String repoId = "bench-repo";
        adminClient.createRepository( CreateRepositoryRequest.newBuilder().setRepoId( repoId ).build() );
        adminClient.close();

        NodbClient client = NodbClient.connect( "localhost", server.getPort(), runtimeToken );
        return new BenchEnvironment( postgres, dataSource, server, client, repoId );
    }

    NodbClient client()
    {
        return client;
    }

    String repoId()
    {
        return repoId;
    }

    @Override
    public void close()
    {
        client.close();
        server.shutdown();
        dataSource.close();
        postgres.stop();
    }
}
